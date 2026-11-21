# Forge Fitness — Native Android

Native Kotlin and Jetpack Compose interval-training app. No HTML wrapper or network permission.

## Features
- Three bodyweight presets: **Foundation** 24:20, **Build** 29:30, **Challenge** 33:48.
- Exercise/rest/warmup/cooldown timer, spoken coaching, beeps, preview, skip, previous and pause.
- Local-only session totals and no accounts or server.
- Sessions intentionally pause when the activity leaves the foreground. Use the phone unlocked for spoken coaching.

The exercise routines were reconstructed from the original requirements; **review before relying on them as the previous HTML workout**.

## Review and build
Open this repository in Android Studio (JDK 17, Android SDK 35), let Gradle sync, and run the app.
Alternatively see **Actions → Android CI → latest successful run → Artifacts → forge-debug-apk**.
Extract the artifact ZIP to get `app-debug.apk`, suitable for testing.
Use a **signed release APK** for sharing gift apps; never commit release keys.

## Known limitations
No background workouts, no custom interval editor, no detailed session history.
This PR is an initial implementation. Android device/CI build has not been verified yet.
