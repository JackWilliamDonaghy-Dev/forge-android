package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class ExerciseTipsTest {
    @Test fun everyMainExerciseHasSpecificFormAdvice() {
        val exercises = Plans.all.flatMap { plan ->
            plan.steps.filter { it.phase == Phase.EXERCISE }
        }
        assertTrue(exercises.isNotEmpty())
        assertTrue(exercises.all { it.guidance != "Move with control and use comfortable form." })
        assertTrue(exercises.all { !it.guidance.contains("Round") })
    }
    @Test fun warmupAndCooldownRemainShortWhenSpoken() {
        Plans.all.flatMap { it.steps }
            .filter { it.phase == Phase.WARMUP || it.phase == Phase.COOLDOWN }
            .forEach { assertEquals("${it.name}.", CoachingPrompts.start(it)) }
    }
    @Test fun preparationHasThreeOrderedSteps() {
        assertEquals(3, WorkoutPreparation.steps.size)
        assertEquals("Grab your water", WorkoutPreparation.steps[0].title)
        assertEquals("Dressed for your workout?", WorkoutPreparation.steps[1].title)
        assertEquals("Floor mat ready?", WorkoutPreparation.steps[2].title)
    }
}
