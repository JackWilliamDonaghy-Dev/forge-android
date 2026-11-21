package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class LivingSceneTest {
    @Test fun phasesHaveDifferentScenes() {
        assertEquals(LivingScene.WARM_UP, LivingScenes.resolve(ForgePage.WORKOUT, Phase.WARMUP, false))
        assertEquals(LivingScene.MOVE, LivingScenes.resolve(ForgePage.WORKOUT, Phase.EXERCISE, false))
        assertEquals(LivingScene.RECOVER, LivingScenes.resolve(ForgePage.WORKOUT, Phase.REST, false))
        assertEquals(LivingScene.COOL_DOWN, LivingScenes.resolve(ForgePage.WORKOUT, Phase.COOLDOWN, false))
        assertEquals(LivingScene.COMPLETE, LivingScenes.resolve(ForgePage.WORKOUT, Phase.REST, true))
    }
    @Test fun otherScreensAreMapped() {
        assertEquals(LivingScene.PREPARE, LivingScenes.resolve(ForgePage.PREPARATION, null, false))
        assertEquals(LivingScene.SETTINGS, LivingScenes.resolve(ForgePage.SETTINGS, null, false))
        assertEquals(LivingScene.HOME, LivingScenes.resolve(ForgePage.HOME, null, false))
    }
    @Test fun previewExercisesAllPhasesIn37Seconds() {
        assertEquals("living-demo", LivingDemo.plan.id)
        assertEquals(37, LivingDemo.plan.durationSeconds)
        assertEquals(listOf(Phase.WARMUP, Phase.EXERCISE, Phase.REST, Phase.EXERCISE, Phase.REST, Phase.COOLDOWN), LivingDemo.plan.steps.map { it.phase })
        assertEquals(1460, Plans.all.first().durationSeconds)
        assertTrue(LivingDemo.plan.steps.filter { it.phase==Phase.EXERCISE }.all { it.guidance.isNotBlank() })
    }
}
