package dev.jackdonaghy.forge

data class TimerSnapshot(val index: Int, val remainingMs: Long, val running: Boolean, val completed: Boolean)
sealed interface Cue {
    data class Started(val step: Step): Cue
    data class Countdown(val number: Int): Cue
    data object Finished: Cue
}
class WorkoutTimer(private val plan: Plan) {
    init { require(plan.steps.isNotEmpty()) }
    var snapshot = TimerSnapshot(0, plan.steps.first().seconds * 1000L, false, false)
        private set
    private var deadline = 0L
    private var announced = 4
    fun toggle(now: Long): List<Cue> {
        if (snapshot.completed) return emptyList()
        if (snapshot.running) {
            snapshot = snapshot.copy(remainingMs = (deadline - now).coerceAtLeast(0), running = false)
            return emptyList()
        }
        deadline = now + snapshot.remainingMs
        announced = 4
        snapshot = snapshot.copy(running = true)
        return listOf(Cue.Started(plan.steps[snapshot.index]))
    }
    fun pause(now: Long) { if (snapshot.running) toggle(now) }
    fun skip(now: Long): List<Cue> = advance(snapshot.index + 1, now, snapshot.running)
    fun previous(now: Long): List<Cue> = advance((snapshot.index - 1).coerceAtLeast(0), now, snapshot.running)
    private fun advance(index: Int, now: Long, running: Boolean): List<Cue> {
        if (snapshot.completed) return emptyList()
        if (index >= plan.steps.size) {
            snapshot = TimerSnapshot(plan.steps.lastIndex, 0, false, true)
            return listOf(Cue.Finished)
        }
        val step = plan.steps[index]
        deadline = now + step.seconds * 1000L
        announced = 4
        snapshot = TimerSnapshot(index, step.seconds * 1000L, running, false)
        return if (running) listOf(Cue.Started(step)) else emptyList()
    }
    fun tick(now: Long): List<Cue> {
        if (!snapshot.running || snapshot.completed) return emptyList()
        val cues = mutableListOf<Cue>()
        while (!snapshot.completed && now >= deadline) {
            cues += advance(snapshot.index + 1, deadline, true)
        }
        if (!snapshot.completed) {
            val remaining = (deadline - now).coerceAtLeast(0)
            snapshot = snapshot.copy(remainingMs = remaining)
            val second = ((remaining + 999) / 1000).toInt()
            if (second in 1..3 && second < announced) {
                announced = second
                cues += Cue.Countdown(second)
            }
        }
        return cues
    }
}
