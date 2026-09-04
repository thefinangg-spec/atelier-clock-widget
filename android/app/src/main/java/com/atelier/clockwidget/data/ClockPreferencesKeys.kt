package com.atelier.clockwidget.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object ClockPreferencesKeys {
    val STYLE_KEY = stringPreferencesKey("clock_style")
    val IS_24_HOUR_KEY = booleanPreferencesKey("is_24_hour")
    val SHOW_SECONDS_KEY = booleanPreferencesKey("show_seconds")
    val SHOW_DATE_KEY = booleanPreferencesKey("show_date")
    val SHOW_WEEKDAY_KEY = booleanPreferencesKey("show_weekday")
    val FONT_STYLE_KEY = stringPreferencesKey("font_style")
    val ALIGNMENT_KEY = stringPreferencesKey("alignment")
    val TEXT_SIZE_KEY = stringPreferencesKey("text_size")
    val CORNER_RADIUS_KEY = intPreferencesKey("corner_radius")
    val BG_OPACITY_KEY = intPreferencesKey("bg_opacity")
    val BG_STYLE_KEY = stringPreferencesKey("bg_style")
    val ACCENT_COLOR_KEY = longPreferencesKey("accent_color")
    val PADDING_KEY = stringPreferencesKey("widget_padding")
}
