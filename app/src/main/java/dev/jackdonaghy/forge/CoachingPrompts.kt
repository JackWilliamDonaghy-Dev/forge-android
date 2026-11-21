package dev.jackdonaghy.forge

/** Keep audible prompts short: the screen already explains what's coming next. */
object CoachingPrompts {
    fun start(step: Step): String = when (step.phase) {
        Phase.REST -> "Rest."
        Phase.WARMUP, Phase.EXERCISE, Phase.COOLDOWN -> "${step.name}."
    }
    const val completed = "Workout complete. Well done."
}
