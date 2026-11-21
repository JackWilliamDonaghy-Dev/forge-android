package dev.jackdonaghy.forge

/**
 * A single memorable correction per movement, kept short enough to speak
 * at the beginning of the interval. These prompts are not a substitute
 * for learning good technique or stopping when an exercise causes pain.
 */
object ExerciseTips {
    private val tips = mapOf(
        "Bodyweight squats" to "Keep heels down and knees in line with toes.",
        "Incline push-ups" to "Keep your body straight and elbows slightly tucked.",
        "Reverse lunges" to "Step back softly and keep your front heel planted.",
        "Glute bridges" to "Lift using your glutes, not your lower back.",
        "Marching high knees" to "Stand tall without leaning backwards.",
        "Forearm plank" to "Brace your core; don't let your hips sink.",
        "Dead bugs" to "Keep your lower back gently against the mat.",
        "Step-out jacks" to "Step lightly and keep your knees comfortable.",
        "Push-ups" to "Keep your body straight; don't flare your elbows wide.",
        "Alternating lunges" to "Take controlled steps with your front heel planted.",
        "Mountain climbers" to "Keep your hips steady and shoulders above wrists.",
        "Right side plank" to "Keep your hips lifted in one straight line.",
        "Left side plank" to "Keep your hips lifted in one straight line.",
        "Glute bridge march" to "Keep your pelvis level as each foot rises.",
        "High knees" to "Stand tall and land lightly.",
        "Squat pulses" to "Move with control; keep knees aligned with toes.",
        "Squat jumps" to "Land softly with bent knees.",
        "Plank shoulder taps" to "Keep hips steady as you lift each hand.",
        "Fast feet" to "Stay light on your feet and keep your chest tall.",
        "Split squats" to "Lower straight down and keep your front heel grounded.",
        "Step-back burpees" to "Step back under control and brace your core."
    )
    fun forExercise(name: String): String =
        tips[name] ?: "Move with control and use comfortable form."
}
