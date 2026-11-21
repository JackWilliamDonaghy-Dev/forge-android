# Forge Living Lab — Iteration 3

This branch remains **experimental**. `main` continues to be Forge 1.0.

Changes based on physical-phone review:
- **No timer jumping:** The circle never scales. Recovery instead adds a faint,
  continuously running alpha pulse to the background halo with a smoothly
  entering/leaving envelope. Swiping between steps cannot relocate the dial.
- **Full workout progress bar:** Restored beneath the timer, with progress
  weighted by actual duration rather than step count. The filled colour follows
  the workout intensity. Shows a legible completion percentage.
- **Navigation-bar clearance:** UI content respects the Android navigation
  inset, so captions and progress remain above the system gesture bar.
- **Professional typography:** Short, semibold labels replace thin
  pseudo-technical captions. The workout keeps the title higher toward the top,
  while the home page's spacious original top margin stays intact.
- **Softer preparation dimming:** Water, clothing and mat remain visually
  secondary, but first-time users have a brighter cue and description.
- **Two intensity responses:** The scene's background, timer and progress bar
  take the entire 0–100 visual response. Text accents use the narrower
  25–75% range, and main instructions/countdown stay readable at all intensities.

Try the 37-second demo, especially swipe changes during recovery, one-line
versus two-line form tips, screen bottom spacing, and Daylight theme.

Intensity still describes **planned visual energy**, not actual measured exertion.
No new phone permissions or sensors. Debug APK uses the separate
`dev.jackdonaghy.forge.living` package, and does not replace stable Forge.
