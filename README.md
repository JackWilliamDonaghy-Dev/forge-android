# Forge Fitness — Version 1.1

**Stable edition:** native, offline Android fitness companion built with Kotlin
and Jetpack Compose. Version **1.1.0** adopts the experimentally tested
**Ambient** interface and is the new stable design baseline.

## Workouts
- Three bodyweight presets: **Foundation (24:20)**, **Build (29:30)** and
  **Challenge (33:48)**, plus a short interface-preview demo.
- One practical technique reminder per exercise and an audible next-up cue
  during rest, with optional countdown beeps.
- Tap the central timer to pause/resume. Swipe to skip or go back; accessible
  alternate actions are available. Workouts pause when the app backgrounds.
- Three tap-anywhere preparation moments: water, workout clothes, floor mat.
- Session totals saved locally. Demo completion does not change workout stats.

## Ambient interface (new in 1.1)
- A single, fixed-position timer and highly readable text.
- Continuous, phase-sensitive breathing light and soft gradients that never
  restart abruptly between exercises; visual effort cues are **not** medical
  or biometric measurements.
- Full-workout progress indicator weighted by actual time.
- Four personal colour themes; **Still, Gentle, Alive** ambient motion modes;
  and Reduce motion, all chosen in a separate Settings screen.
- A minimalist interface focused on the action the person is doing now.

## Branch/release policy
- `main` is the reliable stable version 1.1.
- Experimental concepts stay on `experiment/*`; neither Timefold nor
  other unapproved extras are included in this release.
- This release retains Android application ID **`dev.jackdonaghy.forge`**,
  so it is the same app as v1 rather than a new lab installation.
- `versionCode` increased from 4 to 5 for Android update eligibility.
- CI debug APKs may be signed differently on different hosted runners.
  Android may reject installation over a prior debug build. Uninstalling a
  previous version clears its local stats/preferences. Before everyday
  releases, configure stable, protected release signing; never commit
  keystores or passwords to Git.

## Development
See [docs/APP_PLAYBOOK.md](docs/APP_PLAYBOOK.md) for our retained workflow and
device-tested design principles, and [docs/EXPERIMENTS.md](docs/EXPERIMENTS.md)
for exploration ideas.

## Limitations
- No guaranteed background timer/lock-screen service, custom interval editor,
  cloud sync or fully detailed session history.
- Exercise plans were reconstructed from the original requirements rather than
  recovered from the older HTML app.
- GitHub's automated tests verify code behavior and compilation, not
  accessibility or workout technique on a physical device.
