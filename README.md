# Bedtime tile

A quick settings tile that switches Digital Wellbeing's **Bedtime mode** on and off with one tap, for
Android skins that ship no tile for it (ColorOS 17 has only the Do Not Disturb tile).

- Tap: toggles the real Bedtime mode, with your existing Bedtime settings.
- Long-press: opens the Bedtime mode screen in Digital Wellbeing.

## How it works

Bedtime mode is a "mode" owned by Digital Wellbeing, and Android only lets the owning app or the system
switch it. The adb shell user holds `MANAGE_NOTIFICATIONS`, which is enough. So the tile asks
[Shizuku](https://shizuku.rikka.app/) to run a small Java program as the shell user:

- [`Toggle.java`](src/dev/local/bedtimetile/Toggle.java) runs under `app_process`, finds the mode of type
  `TYPE_BEDTIME` and calls `INotificationManager.setAutomaticZenRuleState` on it.
- [`BedtimeTileService.java`](src/dev/local/bedtimetile/BedtimeTileService.java) is the tile. It flips at once
  and runs `Toggle` through Shizuku; the mode follows about half a second later.
- [`BedtimeSettingsActivity.java`](src/dev/local/bedtimetile/BedtimeSettingsActivity.java) handles the long-press.

## Requirements

- Android 15 or newer with Digital Wellbeing's Bedtime mode set up.
- Shizuku installed and running. Shizuku has to be started again after every reboot.
- ColorOS (OPPO, OnePlus): Shizuku reports "The permission of adb is limited" until the adb restriction is
  lifted in Developer options. Shizuku's guide calls the option "Permission monitoring"; on ColorOS 17 it was
  "Disable system optimisation". Restart Shizuku afterwards.

## Install

1. Install [Shizuku](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api) and start it;
   the Shizuku app walks you through starting it over wireless debugging. On ColorOS, lift the adb
   restriction first (see Requirements).
2. Download the APK from the [latest release](https://github.com/iChebbi/bedtime-tile/releases/latest) on
   the phone and open it. Android asks you to allow installs from your browser or file manager, and Play
   Protect may warn about an unknown developer.
3. Open the quick settings editor and drag in the "Bedtime mode" tile.
4. Tap the tile once and choose "Allow all the time" in the Shizuku dialog. Close the quick settings panel
   if the dialog hides behind it.

From then on a tap switches Bedtime mode and a long-press opens its settings. The app has no launcher icon;
it lives in quick settings only.

To remove it, uninstall "Bedtime tile" from Settings → Apps.

## Build from source

Needs the Android SDK (platform and build-tools 37), a JDK and `adb`. No Gradle.

```sh
./build.sh           # builds build/bedtime-tile.apk
./build.sh install   # builds and installs over adb
```

The first build downloads the Shizuku client library from Maven Central and creates a local debug keystore.
Set `KEYSTORE=/path/to/your.keystore` to sign with a key kept elsewhere (alias `debug`, password `android`).
An APK signed with a different key than the installed one only installs after uninstalling the old app.

## Limits

- Stops working when Shizuku stops; the tile then shows "Start Shizuku".
- Uses hidden platform interfaces, so a future Android version can break it.
- Tested on an OPPO Find X9 with ColorOS 17 (Android 17) only.

## Licence

MIT, see [LICENSE](LICENSE). Release APKs bundle the Shizuku client library, also MIT; its notice is in
[THIRD_PARTY_NOTICES](THIRD_PARTY_NOTICES).
