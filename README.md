# Forge Fitness — Native Android

Native Kotlin and Jetpack Compose interval-training app. No HTML wrapper or network permission.

## Features
- Three bodyweight presets: **Foundation** 24:20, **Build** 29:30, **Challenge** 33:48.
- Voice coaching names the next exercise during rest and gives one specific form correction at each main exercise; never repeats round numbers.
- Timer with beeps, preview, skip, previous, pause/resume without repeating prompts.
- Separate Settings screen for voice, beeps, and four persistent themes (**Ember**, **Ocean**, **Forest**, **Daylight**).
- Three full-screen, tap-anywhere preparation checkpoints for water, workout clothing, and a floor mat.
- Local-only session totals and no accounts or server.
- Sessions intentionally pause when the activity leaves the foreground. Use the phone unlocked for spoken coaching.

The exercise routines were reconstructed from the original requirements; **review before relying on them as the previous HTML workout**.

## Review and build
Open this repository in Android Studio (JDK 17, Android SDK 35), let Gradle sync, and run the app.
Alternatively see **Actions → Android CI → latest successful run → Artifacts → forge-debug-apk**.
Extract the artifact ZIP to get `app-debug.apk`, suitable for testing.

**Important update note:** GitHub's ephemeral debug signing key may be different from the one used
for a previous downloaded debug APK. In that case Android cannot install over the old app:
uninstall the previous debug app first, which clears its local workout statistics and settings.
For long-term updates, use a private, stable release-signing key kept outside the repository.
Never commit release signing keys to Git.

## Known limitations
No background workouts, no custom interval editor, no detailed session history.
Test all themes, speech and pause behaviour on a physical device before release.
