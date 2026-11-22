package dev.jackdonaghy.forge

import org.junit.Assert.*
import org.junit.Test

class AmbientPhysicsTest {
    @Test fun oscillatorIsSeamlessAtCycleBoundary() {
        assertEquals(AmbientPhysics.wave(0f), AmbientPhysics.wave(1f), 0.0001f)
        assertEquals(0f, AmbientPhysics.wave(0f), 0.0001f)
        assertEquals(1f, AmbientPhysics.wave(0.5f), 0.0001f)
        assertEquals(AmbientPhysics.wave(0.25f), AmbientPhysics.wave(0.75f), 0.0001f)
    }

    @Test fun restIsMorePresentThanWarmup() {
        assertTrue(AmbientPhysics.presence(LivingScene.RECOVER) >
            AmbientPhysics.presence(LivingScene.WARM_UP))
        assertTrue(AmbientPhysics.presence(LivingScene.WARM_UP) >
            AmbientPhysics.presence(LivingScene.SETTINGS))
    }

    @Test fun accentAlwaysHasReadableBaseVisibility() {
        assertEquals(0.48f, AmbientPhysics.accentDepth(0f), 0.0001f)
        assertEquals(0.72f, AmbientPhysics.accentDepth(50f), 0.0001f)
        assertEquals(0.96f, AmbientPhysics.accentDepth(100f), 0.0001f)
    }

    @Test fun ambientStrengthModesIncludeOffAndTwoOptions() {
        assertEquals(0f, AmbientStyle.STILL.strength, 0.0001f)
        assertTrue(AmbientStyle.GENTLE.strength in 0.1f..0.9f)
        assertEquals(1f, AmbientStyle.ALIVE.strength, 0.0001f)
        assertEquals(AmbientStyle.ALIVE, AmbientStyle.fromPreference(null))
        assertEquals(AmbientStyle.GENTLE, AmbientStyle.fromPreference("GENTLE"))
    }

    @Test fun glowIsBoundedAndOffMeansOff() {
        for (intensity in listOf(-100f, 0f, 40f, 100f, 900f)) {
            for (wave in listOf(0f, 0.4f, 1f)) {
                assertEquals(0f, AmbientPhysics.glowOpacity(intensity, 0f, wave), 0.0001f)
                assertEquals(0f, AmbientPhysics.haloOpacity(0f, wave), 0.0001f)
                assertTrue(AmbientPhysics.glowOpacity(intensity, 1f, wave) in 0f..0.16f)
                assertTrue(AmbientPhysics.haloOpacity(1f, wave) in 0f..0.2f)
            }
        }
    }
}
