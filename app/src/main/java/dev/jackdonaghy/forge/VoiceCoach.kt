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
        when (cue) {
            is Cue.Started -> {
                if (beeps) tone.startTone(ToneGenerator.TONE_PROP_ACK, 120)
                if (voice && ready) {
                    tts?.speak(CoachingPrompts.start(cue.step),
                        TextToSpeech.QUEUE_FLUSH, null, "exercise")
                }
            }
            is Cue.Countdown -> if (beeps) {
                tone.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
            }
            Cue.Finished -> {
                if (beeps) tone.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                if (voice && ready) {
                    tts?.speak(CoachingPrompts.completed,
                        TextToSpeech.QUEUE_FLUSH, null, "done")
                }
            }
        }
    }

    fun close() {
        tts?.stop()
        tts?.shutdown()
        tone.release()
    }
}
