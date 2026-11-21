# Forge Living Interface — Lab 2: intensity as an interface

This is **not merged into main**; it remains a visual/interaction experiment.

## Core concept
The app knows whether you're warming up, training, recovering or cooling down.
Rather than displaying separate screens and buttons, it morphs a single
centre-anchored timer and the strength of the colour contrast.

- **Calm warm-up:** low colour separation, soft gradients (intensity ≈ 9–30).
- **Exercise:** stronger tonal difference and brighter dial (intensity ≈ 60–98).
- **Rest:** colour depth gradually recedes during the interval (≈ 45 → 20).
- **Cooldown:** very subdued contrast (≈ 23 → 7).
- **Flow:** smooth transitions preserve the sliding colour feel.
- **Layout:** fixed-height text regions; timer anchored centrally regardless of
  one-line or two-line exercise tips.
- **Control:** tap dial to pause/resume; swipe across the screen to next/previous.
  On-screen Previous and Next buttons removed. Screen-reader actions are
  provided for these gestures; End control remains available.
- **Home:** reduced to workout selection, demo and settings; phase screen only
  shows essential information.

The numeric intensity is a **visual design signal generated from the exercise
plan and elapsed time**, NOT measured effort, heart rate, workout difficulty
calibrated to the individual, or medical advice. No sensors or permissions.

## Test
1. Install "Forge Living Lab" APK alongside regular Forge 1.0.
2. Run 37-second demo. Watch calm warmup, high-contrast exercise,
   contrast fading during rest, and subdued cooldown.
3. Swipe between a short and long guidance line; timer must stay in place.
4. Tap the centre timer to pause and resume; swipe left/right to change steps.
5. Check compact display, settings and Reduce motion.
6. Give candid feedback on usefulness and visual hierarchy.

This is a physical-phone prototype; passing CI tests does not validate
ergonomics, contrast accessibility, or personal exercise form.
