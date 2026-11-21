package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class VisualToneTest {
    private val plan = Plan(
        "timing", "Timing", "", listOf(
            Step("Warm up", "", 30, Phase.WARMUP),
            Step("Exercise", "", 40, Phase.EXERCISE),
            Step("Rest", "", 30, Phase.REST)
        )
    )

    @Test fun textEffectsStayBetween25And75Percent() {
        assertEquals(0.25f, VisualTone.textResponse(0f), 0.0001f)
        assertEquals(0.50f, VisualTone.textResponse(50f), 0.0001f)
        assertEquals(0.75f, VisualTone.textResponse(100f), 0.0001f)
        assertEquals(0.25f, VisualTone.textResponse(-300f), 0.0001f)
        assertEquals(0.75f, VisualTone.textResponse(600f), 0.0001f)
    }

    @Test fun progressIsTimeWeightedAcrossUnequalSteps() {
        assertEquals(0f, WorkoutProgress.fraction(plan, TimerSnapshot(0,30000L,true,false)), 0.001f)
        assertEquals(0.3f, WorkoutProgress.fraction(plan, TimerSnapshot(1,40000L,true,false)), 0.001f)
        assertEquals(0.5f, WorkoutProgress.fraction(plan, TimerSnapshot(1,20000L,true,false)), 0.001f)
        assertEquals(0.7f, WorkoutProgress.fraction(plan, TimerSnapshot(2,30000L,true,false)), 0.001f)
        assertEquals(1f, WorkoutProgress.fraction(plan, TimerSnapshot(2,0L,false,true)), 0.001f)
    }

    @Test fun progressRemainsWithinBounds() {
        assertEquals(0.0f, WorkoutProgress.fraction(plan, TimerSnapshot(0,500000L,false,false)),0.001f)
        assertEquals(1.0f, WorkoutProgress.fraction(plan, TimerSnapshot(2,-5000L,true,false)),0.001f)
    }
}
