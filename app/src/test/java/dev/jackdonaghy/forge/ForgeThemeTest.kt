package dev.jackdonaghy.forge

import org.junit.Assert.assertEquals
import org.junit.Test

class ForgeThemeTest {
    @Test fun storedSelectionLoadsAndUnknownDefaultsToEmber() {
        assertEquals(ForgeTheme.FOREST, ForgeTheme.fromPreference("FOREST"))
        assertEquals(ForgeTheme.DAYLIGHT, ForgeTheme.fromPreference("DAYLIGHT"))
        assertEquals(ForgeTheme.EMBER, ForgeTheme.fromPreference(null))
        assertEquals(ForgeTheme.EMBER, ForgeTheme.fromPreference("UNKNOWN"))
    }
}
