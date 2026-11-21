package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class WorkoutTimerTest {
    private val plan = Plan("test", "Test", "Test", listOf(
        Step("A", "", 5, Phase.EXERCISE), Step("B", "", 3, Phase.REST)))

    @Test fun durations() {
        assertEquals(1460, Plans.all[0].durationSeconds)
        assertEquals(1770, Plans.all[1].durationSeconds)
        assertEquals(2028, Plans.all[2].durationSeconds)
    }

    @Test fun countdownAndCompletion() {
        val timer = WorkoutTimer(plan)
        assertTrue(timer.toggle(100L).first() is Cue.Started)
        assertEquals(1, timer.tick(4100L).filterIsInstance<Cue.Countdown>().first().number)
        timer.tick(5100L)
        assertEquals(1, timer.snapshot.index)
        assertTrue(timer.tick(8100L).any { it is Cue.Finished })
        assertTrue(timer.snapshot.completed)
    }

    @Test fun pauseDoesNotConsumeTime() {
        val timer = WorkoutTimer(plan)
        timer.toggle(100)
        timer.pause(1100)
        assertEquals(4000, timer.snapshot.remainingMs)
        timer.tick(100000)
        assertEquals(4000, timer.snapshot.remainingMs)
        assertTrue("Resume shouldn't repeat coaching", timer.toggle(100000).isEmpty())
        timer.tick(101000)
        assertEquals(3000, timer.snapshot.remainingMs)
    }

    @Test fun skippingWhilePausedAnnouncesNewStepOnResume() {
        val timer = WorkoutTimer(plan)
        timer.toggle(0)
        timer.pause(1000)
        assertTrue(timer.skip(1500).isEmpty())
        val cues = timer.toggle(2000)
        assertEquals("B", (cues.single() as Cue.Started).step.name)
    }
}
