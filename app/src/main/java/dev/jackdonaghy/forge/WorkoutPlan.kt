package dev.jackdonaghy.forge

enum class Phase { WARMUP, EXERCISE, REST, COOLDOWN }
data class Step(val name: String, val guidance: String, val seconds: Int, val phase: Phase) {
    init { require(seconds > 0) }
}
data class Plan(val id: String, val name: String, val level: String, val steps: List<Step>) {
    val durationSeconds: Int get() = steps.sumOf { it.seconds }
    val durationLabel: String get() = "%d:%02d".format(durationSeconds / 60, durationSeconds % 60)
}

object Plans {
    private val warmup = listOf("March in place", "Arm circles", "Hip hinges", "Easy squats")
    private val cooldown = listOf("Slow march", "Quad stretch", "Calf stretch and breathe")
    private val foundation = listOf(
        "Bodyweight squats", "Incline push-ups", "Reverse lunges", "Glute bridges",
        "Marching high knees", "Forearm plank", "Dead bugs", "Step-out jacks"
    )
    private val build = listOf(
        "Bodyweight squats", "Push-ups", "Alternating lunges", "Mountain climbers",
        "Right side plank", "Left side plank", "Glute bridge march", "High knees", "Squat pulses"
    )
    private val challenge = listOf(
        "Squat jumps", "Push-ups", "Reverse lunges", "Mountain climbers",
        "Plank shoulder taps", "Fast feet", "Split squats", "Step-back burpees"
    )

    private fun create(
        id: String, label: String, level: String, exercises: List<String>,
        rounds: Int, workSeconds: Int, restSeconds: Int
    ): Plan {
        val steps = mutableListOf<Step>()
        warmup.forEach { steps += Step(it, "Move gently to warm up.", 60, Phase.WARMUP) }

        repeat(rounds) { round ->
            exercises.forEachIndexed { i, name ->
                // Round counts are for the plan structure, not spoken on every exercise.
                steps += Step(name, "Keep a comfortable, controlled pace.", workSeconds, Phase.EXERCISE)
                val upcoming = when {
                    i < exercises.lastIndex -> exercises[i + 1]
                    round < rounds - 1 -> exercises.first()
                    else -> cooldown.first()
                }
                steps += Step("Rest", "Up next: $upcoming", restSeconds, Phase.REST)
            }
        }

        cooldown.forEach { steps += Step(it, "Relax and breathe.", 60, Phase.COOLDOWN) }
        return Plan(id, label, level, steps)
    }

    val all: List<Plan> = listOf(
        create("foundation", "Foundation", "Steady", foundation, 2, 45, 20),
        create("build", "Build", "Moderate", build, 2, 55, 20),
        create("challenge", "Challenge", "Demanding", challenge, 3, 50, 17)
    )
}
