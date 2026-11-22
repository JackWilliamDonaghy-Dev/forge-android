# Forge Ambient Lab — Experiment 02: Living without distraction

Experimental branch: `experiment/ambient-presence`, derived from the latest
`experiment/living-interface` Lab 4; **neither changes main**.

## Hypothesis
An app can feel alive through a continuously breathing field of light, without
making the timer jump, changing text contrast dramatically, or adding controls.
Rather than animate the objects the user needs to watch, animate the atmosphere
around them.

## What's different
- A continuous **7.2 second phase oscillator** lives at the root of the
  interface. It is not restarted when you swipe to the next exercise.
- A subtle radial light field flows behind the content; a companion halo
  breathes *inside* the fixed-size timer.
- Smooth phase envelopes adjust its strength by workout phase; rest gets the
  most noticeable glow, warm-up less. Timer position and size never change.
- Primary text and technique guidance have **stable colours** independent of
  planned intensity. The progress bar and phase lighting remain responsive.
- The theme-sensitive accent has a visibility floor, so warm-up never goes
  nearly invisible.
- Settings provide **Still / Gentle / Alive** (default Alive) to compare three
  versions, alongside the existing reduced-motion switch, which disables
  ambient motion. Preferences are stored locally.
- All workouts and the 37-second preview retain their v1 timers and audio.
  This preview does not count toward workout totals.

## Parallel install
This build's package name is `dev.jackdonaghy.forge.ambient`, shown as
**Forge Ambient Lab**. It installs alongside Forge v1 and Forge Living Lab,
with its own local settings and workout totals.

## Phone review
1. Install Ambient Lab, launch the 37-second demo, tap water/clothes/mat.
2. Watch the dial's halo as you swipe between exercise, rest and cooldown:
   the pulse should **not jump or restart**.
3. Compare Still / Gentle / Alive in Settings.
4. Check title and coaching tip legibility in all four themes.
5. Test the touch-to-pause timer, swipe, full-workout progress bar, and
   real workout presets. Check reduced-motion mode.
6. Report whether the app feels alive **without demanding attention**.

The planned intensity score is a visual parameter, *not* a physiological
or heart-rate estimate. No extra permissions, internet, or accounts are needed.

This is a Kotlin/Jetpack Compose UI experiment; a successful CI build is not
a substitute for physical-phone usability and accessibility checks.
