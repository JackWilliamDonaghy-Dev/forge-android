package dev.jackdonaghy.forge

/**
 * A design signal, NOT a physiological measurement.
 *
 * The score describes the planned movement's approximate visual energy.
 * It responds to the activity, position in the workout, and time left.
 * Nothing here reads sensors, estimates heart rate, or grades a person's effort.
 */
object VisualIntensity {
    fun score(plan: Plan, snapshot: TimerSnapshot): Int {
        if (snapshot.completed) return 5
        val step = plan.steps.getOrNull(snapshot.index) ?: return 0
        val elapsed = (step.seconds * 1000L - snapshot.remainingMs)
            .coerceIn(0L, step.seconds * 1000L)
        val progress = elapsed.toFloat() / (step.seconds * 1000f)
        val durationBefore = plan.steps.take(snapshot.index).sumOf { it.seconds }
        val journey = ((durationBefore + elapsed / 1000f) / plan.durationSeconds)
            .coerceIn(0f, 1f)

        val intensity = when (step.phase) {
            Phase.WARMUP -> 9f + 19f * progress + 3f * journey
            Phase.EXERCISE -> {
                // Later work intervals feel slightly stronger, but the movement
                // determines the majority of the colour depth.
                53f + movementLoad(step.name) * 32f + journey * 8f + progress * 4f
            }
            Phase.REST -> 43f - 26f * progress + journey * 5f
            Phase.COOLDOWN -> 23f - 16f * progress
        }
        return intensity.toInt().coerceIn(0, 100)
    }

    /** Relative visual load of the named movement, not a scientific exercise score. */
    private fun movementLoad(name: String): Float = when (name) {
        "Squat jumps", "Step-back burpees", "Fast feet", "High knees", "Mountain climbers",
        "Marching high knees" -> 1.0f
        "Push-ups", "Plank shoulder taps", "Alternating lunges", "Split squats",
        "Reverse lunges", "Bodyweight squats" -> 0.76f
        "Incline push-ups", "Squat pulses", "Step-out jacks", "Glute bridge march" -> 0.56f
        "Forearm plank", "Right side plank", "Left side plank", "Glute bridges",
        "Dead bugs" -> 0.40f
        else -> 0.55f
    }
}
