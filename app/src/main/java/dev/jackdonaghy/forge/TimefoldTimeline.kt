package dev.jackdonaghy.forge

/** A non-destructive way to inspect any moment in the whole workout. */
data class TimefoldPeek(
    val index: Int,
    val step: Step,
    val secondsFromStart: Int,
    val planFraction: Float
) {
    val timestamp: String get() =
        "%02d:%02d".format(secondsFromStart / 60, secondsFromStart % 60)
}

object TimefoldTimeline {
    /**
     * Convert any position along a full-workout bar to its scheduled movement.
     * Does not access or modify the running timer.
     */
    fun atFraction(plan: Plan, fraction: Float): TimefoldPeek {
        require(plan.steps.isNotEmpty() && plan.durationSeconds > 0)
        val clamped = if (fraction.isNaN()) 0f else fraction.coerceIn(0f, 1f)
        val total = plan.durationSeconds
        val absolute = (total * clamped).toInt().coerceIn(0, total)
        var passed = 0
        plan.steps.forEachIndexed { i, step ->
            val end = passed + step.seconds
            if (absolute < end || i == plan.steps.lastIndex) {
                return TimefoldPeek(i, step, absolute, clamped)
            }
            passed = end
        }
        error("Unreachable: nonempty plan must contain the requested time")
    }

    /** Accessible preview target: the next active movement (or cooldown). */
    fun nextMovementFraction(plan: Plan, currentIndex: Int): Float {
        require(plan.steps.isNotEmpty() && plan.durationSeconds > 0)
        val target = (currentIndex + 1 until plan.steps.size).firstOrNull {
            val phase = plan.steps[it].phase
            phase == Phase.EXERCISE || phase == Phase.COOLDOWN
        } ?: plan.steps.lastIndex
        val previous = plan.steps.take(target).sumOf { it.seconds }.toFloat()
        return ((previous + plan.steps[target].seconds / 2f) / plan.durationSeconds)
            .coerceIn(0f, 1f)
    }
}
