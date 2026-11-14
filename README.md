<div align="center">

<img src="assets/icon.svg" width="140" alt="Bedtime tile icon">

# Bedtime tile

One tap in quick settings to switch Bedtime mode on or off.<br>
For phones that ship no Bedtime tile.

[Install](#install) · [What it does](#what-it-does) · [Good to know](#good-to-know) · [Build from source](#build-from-source)

[![release](https://img.shields.io/github/v/release/iChebbi/bedtime-tile)](https://github.com/iChebbi/bedtime-tile/releases/latest)
[![downloads](https://img.shields.io/github/downloads/iChebbi/bedtime-tile/total)](https://github.com/iChebbi/bedtime-tile/releases)
![Android](https://img.shields.io/badge/Android-15%2B-3DDC84?logo=android&logoColor=white)
![tested on](https://img.shields.io/badge/tested%20on-OPPO%20ColorOS%2017-2B3175)
[![license](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

</div>

## What it does

- **Tap** the tile to switch Digital Wellbeing's Bedtime mode on or off, with the Bedtime settings you already have.
- **Long-press** the tile to open the Bedtime mode settings.

> [!NOTE]
> Tested only on an OPPO phone (Find X9) running ColorOS 17, which is Android 17. Other phones and other
> Android versions are untested.

## Install

1. Install [Shizuku](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api) and start it.
   The Shizuku app walks you through it.
2. On the phone, download the APK from the [latest release](https://github.com/iChebbi/bedtime-tile/releases/latest)
   and open it. Android asks you to allow the install, and Play Protect may warn about an unknown developer.
3. Open the quick settings editor and drag in the "Bedtime mode" tile.
4. Tap the tile once and choose "Allow all the time" in the Shizuku dialog. Close the quick settings panel
   if the dialog hides behind it.

## Good to know

- **Shizuku has to be running.** It stops whenever the phone restarts; the tile then shows "Start Shizuku"
  until you start it again in the Shizuku app.
- **ColorOS needs one extra switch.** If Shizuku says "The permission of adb is limited", turn on
  "Disable system optimisation" in Developer options and restart Shizuku.
- **The mode follows the tap by about half a second.** The tile itself flips at once.
- **There is no app icon on the home screen.** The app lives in quick settings only. To remove it, uninstall
  "Bedtime tile" from Settings → Apps.
- **A future Android update can break it**, because Android offers apps no official way to switch Bedtime mode.

## How it works

Android only lets the system or Digital Wellbeing switch Bedtime mode. Shizuku lends the tile the rights of
adb, and with those the tile asks Android to switch the mode. The code is three small files in
[`src`](src/dev/local/bedtimetile).

## Build from source

Needs the Android SDK (platform and build-tools 37), a JDK and `adb`.

```sh
./build.sh           # builds build/bedtime-tile.apk
./build.sh install   # builds and installs over adb
```

## Licence

MIT, see [LICENSE](LICENSE). Release APKs bundle the Shizuku client library, also MIT; its notice is in
[THIRD_PARTY_NOTICES](THIRD_PARTY_NOTICES).
