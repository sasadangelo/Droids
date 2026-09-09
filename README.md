# Droids

Droids is a Tetris clone for Android. The game includes a playing field consisting of a 12 × 10 grid and 7 geometric figures called "tetrominoes". The aim of the game is to fit these tetrominoes between them in order to complete lines that will be deleted. Each level has a goal (Goal) of lines to complete. Exceeded this goal, the level increases and with it the speed of tetrominoes. The ultimate goal is to be able to gain the highest score possible before the game ends. The game ends when a tetramino reaches the top edge of the playing field. The player wins points every time you complete a line or accelerates the fall of a tetromino.

This game has been created only for educational purpose, it has no claim to be a complete game distributable through the Android market. It's my belief that you can get inspiration from this source code to implement your own video games.

# Screenshots

![Main Menu](https://raw.githubusercontent.com/wiki/sasadangelo/Droids/img/Screenshot_Droids_Home.png) ![Game](https://raw.githubusercontent.com/wiki/sasadangelo/Droids/img/Screenshot_Droids.png)

# Video Demo
[![Video Demo](https://raw.githubusercontent.com/wiki/sasadangelo/Droids/img/Droids_Video.png)](https://www.youtube.com/watch?v=zvO1ws1oZQE "Video Demo")

# Limitations

Currently the game could go on forever and it is not expected that the player finish the game after a certain number of levels. The level change does not result in a change of graphics, simply at each level the game difficulty increases because the tetrominoes descend faster.

# Credits

The game framework (`org.code4projects.framework`) was written by Salvatore D'Angelo, taking inspiration from the framework presented by [Mario Zechner](https://github.com/badlogic) (@github.com/badlogic) in the book Beginning Android Games, and from the open source library libGDX. No code from those sources was copied; the architecture and class layout follow the same concepts as a learning reference.

# License

The whole project is released under the [MIT license](LICENSE).

# Related Projects

[Alien Invaders](https://github.com/sasadangelo/AlienInvaders), [Mr Snake](https://github.com/sasadangelo/MrSnake)

# Installation & Run

Enable USB Debugging mode that on many devices from 3.2 up to 4.0 (excluded) is in Settings>Applications>Development. On devices from Android 4.0 and later, you’ll find them in Settings>Developer Options. On Android 4.2 and later the option is hidden by default. To make it reappear, go to Setting->Info on the device and tap Version Build 7 times, the option will become visible.

Enable installation from Unknown Sources clicking on Settings->Security.

Download the application [clicking here](https://github.com/sasadangelo/Droids/releases/download/0.0.4/droids.apk) and install it.

# Installation & Run from source code

The project is built from the command line with Gradle and does not require Android Studio — any editor (e.g. VS Code) is enough.

## Prerequisites

- **JDK 17** (required by the Android Gradle Plugin)
- **Android SDK command-line tools**, with the following packages installed:
  - `platform-tools`
  - `platforms;android-37` (or the `compileSdk` version set in `app/build.gradle`)
  - `build-tools;37.0.0` (or the matching `buildToolsVersion`)
  - `emulator` and a system image, only if you want to run the game on a virtual device
- Git

On macOS these can be installed with [Homebrew](https://brew.sh):

```bash
brew install openjdk@17
brew install --cask android-commandlinetools

sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-37" "build-tools;37.0.0" "emulator"
```

## Get the source and configure the SDK path

```bash
git clone https://github.com/sasadangelo/Droids.git
cd Droids
echo "sdk.dir=$(brew --prefix)/share/android-commandlinetools" > local.properties
```

## Build and run

Use the provided `droids.sh` helper script:

```bash
./droids.sh            # uses a connected physical device if there is one, otherwise the emulator
./droids.sh device      # forces a connected physical device (enable USB debugging on it first)
./droids.sh emulator    # forces the emulator, starting it if it isn't already running
```

The script compiles the debug APK with `./gradlew assembleDebug`, then installs and launches Droids automatically.

If you don't have an emulator (AVD) yet, create one first, for example:

```bash
avdmanager create avd -n Pixel_3_AVD_ARM -k "system-images;android-30;google_apis;arm64-v8a" -d pixel_3
```

To run on a physical device instead, enable Developer Options (Settings > About phone > tap "Build number" 7 times), turn on USB Debugging in Settings > Developer Options, connect the phone via a USB **data** cable, and accept the "Allow USB debugging" prompt on the phone.

# Troubleshooting

- `./gradlew: Permission denied` — run `chmod +x gradlew` once.
- `adb devices` shows nothing for a physical device — the most common cause is a charge-only USB cable; try a cable known to transfer data, and check the phone's USB connection mode is set to "File Transfer" rather than "Charging only".
- Gradle/Android Gradle Plugin version mismatches — the project uses the Gradle wrapper (`./gradlew`), which downloads the exact Gradle version declared in `gradle/wrapper/gradle-wrapper.properties` automatically, so no manual Gradle installation is needed.
