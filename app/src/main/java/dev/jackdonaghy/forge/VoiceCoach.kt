package dev.jackdonaghy.forge

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceCoach(context: Context) {
    private var tts: TextToSpeech? = null
    private var ready = false
    private val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 72)

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ready = true
                tts?.language = Locale.UK
                tts?.setSpeechRate(1.02f)
            }
        }
    }
    fun play(cue: Cue, voice: Boolean, beeps: Boolean) {
        when(cue) {
            is Cue.Started -> {
                if (beeps) tone.startTone(ToneGenerator.TONE_PROP_ACK, 120)
                if (voice && ready) {
                    val phrase = when(cue.step.phase) {
                        Phase.WARMUP -> "Warm up. "
                        Phase.EXERCISE -> "Go. "
                        Phase.REST -> "Rest. "
                        Phase.COOLDOWN -> "Cool down. "
                    } + cue.step.name + ". " + cue.step.guidance
                    tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "exercise")
                }
            }
            is Cue.Countdown -> if (beeps) tone.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
            Cue.Finished -> {
                if (beeps) tone.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                if (voice && ready) tts?.speak("Workout complete. Well done.", TextToSpeech.QUEUE_FLUSH, null, "done")
            }
        }
    }
    fun close() { tts?.stop(); tts?.shutdown(); tone.release() }
}
