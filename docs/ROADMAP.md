# Droids — Roadmap to a Competitive Tetris, Then Google Play Store

This document tracks what is missing to turn Droids from "a working Kotlin Tetris clone" into a
game that feels like a finished, competitive product — and only after that, what's needed to
publish it on Google Play.

It is based on a direct inspection of the current codebase (as of the Java→Kotlin migration,
tag `0.0.5`), not on generic assumptions. Each item explains *why* it matters, not just *what*
to do.

Written in English to match the rest of the project's documentation (`README.md`, code comments).

**Status (0.2.0, the second MVP):** Phases 1–3 are complete — the game is finished as a game,
with a modern look on every screen. What's left is Phase 4 (getting it onto Google Play) and the
optional Phase 5.

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
- ~~**Fill the whole screen (no letterbox bars).**~~ **Done.** The aspect fix above left black
  bars above and below the 2:3 frame buffer on every modern phone. The frame buffer is now as
  tall as the display's aspect ratio needs (e.g. 640×1422 on 20:9) while screens keep laying
  out on the same centered 640×960 area, so no screen coordinates changed. `Graphics` exposes
  the extra space via `getVisibleTop()`/`getVisibleBottom()` and a `drawBackground()` that
  centers a background and stretches its edge rows to the screen edges; transitions cover the
  whole visible area; touch is mapped into layout coordinates; the window extends into display
  cutouts. Tablets (wider than 2:3) are still pillarboxed. The splash background is generated
  taller (640×1520) instead of being stretched. Screens don't yet *use* the extra space for
  content — that comes with the HUD/menu redesign.
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
  between any two screens, wired into every screen switch. Later generalized into the
  `framework.transition` package: a `TransitionScreen` base class (timing, easing via the new
  `Interpolation` curves, hand-off, back-press swallowing) with generic `FadeTransition`,
  `SlideTransition` and `TileWipeTransition` subclasses. The Droids-specific part lives in the
  game: `BlockWipeTransition` (glossy blocks popping in/out over a `TileWipeTransition`) and
  `Transitions`, the one place deciding which transition each kind of navigation uses (slide
  for menu pages, block wipe into a game, fade out of a game). `Graphics` gained the primitives
  these need: alpha/scale/rotation pixmap drawing, rounded rects and save/translate/rotate/
  restore.
- ~~**Splash screen.**~~ **Done.** The game used to open straight on the menu. A generic
  `SplashScreen` in the framework owns timing and tap-to-skip; `DroidsSplashScreen` draws the
  "DROIDS" block logo popping in over the blue tetromino background, twinkling sparkles and the
  credits, then block-wipes to the start screen. The art is generated by
  `assetstemplate/splash/generate_splash.py`.
- ~~**New adaptive app icon.**~~ **Done.** Replaced the single flat `drawable/icon.png` with a
  proper adaptive icon (solid navy background + foreground/monochrome layers rasterized from
  `assetstemplate/icon-hdpi.svg`) covering every density bucket (`mdpi`→`xxxhdpi`), plus legacy
  square/round PNGs and `mipmap-anydpi-v26/ic_launcher(_round).xml` for modern launchers.
  Then redesigned: a "D" made of glossy rainbow blocks on a blue background with faint tetromino
  silhouettes, generated (all buckets, legacy icons and the 512px Play Store icon) by
  `assetstemplate/icon/generate_icon.py`, sharing its palette and block style with the rest of
  the generated art through `assetstemplate/artkit.py`.
- ~~**Palette refresh.**~~ **Done.** Hue-shifted the gold/brown menu text (`mainmenu.png`,
  `pausemenu.png`, `ready.png`, `gameover.png`) to cyan, matching the cyan tetromino blocks and
  the navy HUD palette instead of the dated gold clip-art look.

### Modern restyle (from 0.1.1)

0.1.1 brought a new icon, a splash screen and animated transitions in a glossy-blocks-on-blue
style, inspired by modern mobile Tetris games (original art only — no Tetris logo, trade dress
or assets). The rest of the game still has the older look; restyle it in this order, from the
foundation up, each step being its own release so the game always stays working:

1. ~~**Use the whole screen height (no letterbox bars).**~~ **Done in 0.1.2.** The foundation
   for everything else: redesigning menus and HUD first would mean redoing them afterwards.
   Touched the framework (the drawing area is no longer fixed at 640×960) and every screen.
2. ~~**Playfield.**~~ **Done in 0.1.3.** Glossy blocks like the icon's (all 14 block sprites,
   board and preview sizes), the board drawn from a new `playfield.png` (navy checkerboard,
   dot texture, glowing frame) over the background's old black rectangle, and the ghost piece
   drawn as rounded outlines in the piece's color over a faint veil (new framework primitive
   `Graphics.drawRoundRectOutline()`). Generated by `assetstemplate/playfield/
   generate_playfield.py`. The menu background still shows the old flat blocks baked into
   `startscreen.png`; that goes with the home screen redesign.
3. ~~**In-game HUD.**~~ **Done in 0.1.4.** The game screen is now laid out from the visible
   area (`GameLayout`): a HUD bar along the top (pause button, Hold, Score with Level/Goal or
   Time/Lines in Sprint, Next with both queued shapes) drawn in code by `GameHud` with the
   Lilita One font (OFL), and the board below it with cells as big as the space allows (38 on
   2:3, ~51 on 2:1, 56 on 20:9) instead of a fixed 40. Blocks and the board are rendered at 64px
   and scaled with filtering; gestures scale with the cell size. The old `gamescreen*.png`
   backgrounds (with baked-in labels) are replaced by six generated blue-tetromino backgrounds,
   one hue per level tier. Framework: `Graphics.newFont()`, `TextStyle.font` and a drop
   shadow. The pause/ready/game over overlays are still the old ones (step 5).
4. ~~**Home screen.**~~ **Done in 0.1.5.** The splash's blue background with the floating
   "DROIDS" block logo, a big glossy green Play button (Resume when a game is in progress) that
   pulses gently, and Scores/Settings/Quit as icon tiles along the bottom, laid out from the
   visible area. The panel look shared by the HUD and the home lives in `DroidsUi`; the art is
   generated by `assetstemplate/home/generate_home.py`. The old `logo.png`/`mainmenu.png` are
   gone. Also fixed a crash: Quit (from the menu, not the back button) showed its confirmation
   dialog from the render thread; `AndroidGame.confirmExit()` now posts it to the UI thread.
5. ~~**Other screens.**~~ **Done in 0.2.0 (second MVP).** Mode select, high scores and settings
   share a `MenuPage` frame (blue background, back button, title) with panels, toggles and
   sliders drawn in code; Ready, pause, game over and Sprint cleared are `GameOverlay` panels
   centered on the board with glossy buttons, and game over/cleared gained **Play again**. All
   the old bitmap UI (`startscreen.png`, `buttons.png`, `pausemenu.png`, `ready.png`,
   `gameover.png`) and the obsolete `icon-*.svg` sources are gone: every screen now uses the
   new style.

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
  block colors are untouched, since those are what tells shapes apart during play. Since 0.1.4
  the tiers use six generated blue-tetromino backgrounds (`gamebg_<hue>.png`) instead.

**Good to have, can follow after v1:**
- ~~**Hold piece.**~~ **Done.** `DroidsWorld.holdFallingShape()` stashes the falling shape (once
  per shape, via a `canHold` flag reset each time a new one starts falling) and either pulls in
  the next queued shape if hold was empty, or swaps with whatever was already held. `Shape.
  resetSpawn()` resets position/rotation so a held shape doesn't carry over wherever the player
  had moved or rotated it. Triggered by swiping up on the play field (previously unused, since
  drag/tap/swipe-down already covered move/rotate/soft-drop). Rendered in a new "Hold" slot
  above "Level" — added that label to `gamescreen.png` and all 5 level-tinted variants (matching
  "Next"/"Level" style), since the game has no letter font to draw it at runtime.
- ~~**Next-piece queue.**~~ **Done.** `DroidsWorld.nextShape` (a single `Shape?`) became
  `nextShapes` (a `List<Shape>` backed by a queue); `makeNextShapeFalling()` dequeues the front
  and enqueues one fresh random shape to keep it topped up. `DroidsWorldRenderer` draws the queue
  stacked vertically under "Next". Capped at 2, not 3: on-device testing showed the "Next" column
  only has room for two worst-case-height (the 4-block I piece) shapes before the next one
  collides with the Score panel below it — the roadmap's own 2–3 range, at the end that actually
  fits.
- ~~**Game modes.**~~ **Done.** Added `DroidsWorld.GameMode` (Marathon/Sprint/Endless) and a new
  `ModeSelectScreen` shown when starting a fresh game (resuming a paused/running game still skips
  straight to `GameScreen`, unchanged). Marathon is the existing behavior unchanged - level/speed
  ramp up forever, ends only on top-out. Sprint and Endless both freeze the level (constant fall
  speed) instead of ramping it; Sprint additionally tracks total lines cleared and a running timer,
  winning (new `DroidsWorld.GameState.Cleared`, rendered by a new `GameScreen.GameCleared` state)
  once `SPRINT_TARGET_LINES` (40) is reached, while Endless has no win condition at all - just
  relaxed, non-escalating play. No new art: the mode-select rows and the Sprint timer reuse
  `drawRect`/`drawText`, the same fallback used for the ghost piece.
- ~~**Settings screen.**~~ **Done.** Replaced the single `Settings.soundEnabled` toggle with
  separate `musicEnabled`/`sfxEnabled` flags and `musicVolume`/`sfxVolume` (0f-1f) floats, and
  added a `SettingsScreen` (reachable from the start screen's former sound-toggle button) with a
  toggle and a drag-to-set slider per channel - again drawn with `drawRect`/`drawText` rather than
  new art. The ~15 call sites that used to check `Settings.soundEnabled` directly now go through
  new `Assets.playClick()/playBitten()/playMusic()/pauseMusic()/stopMusic()` helpers that apply the
  right enabled flag and volume in one place. Fixed the Phase 3 scoped-storage bug in the same
  change (see below), since the new settings would otherwise silently fail to save exactly like
  the old ones did.

## Phase 3 — Robustness

Things that don't affect how the game looks or plays today, but will bite before or shortly after it reaches real users.

- ~~**High scores / sound setting don't actually persist on modern Android.**~~ **Done.**
  `AndroidFileIO.kt` read/wrote via `Environment.getExternalStorageDirectory()`, which scoped
  storage (Android 10+/API 29+) blocks for apps targeting a modern SDK (we target 37), so
  `Settings.load`/`Settings.save` were silently failing. Switched `AndroidFileIO` to
  `context.filesDir` (no permission needed on any supported API level) and dropped
  `WRITE_EXTERNAL_STORAGE` from the manifest. Fixed alongside the Settings screen work above,
  since the new music/SFX settings would otherwise have inherited the exact same silent failure.
- ~~**Automated tests.**~~ **Done.** The `model` package (`DroidsWorld`, `Shape`, `Block`,
  `Settings`) is now plain Kotlin with no Android dependencies. JVM unit tests cover shape
  geometry and rotation cycles, drop distance, hold/queue behavior, line clearing and scoring,
  and high-score ordering. Run them with `./gradlew test`; no emulator is required.
- ~~**Edge-to-edge / deprecated display APIs.**~~ **Done.** Replaced `FLAG_FULLSCREEN` and
  `getDefaultDisplay()` in `AndroidGame.onCreate()` with AndroidX edge-to-edge window handling,
  system-bar insets control, and `currentWindowMetrics` with a pre-API 30 fallback. The existing
  aspect-fit rendering and touch-coordinate mapping continue to use the same display bounds.

## Phase 4 — Google Play submission

Nothing here changes how the game looks or plays — it's entirely about getting a finished game
into the store. Do this last, once Phases 1–3 already produced something worth publishing.

- **Crash visibility.** Once the app is in a Google Play testing track, use Play Console's built-in
  Android vitals to monitor crashes and ANRs without changing the app. If crash reports are needed
  before Play testing or with more diagnostic detail, consider integrating Firebase Crashlytics.

- **Release signing.** No `signingConfig` for the `release` build type exists yet, and there must
  never be a keystore committed to the repo. Generate a release keystore, add a
  `signingConfigs { release { ... } }` block reading path/passwords from environment variables or
  a local, gitignored `keystore.properties`. Play App Signing (Google manages the app signing
  key, you keep an upload key) is the recommended modern setup.
- **Android App Bundle.** Play Console requires an `.aab`. AGP already produces this via
  `./gradlew bundleRelease` — becomes part of the release process once signing exists (candidate
  for a `droids-release.sh` companion to the existing `droids.sh`).
- **Versioning.** Scheme decided at 0.1.0 (the first MVP): `versionName` mirrors the git tag,
  and `versionCode` increases by 1 per tagged release (0.1.0 → `versionCode 2`). Play Console
  requires `versionCode` to strictly increase on every upload, so keep bumping it per release.
- **Store assets.** Play Console needs a 512×512 hi-res icon and a 1024×500 feature graphic
  (separate from the in-app adaptive icon done in Phase 1), plus phone screenshots (and tablet
  ones only if tablet layouts are actually tested), short + full description, category, contact
  email. The 512×512 icon already exists (`app/src/main/ic_launcher-playstore.png`, generated
  with the launcher icon); the feature graphic can be generated the same way with
  `assetstemplate/artkit.py` (blue background + "DROIDS" block logo).
- **Device/aspect-ratio testing.** At minimum one tall modern phone (20:9) and one older 16:9
  device. The layouts were checked on the emulator at 2:3, 2:1 and 20:9 (the screens adapt to the
  height); still to do on real hardware, ideally including one phone with a camera cutout.
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

- **Tablets: fill the screen horizontally too.** Screens wider than 2:3 are still pillarboxed
  (the game is a centered phone-shaped column, which works but wastes the sides). Same approach
  as the full-height fix, mirrored: a wider frame buffer, `getVisibleLeft()`/`getVisibleRight()`,
  `drawBackground()` stretching edge columns, transitions and touch covering the side bands, a
  wider generated background; then lay the HUD/menus out for the extra width.
- **Leaderboards/achievements** via Play Games Services — the current "top 5" high score list is
  local to the device only and lost on uninstall/device change.
- **Monetization** (ads or a one-time unlock) — not required to publish a free game.
- **Localization** — since 0.2.0 no UI text is baked into bitmaps any more: everything is drawn
  at runtime with `drawText()`, so this is now a translation task. It needs the English strings
  moved out of the Kotlin code (e.g. Android string resources exposed through the framework) and
  layouts that tolerate longer words, since there is no text measurement primitive yet.
