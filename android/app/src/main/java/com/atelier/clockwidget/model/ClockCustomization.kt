package com.atelier.clockwidget.model

import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.glance.layout.Alignment
import com.atelier.clockwidget.data.ClockPreferencesKeys

enum class ClockStyle(val id: String) {
    MINIMAL("minimal"),
    EDITORIAL("editorial"),
    DIGITAL("digital"),
    TERMINAL("terminal"),
    TYPOGRAPHIC("typographic"),
    GLASS("glass");

    companion object {
        fun fromId(id: String?): ClockStyle =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MINIMAL
    }
}

enum class WidgetSizeCategory {
    SMALL,
    MEDIUM,
    LARGE
}

data class ClockCustomization(
    val style: ClockStyle = ClockStyle.MINIMAL,
    val is24Hour: Boolean = false,
    val showSeconds: Boolean = false,
    val showDate: Boolean = true,
    val showWeekday: Boolean = true,
    val fontStyle: String = "modern-sans",
    val alignment: String = "left",
    val textSize: String = "balanced",
    val cornerRadiusDp: Int = 24,
    val backgroundOpacityPercent: Int = 40,
    val backgroundStyle: String = "glass",
    val accentColorHex: Long = 0xFFF4F4F5,
    val padding: String = "standard"
) {
    val accentColor: Color get() = Color(accentColorHex)
    val subtleTextColor: Color get() = Color(0xFF9E9E9E)

    fun resolveBackgroundColor(): Color {
        val alpha = (backgroundOpacityPercent.coerceIn(0, 100) / 100f)
        return when (backgroundStyle) {
            "solid" -> Color(0xFF18181B).copy(alpha = alpha.coerceAtLeast(0.4f))
            "glass" -> Color(0xFF27272A).copy(alpha = alpha)
            "transparent" -> Color(0x00000000)
            "outline" -> Color(0xFF09090B).copy(alpha = alpha)
            else -> Color(0xFF1C1917).copy(alpha = alpha)
        }
    }

    fun resolveGlanceAlignment(): Alignment {
        return when (alignment) {
            "center" -> Alignment.Center
            "right" -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    }

    companion object {
        fun fromPreferences(prefs: Preferences): ClockCustomization {
            val styleId = prefs[ClockPreferencesKeys.STYLE_KEY] ?: "minimal"
            val is24 = prefs[ClockPreferencesKeys.IS_24_HOUR_KEY] ?: false
            val showSec = prefs[ClockPreferencesKeys.SHOW_SECONDS_KEY] ?: false
            val showDt = prefs[ClockPreferencesKeys.SHOW_DATE_KEY] ?: true
            val showWk = prefs[ClockPreferencesKeys.SHOW_WEEKDAY_KEY] ?: true
            val font = prefs[ClockPreferencesKeys.FONT_STYLE_KEY] ?: "modern-sans"
            val align = prefs[ClockPreferencesKeys.ALIGNMENT_KEY] ?: "left"
            val size = prefs[ClockPreferencesKeys.TEXT_SIZE_KEY] ?: "balanced"
            val corner = prefs[ClockPreferencesKeys.CORNER_RADIUS_KEY] ?: 24
            val opacity = prefs[ClockPreferencesKeys.BG_OPACITY_KEY] ?: 40
            val bg = prefs[ClockPreferencesKeys.BG_STYLE_KEY] ?: "glass"
            val accent = prefs[ClockPreferencesKeys.ACCENT_COLOR_KEY] ?: 0xFFF4F4F5
            val pad = prefs[ClockPreferencesKeys.PADDING_KEY] ?: "standard"

            return ClockCustomization(
                style = ClockStyle.fromId(styleId),
                is24Hour = is24,
                showSeconds = showSec,
                showDate = showDt,
                showWeekday = showWk,
                fontStyle = font,
                alignment = align,
                textSize = size,
                cornerRadiusDp = corner,
                backgroundOpacityPercent = opacity,
                backgroundStyle = bg,
                accentColorHex = accent,
                padding = pad
            )
        }
    }
}
