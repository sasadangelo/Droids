# Droids — Roadmap to a Competitive Tetris, Then Google Play Store

This document tracks what is missing to turn Droids from "a working Kotlin Tetris clone" into a
game that feels like a finished, competitive product — and only after that, what's needed to
publish it on Google Play.

It is based on a direct inspection of the current codebase (as of the Java→Kotlin migration,
tag `0.0.5`), not on generic assumptions. Each item explains *why* it matters, not just *what*
to do.

Written in English to match the rest of the project's documentation (`README.md`, code comments).

## Priority order

The order below is deliberate: **visual polish first, gameplay depth second, robustness third,
Play Store submission last.** A store listing is worthless if the game underneath still looks
and plays like a 2016 tutorial project — fix that first. It is fine for the first official
release to skip some items in Phase 2 or 3; the goal is "feels like a real, competitive Tetris,"
not "every feature every other Tetris clone has."

1. **Phase 1 — Visual & UX polish**: make what already exists today look and feel current.
2. **Phase 2 — Gameplay depth**: the features that make it a real, competitive Tetris.
3. **Phase 3 — Robustness**: fix the things that quietly don't work, add a safety net.
4. **Phase 4 — Google Play submission**: everything specific to getting it into the store.
5. **Phase 5 — Post-launch, optional**: nice-to-haves with no launch deadline.

---

## Phase 1 — Visual & UX polish

The current art (`app/src/main/assets/*.png`) is small, low-resolution bitmaps designed for a
fixed 320×480 screen. "Nicer buttons" alone won't fix the blurriness — the rendering pipeline and
the art need to be addressed together.

- **Stop stretching the frame buffer.** `AndroidGame.kt` allocates a hardcoded 320×480 bitmap
  frame buffer (the landscape 480×320 branch is dead code — the manifest locks portrait), and
  `AndroidFastRenderView.run()` stretches it to fill the full screen via `canvas.drawBitmap`. This
  was reasonable when phones were close to 3:2; no current phone is (they're 19.5:9 to 20:9), so
  today every block and button is visibly egg-shaped instead of square on any modern device. Fix
  by computing `dstRect` to preserve aspect ratio (letterbox/pillarbox) instead of filling the
  whole clip bounds — small, contained change, but a prerequisite for any art redesign to actually
  look right.
- **Redesign the art at a higher resolution.** Buttons, HUD panels, menu backgrounds — source as
  SVG in `assetstemplate/` (already has vector originals for some assets) and export at 2x/3x the
  current pixel size, like a normal Android density-aware resource set, instead of one
  fixed-size PNG per asset.
- **Redesign the HUD layout.** Level/Goal/Score panels are currently plain flat rectangles.
- **Screen transitions.** Start → Loading → Game → Highscore currently cut instantly; even a
  simple fade/slide reads as much more finished.
- **New adaptive app icon.** Only a single flat `icon.png` exists in `drawable/`,
  `drawable-ldpi/`, `-mdpi/`, `-hdpi/` — no `-xhdpi`/`-xxhdpi`/`-xxxhdpi`, no adaptive icon
  (`foreground` + `background` + optional `monochrome` layers), no round variant.
  `assetstemplate/icon-*.svg` has sources to start from.
- **Palette refresh.** Consider a less dated color scheme for menus (`mainmenu.png`,
  `pausemenu.png`, etc.) while redoing the art anyway.

## Phase 2 — Gameplay depth (making it a real, competitive Tetris)

Right now `DroidsWorld` levels only change one number (the fall-update interval, computed from
`level` inside `Shape.needsFallUpdate()`) — the board, background and rules never change, and
several features every modern Tetris player expects are simply absent. Not all of these need to
land before v1 — treat the first group as what actually changes how the game *feels* to play, and
the second as valuable but deferrable.

**Worth having before the first release:**
- **Wall kicks.** `Shape.applyRotation()` rotates around a fixed block with no attempt to nudge
  the piece away from a wall or floor if the naive rotation would collide — near an edge, rotation
  probably just silently fails (`undoRotate()` in `GameScreen.GameRunning.update()`). This is the
  single biggest "feel" difference from a real Tetris for anyone who's played one before, and the
  most involved logic change in this phase.
- **Ghost piece.** A preview of where the falling shape will land, reusing the existing collision
  logic in `Shape`/`DroidsWorld`. Standard expectation in any modern Tetris.
- **System back button.** Confirmed there is no `onBackPressed`/`KEYCODE_BACK` handling anywhere
  in the codebase — pressing back mid-game just kills the Activity instantly, no pause/confirm,
  no state saved.
- **Visual level progression.** Change background/block palette every N levels instead of only
  speed — cheap relative to its impact on making "level 5" feel different from "level 1."

**Good to have, can follow after v1:**
- **Hold piece** — stash the current shape once per drop; new model state, moderate effort.
- **Next-piece queue** — currently shows exactly one next shape; showing 2–3 is a small
  `DroidsWorld` + `DroidsWorldRenderer` change.
- **Game modes** — e.g. Marathon (current) vs. Sprint (40 lines) vs. Endless; reuses the existing
  `DroidsWorld` state machine, mostly a win/end-condition variant.
- **Settings screen** — currently one on/off toggle for all audio (`Settings.soundEnabled`); a
  real options screen (music/SFX split, volume) is a natural companion to the Phase 1 UI redesign.

## Phase 3 — Robustness

Things that don't affect how the game looks or plays today, but will bite before or shortly after
it reaches real users.

- **High scores / sound setting don't actually persist on modern Android.**
  `AndroidFileIO.kt` reads/writes via `Environment.getExternalStorageDirectory()`. Since Android
  10 (API 29), scoped storage blocks this for apps targeting a modern SDK (we target 37); the
  `WRITE_EXTERNAL_STORAGE` permission itself is capped at `maxSdkVersion="18"` on modern targets,
  so it's granted but does nothing. Because `Settings.load`/`Settings.save` swallow all
  exceptions, this fails completely silently — on any device newer than \~2020, the sound
  preference and high scores are very likely never actually saved. Fix by switching
  `AndroidFileIO` to `context.getFilesDir()` or `context.getExternalFilesDir(null)` (neither needs
  a permission on modern SDKs), then drop `WRITE_EXTERNAL_STORAGE` from the manifest entirely.
- **Automated tests.** The `model` package (`DroidsWorld`, `Shape`, `Block`, `Settings`) is now
  plain Kotlin with no Android dependencies — realistic to add JVM unit tests for line clearing,
  scoring, rotation and level-up logic without an emulator. Currently zero tests exist, and this
  package is exactly the kind of logic that regresses silently when Phase 2 changes rotation/
  scoring behavior.
- **Crash visibility.** Nothing wired up today. Minimum viable option costs no code changes: Play
  Console's built-in Android vitals once the app is in any testing track. A dedicated SDK
  (Firebase Crashlytics) is a step up if needed later.
- **Edge-to-edge / deprecated display APIs.** The build already warns about this:
  `FLAG_FULLSCREEN`, `getDefaultDisplay()` and related APIs used in `AndroidGame.onCreate()` are
  deprecated in favor of edge-to-edge (`WindowCompat.setDecorFitsSystemWindows`). Not an immediate
  hard failure at API 37, but touches the same screen-sizing code as the Phase 1 aspect-ratio fix,
  so it's cheapest to do at the same time rather than revisiting that file twice.

## Phase 4 — Google Play submission

Nothing here changes how the game looks or plays — it's entirely about getting a finished game
into the store. Do this last, once Phases 1–3 already produced something worth publishing.

- **Release signing.** No `signingConfig` for the `release` build type exists yet, and there must
  never be a keystore committed to the repo. Generate a release keystore, add a
  `signingConfigs { release { ... } }` block reading path/passwords from environment variables or
  a local, gitignored `keystore.properties`. Play App Signing (Google manages the app signing
  key, you keep an upload key) is the recommended modern setup.
- **Android App Bundle.** Play Console requires an `.aab`. AGP already produces this via
  `./gradlew bundleRelease` — becomes part of the release process once signing exists (candidate
  for a `droids-release.sh` companion to the existing `droids.sh`).
- **Versioning.** `versionCode`/`versionName` in `app/build.gradle` are still `1`/`"1.0"`,
  unrelated to the git tags used so far (`0.0.1`…`0.0.5`). Play Console requires `versionCode` to
  strictly increase on every upload — decide a scheme (e.g. mirror the git tag into
  `versionName`, bump `versionCode` by 1 per upload) before the first submission.
- **Store assets.** Play Console needs a 512×512 hi-res icon and a 1024×500 feature graphic
  (separate from the in-app adaptive icon done in Phase 1), plus phone screenshots (and tablet
  ones only if tablet layouts are actually tested), short + full description, category, contact
  email.
- **Device/aspect-ratio testing.** At minimum one tall modern phone (20:9) and one older 16:9
  device; validates the Phase 1 aspect-ratio fix actually holds up.
- **Play Console account and process (calendar time, not engineering time).** One-time $25
  developer registration; content rating questionnaire (IARC); a Data Safety form (should be
  simple and honest once Phase 3's storage fix means no data leaves the device at all); a privacy
  policy page (a single static page is enough — e.g. hosted on this repo's GitHub Pages/wiki) —
  required by Play Console even for a game that collects nothing, if any permission is declared.
  New developer accounts must also run a **closed testing track with at least 12 testers for 14
  continuous days** before Google allows a production release — start this clock as early as
  Phase 4 begins, since it runs in parallel with everything else in this phase.

## Phase 5 — Post-launch, optional

No launch deadline attached to any of these.

- **Leaderboards/achievements** via Play Games Services — the current "top 5" high score list is
  local to the device only and lost on uninstall/device change.
- **Monetization** (ads or a one-time unlock) — not required to publish a free game.
- **Localization** — all UI text is currently baked into bitmap assets (`mainmenu.png` etc.
  contain rendered English text) rather than string resources, so this is a redesign of the art,
  not a translation task. Worth knowing before promising it to anyone.
