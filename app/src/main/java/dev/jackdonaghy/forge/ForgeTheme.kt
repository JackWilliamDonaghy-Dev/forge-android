package dev.jackdonaghy.forge

import androidx.compose.ui.graphics.Color

enum class ForgeTheme(val title: String, val description: String) {
    EMBER("Ember", "Charcoal & orange"),
    OCEAN("Ocean", "Navy & cyan"),
    FOREST("Forest", "Green & sage"),
    DAYLIGHT("Daylight", "Ivory & blue");

    companion object {
        fun fromPreference(value: String?): ForgeTheme =
            entries.firstOrNull { it.name == value } ?: EMBER
    }
}

data class ForgePalette(
    val background: Color,
    val surface: Color,
    val accent: Color,
    val onAccent: Color,
    val text: Color,
    val muted: Color,
    val outline: Color,
    val isLight: Boolean = false
)

fun ForgeTheme.palette(): ForgePalette = when (this) {
    ForgeTheme.EMBER -> ForgePalette(
        background = Color(0xFF0D1217), surface = Color(0xFF202B34),
        accent = Color(0xFFF3A66D), onAccent = Color(0xFF151B20),
        text = Color(0xFFF5F6F6), muted = Color(0xFFBBC5CC), outline = Color(0xFF38454E)
    )
    ForgeTheme.OCEAN -> ForgePalette(
        background = Color(0xFF0C1C2C), surface = Color(0xFF19354A),
        accent = Color(0xFF66D9E8), onAccent = Color(0xFF08202B),
        text = Color(0xFFF2FBFF), muted = Color(0xFFB9D3DE), outline = Color(0xFF38576C)
    )
    ForgeTheme.FOREST -> ForgePalette(
        background = Color(0xFF10251E), surface = Color(0xFF223C31),
        accent = Color(0xFFA5D6A7), onAccent = Color(0xFF152C1A),
        text = Color(0xFFF4FAF4), muted = Color(0xFFCAD8C9), outline = Color(0xFF43604D)
    )
    ForgeTheme.DAYLIGHT -> ForgePalette(
        background = Color(0xFFF7F5EE), surface = Color(0xFFE9E6DC),
        accent = Color(0xFF315D8C), onAccent = Color.White,
        text = Color(0xFF192736), muted = Color(0xFF4B5967), outline = Color(0xFFBFC6CA),
        isLight = true
    )
}
