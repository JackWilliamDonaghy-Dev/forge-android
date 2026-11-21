# Forge experiment lab

**Rule:** keep `main` at reviewed v1. Each experiment is independently buildable and testable.
Choose ideas based on a new way of interacting, not adding decorative menus.

## Experiment A — Screenless Coach
Branch: `experiment/screenless-coach`

**Question:** Can the whole workout be followed without reading the phone?
Explore concise voice with context-aware next-up cues, pause/skip through
large touch zones, haptics, optional lock-screen media/foreground playback.
Start with a one-workout prototype and test phone screen-off behavior.
A foreground service needs explicit Android implementation and review;
no promise of background timing until verified.

## Experiment B — Motion Gestures
Branch: `experiment/motion-gestures`

**Question:** Can one deliberate phone movement replace fiddly buttons?
Prototype gesture input (sensor-based double-tap/twist or shake while held)
with threshold tuning, obvious confirmation, and an easy off switch.
Prevent false skip/stop; never infer exercise technique from a loose phone.

## Experiment C — Living Interface
Branch: `experiment/living-interface`

**Question:** Can an app avoid its own menu structure?
Prototype a Compose interface that morphs by phase, with one dominant
large target, restrained haptics and animated transitions. Accessible
text and reduced-motion fallback are required.

## Experiment D — Adaptive Exercise Guidance (later)
Potential branch: `experiment/adaptive-coach`

**Question:** Can effort ratings change the next interval without internet or accounts?
Use simple on-device rules and one-tap feedback rather than AI by default.
Measure usability and control; avoid presenting the recommendation as medical advice.

## Experiment E — On-device form observation (research only)
Potential branch: `experiment/camera-form-lab`

Explore permission-gated on-device pose landmarks (CameraX + MediaPipe/ML Kit).
Warn about accuracy, camera placement, privacy, and computational demands.
Never claim real technique validation without tests and qualified guidance.

## Discovery
Language: Kotlin 2.4.21 stable as at November 21, 2026; 2.5.0-RC previews are
for an isolated research branch only.
New Android exploration candidates include Jetpack AppState, Glance Adaptive,
Remote Compose, and selected Gemini Nano APIs (device support varies).

Review this list periodically and archive boring experiments rather than
polluting the stable app.
