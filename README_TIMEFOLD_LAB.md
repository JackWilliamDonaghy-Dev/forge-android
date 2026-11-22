# Forge Timefold Lab — Experiment 03

Branch: `experiment/timefold`, based on the admired Forge Ambient prototype.
Stable `main` and both earlier labs stay unchanged.

## Hypothesis
A progress bar can be an **instrument for looking ahead**, not merely an
indicator. The user should never have to navigate away from the current action
just to understand what's coming later.

## How to use it
- Start the **37-second demo** or a regular workout.
- **Press and hold the whole-workout progress bar**, then drag across it.
- The existing exercise title and tip area becomes a preview of the movement
  at that future moment. The timer remains fixed and continues its countdown.
- **Lift your finger** to return to the present; nothing is skipped or edited.
- A simple tap of the bar toggles a preview of the next movement for an
  accessible alternate interaction; tap again to close it.
- The timeline marker points to the preview, while the regular fill continues
  to represent actual workout progress.
- Rest, ambient light, tone, phase changes, spoken tips, full workout presets,
  themes, and Still / Gentle / Alive options are inherited unchanged.

## Invariant
Previewing a movement MUST NOT mutate the live WorkoutTimer, skip an interval,
pause the workout, inflate session statistics or change exercise difficulty.

## Separate installation
Android app ID: `dev.jackdonaghy.forge.timefold`
Label: **Forge Timefold Lab**
The debug APK can be installed beside Forge 1.0, Living Lab and Ambient Lab.

## Test
1. During the 37-second demo hold and drag the bottom bar.
2. Read future movement names without the dial moving or pausing.
3. Release; title returns to the current exercise.
4. Tap bar once to preview the next movement; tap again to dismiss.
5. Swipe across the main surface to skip/back, verifying this still works.
6. Check short and long technique tips, themes, background behaviour and
   reduced-motion setting.

This is a gesture/accessibility experiment, not a physiological measurement.
Test reachability on real phones and Voice Access/TalkBack before production.
