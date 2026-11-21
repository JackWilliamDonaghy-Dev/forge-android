package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class CoachingPromptsTest {
    @Test fun exerciseNamesOnly() {
        val exercise = Plans.all.first().steps.first { it.phase == Phase.EXERCISE }
        assertEquals(
            "Bodyweight squats. Keep heels down and knees in line with toes.",
            CoachingPrompts.start(exercise)
        )
        assertFalse(CoachingPrompts.start(exercise).contains("Round"))
        assertFalse(CoachingPrompts.start(exercise).contains("Keep a comfortable"))
    }

    @Test fun restIsShort() {
        val rest = Plans.all.first().steps.first { it.phase == Phase.REST }
        assertEquals("Rest. Next: Incline push-ups.", CoachingPrompts.start(rest))
        assertTrue(rest.guidance.startsWith("Next: "))
    }

    @Test fun finalRestPreviewsCooldown() {
        val lastRest = Plans.all.first().steps.last { it.phase == Phase.REST }
        assertEquals("Next: Slow march.", lastRest.guidance)
    }
}
