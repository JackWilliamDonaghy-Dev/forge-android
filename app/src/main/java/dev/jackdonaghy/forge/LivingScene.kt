package dev.jackdonaghy.forge

/** Different looks for each moment, all powered by the normal Forge workout engine. */
enum class LivingScene { HOME, SETTINGS, PREPARE, WARM_UP, MOVE, RECOVER, COOL_DOWN, COMPLETE }

object LivingScenes {
    fun resolve(page: ForgePage, phase: Phase?, completed: Boolean): LivingScene = when (page) {
        ForgePage.HOME -> LivingScene.HOME
        ForgePage.SETTINGS -> LivingScene.SETTINGS
        ForgePage.PREPARATION -> LivingScene.PREPARE
        ForgePage.WORKOUT -> when {
            completed -> LivingScene.COMPLETE
            phase == Phase.WARMUP -> LivingScene.WARM_UP
            phase == Phase.EXERCISE -> LivingScene.MOVE
            phase == Phase.REST -> LivingScene.RECOVER
            phase == Phase.COOLDOWN -> LivingScene.COOL_DOWN
            else -> LivingScene.HOME
        }
    }

    fun heading(scene: LivingScene): String = when (scene) {
        LivingScene.HOME -> "YOUR NEXT MOVE"
        LivingScene.SETTINGS -> "MAKE IT YOURS"
        LivingScene.PREPARE -> "PREPARE"
        LivingScene.WARM_UP -> "WAKE UP"
        LivingScene.MOVE -> "MOVE"
        LivingScene.RECOVER -> "RECOVER"
        LivingScene.COOL_DOWN -> "UNWIND"
        LivingScene.COMPLETE -> "DONE"
    }
}

/** A runnable preview (37 seconds), deliberately excluded from workout statistics. */
object LivingDemo {
    val plan = Plan("living-demo", "Living demo", "37-second preview", listOf(
        Step("March in place", "Move gently to warm up.", 5, Phase.WARMUP),
        Step("Bodyweight squats", ExerciseTips.forExercise("Bodyweight squats"), 8, Phase.EXERCISE),
        Step("Rest", "Next: Incline push-ups.", 5, Phase.REST),
        Step("Incline push-ups", ExerciseTips.forExercise("Incline push-ups"), 8, Phase.EXERCISE),
        Step("Rest", "Next: Slow march.", 5, Phase.REST),
        Step("Slow march", "Relax and breathe.", 6, Phase.COOLDOWN)
    ))
}
