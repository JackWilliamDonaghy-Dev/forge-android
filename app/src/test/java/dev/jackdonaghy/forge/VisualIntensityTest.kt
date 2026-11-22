package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class VisualIntensityTest {
    private val plan = Plan("sample","Sample","", listOf(
        Step("March in place","",10,Phase.WARMUP),
        Step("Squat jumps","",10,Phase.EXERCISE),
        Step("Rest","",10,Phase.REST),
        Step("Slow march","",10,Phase.COOLDOWN)
    ))

    private fun at(index: Int, secondsRemaining: Int) =
        TimerSnapshot(index, secondsRemaining * 1000L, true, false)

    @Test fun warmupRisesButStaysBelowExercise() {
        val beginning = VisualIntensity.score(plan, at(0,10))
        val ending = VisualIntensity.score(plan, at(0,1))
        assertTrue(beginning < ending)
        assertTrue(ending < VisualIntensity.score(plan, at(1,10)))
        assertTrue(ending < 35)
    }
    @Test fun exerciseCreatesStrongestContrast() {
        val exercise = VisualIntensity.score(plan,at(1,1))
        val rest = VisualIntensity.score(plan,at(2,10))
        assertTrue(exercise >= 80)
        assertTrue(exercise > rest)
    }
    @Test fun restFadesAndCooldownIsCalm() {
        assertTrue(VisualIntensity.score(plan,at(2,10)) > VisualIntensity.score(plan,at(2,1)))
        assertTrue(VisualIntensity.score(plan,at(3,10)) > VisualIntensity.score(plan,at(3,1)))
        assertTrue(VisualIntensity.score(plan,at(3,1)) < 25)
    }
    @Test fun neverPretendsToMeasureActualEffort() {
        plan.steps.indices.forEach { index ->
            listOf(0L,1000L,100000L).forEach { remaining ->
                val s=VisualIntensity.score(plan, TimerSnapshot(index,remaining,false,false))
                assertTrue(s in 0..100)
            }
        }
        assertEquals(5,VisualIntensity.score(plan,TimerSnapshot(3,0,false,true)))
    }
}
