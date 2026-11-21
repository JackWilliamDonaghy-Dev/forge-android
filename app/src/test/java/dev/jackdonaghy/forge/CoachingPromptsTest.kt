package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class CoachingPromptsTest {
    @Test fun exerciseNamesOnly() {
        val exercise = Plans.all.first().steps.first { it.phase == Phase.EXERCISE }
        assertEquals("Bodyweight squats.", CoachingPrompts.start(exercise))
        assertFalse(CoachingPrompts.start(exercise).contains("Round"))
    }

    @Test fun restIsShort() {
        val rest = Plans.all.first().steps.first { it.phase == Phase.REST }
        assertEquals("Rest.", CoachingPrompts.start(rest))
        assertTrue(rest.guidance.startsWith("Up next: "))
    }

    @Test fun finalRestPreviewsCooldown() {
        val lastRest = Plans.all.first().steps.last { it.phase == Phase.REST }
        assertEquals("Up next: Slow march", lastRest.guidance)
    }
}
