# Forge Fitness — Version 1.0

**Stable baseline:** offline native Android app built in Kotlin and Jetpack Compose.
The original experimentation releases (0.1–0.3) are now considered the completed v1 feature set.

## What v1 includes
- Three bodyweight workouts (Foundation, Build, Challenge) with on-device timers.
- Brief voice coaching: next movement during rest; one practical technique reminder for each main exercise.
- Pause/resume, skips, countdown beeps, local session totals.
- Separate Settings screen, four persistent themes, and tap-anywhere pre-workout checklist
  (water, workout clothing, floor mat).
- Android GitHub Actions unit tests and an unsigned-in-the-release-sense **debug APK** artifact.

## Development and releases
- `main` is the stable, reviewed v1 baseline.
- Make experiments on branches named `experiment/<idea>`. Never force-merge an experiment.
- Ask for a concrete APK and test on a real device; green CI proves compilation/tests,
  not usability, sensor accuracy, or phone-specific behavior.
- Keep the same Android applicationId for in-place Forge updates.
- For updates, raise `versionCode` each time; `versionName` is user-visible.
- CI debug APKs may have different signing keys on each hosted runner: installing a
  new debug build over the old one can fail; uninstalling loses local preferences/stats.
  Before long-term release, set up **stable release signing** and keep the keystore secret.
- Store no private birthday content or personal photos in public repositories.

See [docs/APP_PLAYBOOK.md](docs/APP_PLAYBOOK.md) and
[docs/EXPERIMENTS.md](docs/EXPERIMENTS.md).

## Known limitations
- Workouts pause in the background; no lock-screen workout service yet.
- No customizable intervals or full workout history yet.
- Camera and sensors are not part of v1.
- Exact original HTML routines weren't recovered: the routines were reconstructed and
  reviewed through iterations rather than translated exactly.
