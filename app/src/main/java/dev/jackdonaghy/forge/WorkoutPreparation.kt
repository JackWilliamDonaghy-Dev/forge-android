package dev.jackdonaghy.forge

/** Checklist shown between choosing a workout and starting its timer. */
data class PreparationStep(
    val title: String,
    val description: String,
    val symbol: String
)
object WorkoutPreparation {
    val steps = listOf(
        PreparationStep(
            "Grab your water",
            "Have a bottle or glass nearby for breaks.",
            "💧"
        ),
        PreparationStep(
            "Dressed for your workout?",
            "Change into clothes you can move comfortably in.",
            "👟"
        ),
        PreparationStep(
            "Floor mat ready?",
            "Set it out for planks and floor exercises.",
            "▤"
        )
    )
}
