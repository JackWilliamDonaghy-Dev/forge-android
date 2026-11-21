package dev.jackdonaghy.forge

/** Keep audible prompts short: the screen already explains what's coming next. */
object CoachingPrompts {
    fun start(step: Step): String = when (step.phase) {
        // The rest interval announces the next movement without lengthy coaching.
        Phase.REST -> "Rest. ${step.guidance}"
        // One short form cue is repeated when the exercise comes around again,
        // not the round number or generic motivation.
        Phase.EXERCISE -> "${step.name}. ${step.guidance}"
        Phase.WARMUP, Phase.COOLDOWN -> "${step.name}."
    }
    const val completed = "Workout complete. Well done."
}
