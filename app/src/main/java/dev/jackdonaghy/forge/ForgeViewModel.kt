package dev.jackdonaghy.forge

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ForgeState(
    val plan: Plan? = null,
    val timer: TimerSnapshot? = null,
    val speak: Boolean = true,
    val beep: Boolean = true,
    val theme: ForgeTheme = ForgeTheme.EMBER,
    val sessions: Int = 0,
    val minutes: Int = 0
)

class ForgeViewModel(application: Application): AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("forge", 0)
    private val mutable = MutableStateFlow(ForgeState(
        speak = prefs.getBoolean("speak", true),
        beep = prefs.getBoolean("beep", true),
        theme = ForgeTheme.fromPreference(prefs.getString("theme", null)),
        sessions = prefs.getInt("sessions", 0),
        minutes = prefs.getInt("minutes", 0)
    ))
    val state = mutable.asStateFlow()
    private val outgoing = MutableSharedFlow<Cue>(extraBufferCapacity = 64)
    val cues = outgoing.asSharedFlow()
    private var timer: WorkoutTimer? = null
    private var savedCompletion = false

    init {
        viewModelScope.launch {
            while (isActive) {
                timer?.let {
                    if (it.snapshot.running) {
                        dispatch(it.tick(SystemClock.elapsedRealtime()))
                        publish()
                    }
                }
                delay(100)
            }
        }
    }

    private fun dispatch(cues: List<Cue>) { cues.forEach { outgoing.tryEmit(it) } }

    private fun publish() {
        val current = timer ?: return
        mutable.value = mutable.value.copy(timer = current.snapshot)
        if (current.snapshot.completed && !savedCompletion) {
            savedCompletion = true
            val newSessions = mutable.value.sessions + 1
            val newMinutes = mutable.value.minutes +
                (mutable.value.plan!!.durationSeconds + 30) / 60
            prefs.edit().putInt("sessions", newSessions)
                .putInt("minutes", newMinutes).apply()
            mutable.value = mutable.value.copy(sessions = newSessions, minutes = newMinutes)
        }
    }

    fun start(plan: Plan) {
        timer = WorkoutTimer(plan)
        savedCompletion = false
        mutable.value = mutable.value.copy(plan = plan, timer = timer!!.snapshot)
        dispatch(timer!!.toggle(SystemClock.elapsedRealtime()))
        publish()
    }

    fun playPause() {
        timer?.let { dispatch(it.toggle(SystemClock.elapsedRealtime())); publish() }
    }
    fun pauseOnBackground() {
        timer?.pause(SystemClock.elapsedRealtime())
        publish()
    }
    fun skip() { timer?.let { dispatch(it.skip(SystemClock.elapsedRealtime())); publish() } }
    fun previous() { timer?.let { dispatch(it.previous(SystemClock.elapsedRealtime())); publish() } }
    fun leave() {
        pauseOnBackground()
        timer = null
        mutable.value = mutable.value.copy(plan = null, timer = null)
    }

    fun setSpeak(on: Boolean) {
        prefs.edit().putBoolean("speak", on).apply()
        mutable.value = mutable.value.copy(speak = on)
    }
    fun setBeep(on: Boolean) {
        prefs.edit().putBoolean("beep", on).apply()
        mutable.value = mutable.value.copy(beep = on)
    }
    fun setTheme(theme: ForgeTheme) {
        prefs.edit().putString("theme", theme.name).apply()
        mutable.value = mutable.value.copy(theme = theme)
    }
}
