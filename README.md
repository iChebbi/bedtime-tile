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

## Build and install

Needs the Android SDK (platform and build-tools 37), a JDK and `adb`. No Gradle.

```sh
./build.sh           # builds build/bedtime-tile.apk
./build.sh install   # builds and installs over adb
```

The first build downloads the Shizuku client library from Maven Central and creates a local debug keystore.

Then add the "Bedtime mode" tile in the quick settings editor, tap it once and allow "Bedtime tile" in the
Shizuku dialog.

## Limits

- Stops working when Shizuku stops; the tile then shows "Start Shizuku".
- Uses hidden platform interfaces, so a future Android version can break it.
- Tested on an OPPO Find X9 with ColorOS 17 (Android 17) only.
