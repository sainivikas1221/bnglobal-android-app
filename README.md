# BNGlobal Android App

A WebView-wrapped Android app that mirrors https://www.bnglobal.com.au/. Launch shows a
branded loading screen immediately (even offline); once internet is available it loads the
live site. If there's no connection, it shows a "No Internet Connection" message with a
Retry button, and auto-loads as soon as connectivity returns.

## What's included
- Full Android Studio (Gradle) project under this folder
- `app/src/main/java/com/bnglobal/app/MainActivity.java` — WebView + connectivity/loading logic
- `app/src/main/res/layout/activity_main.xml` — loading screen + WebView layout
- App icon: a placeholder "BN" icon (navy circle). Your real logo (from bnglobal.com.au)
  couldn't be downloaded automatically because of a network restriction in this build
  environment — see "Swap in your real logo" below.

## Build it into an APK (no coding needed)

**Option A — Android Studio (recommended, easiest)**
1. Install [Android Studio](https://developer.android.com/studio) if you don't have it.
2. Open Android Studio → **Open** → select this `BNGlobalApp` folder.
3. Let it sync (it will auto-download the Gradle wrapper and Android SDK components it needs — first sync can take a few minutes).
4. Menu: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. When it finishes, click the **locate** link in the notification, or find the file at:
   `app/build/outputs/apk/debug/app-debug.apk`

**Option B — Command line** (if you already have the Android SDK + Gradle installed)
```
cd BNGlobalApp
gradle wrapper          # only needed once, generates gradlew
./gradlew assembleDebug
```
APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Install on an Android phone
1. Copy `app-debug.apk` to the phone (email, USB transfer, cloud drive, etc.).
2. On the phone, tap the file. If prompted, allow "Install unknown apps" for the app you used
   to open the file (Settings → apps → special access → install unknown apps).
3. Tap **Install**. Once done, tap **Open** — or find the "BNGlobal" icon on the home screen /
   app drawer and tap it like any other app.

## Swap in your real logo (optional but recommended)
Replace these files with your actual logo (keep the same filenames and dimensions per folder):
- `app/src/main/res/mipmap-mdpi/ic_launcher.png` (48x48)
- `app/src/main/res/mipmap-hdpi/ic_launcher.png` (72x72)
- `app/src/main/res/mipmap-xhdpi/ic_launcher.png` (96x96)
- `app/src/main/res/mipmap-xxhdpi/ic_launcher.png` (144x144)
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` (192x192)
- `app/src/main/res/drawable/logo_splash.png` (used on the loading screen, ~300x300 recommended)

Or, easier: in Android Studio, right-click `app/src/main/res` → **New → Image Asset**, pick your
logo file, and it will regenerate all the launcher icon sizes for you automatically.

## Publishing to the Play Store (optional)
This debug build installs fine for personal/manual distribution, but the Play Store requires a
**signed release build**. In Android Studio: **Build → Generate Signed Bundle / APK**, create a
keystore, and follow the prompts. Full guide:
https://developer.android.com/studio/publish/app-signing

## Notes
- `minSdkVersion` is set to 21 (Android 5.0+), covering virtually all active devices.
- Package name (application ID) is `com.bnglobal.app` — change it in `app/build.gradle` if you
  need something else (e.g. to publish under a different identity).
- External links on the site (e.g. Facebook/Instagram) open in the phone's normal browser;
  everything under bnglobal.com.au stays inside the app.
