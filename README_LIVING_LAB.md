# Forge Living Interface — Experiment 01

Branch: `experiment/living-interface`. This experiment is **not merged** into `main`.

## Idea
A single evolving workout surface. Instead of a succession of traditional menus and
pages, colour, ambient light, a circular clock, gesture and typography change with
the phase of the workout.

- Home: real workout presets or **Try 37-second demo**.
- Water / clothing / floor mat: tap anywhere to confirm and flow forward.
- Exercise: tap the large dial to pause/resume; horizontal swipe to skip/back
  (visible buttons provided too). One short form cue from Forge v1.
- Rest: a slowly breathing dial, less intense background, next exercise spoken.
- Cooldown: calmer colours. Completion: final scene and return action.
- Settings: persistent themes, voice, beeps, and reduce-motion switch.
- No new permissions, internet access, camera or server.

## Install
Package ID `dev.jackdonaghy.forge.living`; visible app label **Forge Living Lab**.
It installs beside standard Forge (`dev.jackdonaghy.forge`) and has separate
statistics and preferences. If a later debug APK is signed differently, Android
may require uninstalling the previous **lab** build, not the stable Forge app.

## Review in 1 minute
Start the 37-second demo → confirm 3 preparation prompts → watch the interface
transform through warmup / exercise / rest / cooldown. Tap clock to pause, swipe
left/right, test theme changes and reduced motion. The preview must not count
as a completed fitness session.

This is a test of interaction design, not a physical fitness coaching validation.
The existing background-pause behavior remains; full workouts are unchanged.
