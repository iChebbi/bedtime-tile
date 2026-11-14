#!/bin/sh
# Builds and signs build/bedtime-tile.apk with the plain SDK tools (no Gradle).
# Usage: ./build.sh [install]   ("install" needs one adb device, or ANDROID_SERIAL set)
set -e
cd "$(dirname "$0")"
SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
BT="$SDK/build-tools/37.0.0"
JAR="$SDK/platforms/android-37.0/android.jar"

rm -rf build && mkdir -p build/classes build/dex libs
# Shizuku client library (classes only), fetched from Maven Central on the first build.
for a in api provider shared aidl; do
    [ -f "libs/$a.jar" ] && continue
    curl -fsSL -o "build/$a.aar" "https://repo1.maven.org/maven2/dev/rikka/shizuku/$a/13.1.5/$a-13.1.5.aar"
    unzip -p "build/$a.aar" classes.jar > "libs/$a.jar"
done
"$BT/aapt2" compile --dir res -o build/res.zip
"$BT/aapt2" link -I "$JAR" --manifest AndroidManifest.xml -o build/unsigned.apk build/res.zip
javac --release 17 -cp "$JAR:libs/*" -d build/classes $(find src -name '*.java')
"$BT/d8" --min-api 35 --lib "$JAR" --output build/dex $(find build/classes -name '*.class') libs/*.jar
(cd build/dex && zip -q ../unsigned.apk classes.dex)
"$BT/zipalign" -f 4 build/unsigned.apk build/aligned.apk
KS="${KEYSTORE:-debug.keystore}"
[ -f "$KS" ] || keytool -genkeypair -keystore "$KS" -storepass android -keypass android \
    -alias debug -keyalg RSA -validity 10000 -dname CN=debug
"$BT/apksigner" sign --ks "$KS" --ks-pass pass:android --out build/bedtime-tile.apk build/aligned.apk
echo "built build/bedtime-tile.apk"
if [ "$1" = install ]; then adb install -r build/bedtime-tile.apk; fi
