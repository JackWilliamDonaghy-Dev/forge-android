package dev.jackdonaghy.forge

import kotlin.math.roundToInt

/** How much the user-facing design reacts to the plan's visual intensity. */
object VisualTone {
    fun normalized(intensity: Float): Float = (intensity / 100f).coerceIn(0f, 1f)

    // Typography never disappears into the background at low intensity,
    // and never becomes the most saturated thing at high intensity.
    fun textResponse(intensity: Float): Float = 0.25f + normalized(intensity) * 0.50f
}

/** Actual timeline progress, weighted by seconds rather than number of steps. */
object WorkoutProgress {
    fun fraction(plan: Plan, snapshot: TimerSnapshot): Float {
        if (snapshot.completed) return 1f
        if (plan.steps.isEmpty() || plan.durationSeconds <= 0) return 0f
        val index = snapshot.index.coerceIn(0, plan.steps.lastIndex)
        val previous = plan.steps.take(index).sumOf { it.seconds }.toFloat()
        val current = plan.steps[index]
        val completedWithin = (current.seconds - snapshot.remainingMs / 1000f)
            .coerceIn(0f, current.seconds.toFloat())
        return ((previous + completedWithin) / plan.durationSeconds).coerceIn(0f, 1f)
    }
}
