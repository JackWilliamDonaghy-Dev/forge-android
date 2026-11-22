package dev.jackdonaghy.forge

import kotlin.math.PI
import kotlin.math.cos

/**
 * How much environmental motion the user wants.
 * Main controls and text never change position or scale.
 */
enum class AmbientStyle(val label: String, val description: String, val strength: Float) {
    STILL("Still", "Keep the scene quiet", 0f),
    GENTLE("Gentle", "Barely perceptible movement", 0.52f),
    ALIVE("Alive", "Soft breathing light", 1f);

    companion object {
        fun fromPreference(value: String?): AmbientStyle =
            entries.firstOrNull { it.name == value } ?: ALIVE
    }
}

/**
 * All math in this class is pure and unit-tested. The oscillator advances
 * continuously at the root, not restarted when exercises are swiped.
 *
 * This is presentation-only — not heart-rate, measured effort, or biometric data.
 */
object AmbientPhysics {
    /** Smooth periodic wave, same value AND gradient at both ends of a cycle. */
    fun wave(cycle: Float): Float {
        val radians = (cycle.coerceIn(0f, 1f) * 2f * PI).toFloat()
        return (0.5f - 0.5f * cos(radians)).coerceIn(0f, 1f)
    }

    fun presence(scene: LivingScene): Float = when (scene) {
        LivingScene.HOME -> 0.48f
        LivingScene.PREPARE -> 0.43f
        LivingScene.WARM_UP -> 0.55f
        LivingScene.MOVE -> 0.76f
        LivingScene.RECOVER -> 1f
        LivingScene.COOL_DOWN -> 0.72f
        LivingScene.COMPLETE -> 0.58f
        LivingScene.SETTINGS -> 0.18f
    }

    fun accentDepth(intensity: Float): Float =
        (0.48f + 0.48f * (intensity / 100f).coerceIn(0f, 1f))

    /** Approximate alpha of a broad, diffuse area of light (0 means off). */
    fun glowOpacity(intensity: Float, presence: Float, wave: Float): Float {
        val p = presence.coerceIn(0f, 1f)
        val intensityDepth = (intensity / 100f).coerceIn(0f, 1f)
        return (p * (0.058f + intensityDepth * 0.09f) *
                (0.5f + wave.coerceIn(0f, 1f) * 0.5f)).coerceIn(0f, 0.16f)
    }

    /** Halo exists behind the dial; neither the dial nor the progress ring scales. */
    fun haloOpacity(presence: Float, wave: Float): Float {
        val p = presence.coerceIn(0f, 1f)
        return (p * (0.07f + wave.coerceIn(0f, 1f) * 0.10f)).coerceIn(0f, 0.2f)
    }
}
