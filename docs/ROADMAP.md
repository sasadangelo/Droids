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

- ~~**Stop stretching the frame buffer.**~~ **Done.** `AndroidGame.kt` allocated a hardcoded
  320×480 bitmap frame buffer (the landscape 480×320 branch is dead code — the manifest locks
  portrait), and `AndroidFastRenderView.run()` stretched it to fill the full screen via
  `canvas.drawBitmap`. This was reasonable when phones were close to 3:2; no current phone is
  (they're 19.5:9 to 20:9), so every block and button used to be visibly egg-shaped instead of
  square on any modern device. Fixed by computing `dstRect` to preserve aspect ratio
  (letterbox/pillarbox) instead of filling the whole clip bounds, and mapping touch coordinates
  through the same offset/scale.
- ~~**Redesign the art at a higher resolution.**~~ **Done.** Buttons, HUD panels, menu
  backgrounds — sourced as SVG in `assetstemplate/` (already had vector originals for some
  assets) and exported at 2x the previous pixel size (framebuffer doubled to 640×960 to match).
  Text-bearing assets that depend on proprietary fonts not available in this environment
  (`gameover.png`, `mainmenu.png`, `pausemenu.png`, `ready.png`) were instead upscaled from the
  existing bitmaps with high-quality (Lanczos) filtering rather than re-rendered from SVG, to
  avoid baking in a wrong fallback font. Flat-color block sprites (no vector source) were
  upscaled with nearest-neighbor filtering to keep their edges crisp. 3x/density-aware buckets
  were deliberately left out of scope — see rationale in code review / commit history.
- ~~**Redesign the HUD layout.**~~ **Done.** Level/Goal/Score panels used to be plain flat
  rectangles drawn in code; they were rounded, gradient-filled panels baked into the new
  `gamescreen.png`/`startscreen.png` artwork as part of the previous item, so no separate
  `drawRect`-based HUD code remained to update.
- ~~**Screen transitions.**~~ **Done.** Start → Loading → Game → Highscore used to cut instantly;
  added a reusable `FadeTransitionScreen` in the framework layer that cross-fades through black
  between any two screens, wired into every screen switch.
- ~~**New adaptive app icon.**~~ **Done.** Replaced the single flat `drawable/icon.png` with a
  proper adaptive icon (solid navy background + foreground/monochrome layers rasterized from
  `assetstemplate/icon-hdpi.svg`) covering every density bucket (`mdpi`→`xxxhdpi`), plus legacy
  square/round PNGs and `mipmap-anydpi-v26/ic_launcher(_round).xml` for modern launchers.
- ~~**Palette refresh.**~~ **Done.** Hue-shifted the gold/brown menu text (`mainmenu.png`,
  `pausemenu.png`, `ready.png`, `gameover.png`) to cyan, matching the cyan tetromino blocks and
  the navy HUD palette instead of the dated gold clip-art look.

## Phase 2 — Gameplay depth (making it a real, competitive Tetris)

Right now `DroidsWorld` levels only change one number (the fall-update interval, computed from
`level` inside `Shape.needsFallUpdate()`) — the board, background and rules never change, and
several features every modern Tetris player expects are simply absent. Not all of these need to
land before v1 — treat the first group as what actually changes how the game *feels* to play, and
the second as valuable but deferrable.

**Worth having before the first release:**
- ~~**Wall kicks.**~~ **Done.** `Shape.applyRotation()` still rotates around a fixed block with no
  awareness of walls, floor or other pieces, but rotation no longer just fails near an edge:
  `Shape.rotateWithWallKick()` retries a short list of offsets (±1 and ±2 columns, then one row up)
  after a colliding rotation, keeping the first one that clears, and only falls back to
  `undoRotate()` if none of them do. `GameScreen.GameRunning.update()`'s rotate handler now calls
  this instead of the old rotate/collide/undoRotate sequence. Not full SRS (no per-shape,
  per-transition kick tables) — a simpler generic offset list, since this project's shapes don't
  use SRS rotation states to begin with.
- ~~**Ghost piece.**~~ **Done.** `Shape.dropDistance()` probes downward from the falling shape's
  current position using the same `collide()` logic the real fall relies on, then fully restores
  the shape's position — no new collision rules, no score side effects. `DroidsWorldRenderer` draws
  a translucent copy of the falling shape's blocks offset by that distance (skipped when the piece
  is already resting), reusing the existing `drawRect` primitive rather than adding new art assets.
- ~~**System back button.**~~ **Done.** Added `Screen.backPressed()` to the framework interface and
  implemented it per screen: `GameScreen` pauses on back if running/ready, and goes home from the
  pause or game-over state (matching the existing pause/home/X buttons exactly, so game state is
  saved the same way pausing already saves it); `HighscoreScreen` goes home like its own back
  button; `StartScreen` now asks for confirmation before exiting via a new `Game.confirmExit()`
  (also wired into the previously-unconfirmed Quit menu item); `LoadingScreen` and
  `FadeTransitionScreen` fall back to default/no-op. `AndroidGame.onBackPressed()` just delegates
  to the current screen. Uses the deprecated `Activity.onBackPressed()` rather than
  `OnBackPressedCallback`, since the project has no AndroidX dependency to unlock it.
- ~~**Visual level progression.**~~ **Done.** Added five hue-shifted variants of `gamescreen.png`
  (`gamescreen_purple/teal/amber/crimson/olive.png`, generated from the original via a hue
  rotation plus a flat color blend so even the near-black playfield picks up a visible tint, not
  just the border gradient). `GameScreen` cycles through the original plus these five every
  `LEVELS_PER_BACKGROUND` (3) levels rather than growing unbounded with level. An earlier attempt
  at a runtime semi-transparent overlay instead of real art was tried and dropped — the effect was
  too subtle to read as "different," especially over the mostly-black playfield. Falling/settled
  block colors are untouched, since those are what tells shapes apart during play.

**Good to have, can follow after v1:**
- **Hold piece** — stash the current shape once per drop; new model state, moderate effort.
- ~~**Next-piece queue.**~~ **Done.** `DroidsWorld.nextShape` (a single `Shape?`) became
  `nextShapes` (a `List<Shape>` backed by a queue); `makeNextShapeFalling()` dequeues the front
  and enqueues one fresh random shape to keep it topped up. `DroidsWorldRenderer` draws the queue
  stacked vertically under "Next". Capped at 2, not 3: on-device testing showed the "Next" column
  only has room for two worst-case-height (the 4-block I piece) shapes before the next one
  collides with the Score panel below it — the roadmap's own 2–3 range, at the end that actually
  fits.
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
