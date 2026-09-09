#!/usr/bin/env bash
# Build and run Droids.
#
# Usage:
#   ./droids.sh              Build, then run on a connected physical device
#                             if there is one, otherwise on the emulator.
#   ./droids.sh device       Force running on a connected physical device.
#   ./droids.sh emulator     Force running on the Pixel_3_AVD_ARM emulator
#                             (starts it if it isn't already running).
set -euo pipefail

export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

AVD_NAME="Pixel_3_AVD_ARM"
APP_ID="org.code4projects.droids"
MAIN_ACTIVITY="$APP_ID/.view.DroidsGame"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
MODE="${1:-auto}"

cd "$(dirname "$0")"

echo "==> Compilazione APK debug"
./gradlew assembleDebug

has_physical_device() {
    adb devices | awk 'NR>1 && $2=="device"' | grep -qv emulator
}

case "$MODE" in
    device)
        echo "==> Attendo un dispositivo fisico collegato (adb)..."
        adb wait-for-device
        ;;
    emulator)
        if ! adb devices | grep -q emulator; then
            echo "==> Avvio emulatore $AVD_NAME"
            nohup emulator -avd "$AVD_NAME" -no-snapshot -no-boot-anim > /tmp/droids-emulator.log 2>&1 &
            disown
        fi
        adb wait-for-device
        echo "==> Attendo il completamento del boot..."
        until [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
            sleep 2
        done
        ;;
    auto)
        if has_physical_device; then
            echo "==> Uso il dispositivo fisico collegato"
        else
            exec "$0" emulator
        fi
        ;;
    *)
        echo "Uso: $0 [device|emulator]" >&2
        exit 1
        ;;
esac

echo "==> Installo l'APK"
adb install -r "$APK_PATH"

echo "==> Avvio Droids"
adb shell am start -n "$MAIN_ACTIVITY"
