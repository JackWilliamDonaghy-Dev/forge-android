package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class TimefoldTimelineTest {
    private val plan = Plan("preview", "Preview", "", listOf(
        Step("Warm up", "", 10, Phase.WARMUP),
        Step("Push-ups", "Straight body.", 30, Phase.EXERCISE),
        Step("Rest", "Next: Squats.", 20, Phase.REST),
        Step("Squats", "Heels down.", 40, Phase.EXERCISE)
    ))

    @Test fun timelineUsesDurationNotStepCount() {
        assertEquals(0, TimefoldTimeline.atFraction(plan, 0f).index)
        assertEquals(1, TimefoldTimeline.atFraction(plan, 0.10f).index)
        assertEquals(1, TimefoldTimeline.atFraction(plan, 0.39f).index)
        assertEquals(2, TimefoldTimeline.atFraction(plan, 0.40f).index)
        assertEquals(3, TimefoldTimeline.atFraction(plan, 0.60f).index)
        assertEquals(3, TimefoldTimeline.atFraction(plan, 1f).index)
    }

    @Test fun timestampsAndBoundariesAreSafe() {
        assertEquals("00:00", TimefoldTimeline.atFraction(plan, -10f).timestamp)
        assertEquals("00:50", TimefoldTimeline.atFraction(plan, 0.50f).timestamp)
        assertEquals("01:40", TimefoldTimeline.atFraction(plan, 10f).timestamp)
        assertEquals(0, TimefoldTimeline.atFraction(plan, Float.NaN).index)
    }

    @Test fun accessiblePreviewSkipsRest() {
        assertEquals(3, TimefoldTimeline.atFraction(
            plan, TimefoldTimeline.nextMovementFraction(plan, 1)
        ).index)
        assertEquals(1, TimefoldTimeline.atFraction(
            plan, TimefoldTimeline.nextMovementFraction(plan, 0)
        ).index)
    }

    @Test fun previewNeverChangesTheWorkoutTimer() {
        val timer = WorkoutTimer(plan)
        timer.toggle(100L)
        val before = timer.snapshot
        repeat(100) { n ->
            TimefoldTimeline.atFraction(plan, n / 99f)
        }
        assertEquals(before, timer.snapshot)
    }
}
