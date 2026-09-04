package com.atelier.clockwidget.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import com.atelier.clockwidget.glance.ClockGlanceWidget
import com.atelier.clockwidget.model.ClockCustomization
import com.atelier.clockwidget.model.ClockStyle
import kotlinx.coroutines.flow.first

val Context.clockDataStore by preferencesDataStore(name = "atelier_clock_user_prefs")

object ClockPreferencesStore {

    suspend fun saveDefaultConfig(context: Context, config: ClockCustomization) {
        context.clockDataStore.edit { prefs ->
            prefs[ClockPreferencesKeys.STYLE_KEY] = config.style.id
            prefs[ClockPreferencesKeys.IS_24_HOUR_KEY] = config.is24Hour
            prefs[ClockPreferencesKeys.SHOW_SECONDS_KEY] = config.showSeconds
            prefs[ClockPreferencesKeys.SHOW_DATE_KEY] = config.showDate
            prefs[ClockPreferencesKeys.SHOW_WEEKDAY_KEY] = config.showWeekday
            prefs[ClockPreferencesKeys.FONT_STYLE_KEY] = config.fontStyle
            prefs[ClockPreferencesKeys.ALIGNMENT_KEY] = config.alignment
            prefs[ClockPreferencesKeys.TEXT_SIZE_KEY] = config.textSize
            prefs[ClockPreferencesKeys.CORNER_RADIUS_KEY] = config.cornerRadiusDp
            prefs[ClockPreferencesKeys.BG_OPACITY_KEY] = config.backgroundOpacityPercent
            prefs[ClockPreferencesKeys.BG_STYLE_KEY] = config.backgroundStyle
            prefs[ClockPreferencesKeys.ACCENT_COLOR_KEY] = config.accentColorHex
            prefs[ClockPreferencesKeys.PADDING_KEY] = config.padding
        }

        // Also update all currently placed Glance widget instances on the home screen!
        try {
            val glanceManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceManager.getGlanceIds(ClockGlanceWidget::class.java)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[ClockPreferencesKeys.STYLE_KEY] = config.style.id
                    prefs[ClockPreferencesKeys.IS_24_HOUR_KEY] = config.is24Hour
                    prefs[ClockPreferencesKeys.SHOW_SECONDS_KEY] = config.showSeconds
                    prefs[ClockPreferencesKeys.SHOW_DATE_KEY] = config.showDate
                    prefs[ClockPreferencesKeys.SHOW_WEEKDAY_KEY] = config.showWeekday
                    prefs[ClockPreferencesKeys.FONT_STYLE_KEY] = config.fontStyle
                    prefs[ClockPreferencesKeys.ALIGNMENT_KEY] = config.alignment
                    prefs[ClockPreferencesKeys.TEXT_SIZE_KEY] = config.textSize
                    prefs[ClockPreferencesKeys.CORNER_RADIUS_KEY] = config.cornerRadiusDp
                    prefs[ClockPreferencesKeys.BG_OPACITY_KEY] = config.backgroundOpacityPercent
                    prefs[ClockPreferencesKeys.BG_STYLE_KEY] = config.backgroundStyle
                    prefs[ClockPreferencesKeys.ACCENT_COLOR_KEY] = config.accentColorHex
                    prefs[ClockPreferencesKeys.PADDING_KEY] = config.padding
                }
                ClockGlanceWidget().update(context, glanceId)
            }
            ClockGlanceWidget().updateAll(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadDefaultConfig(context: Context): ClockCustomization {
        return try {
            val prefs = context.clockDataStore.data.first()
            val styleId = prefs[ClockPreferencesKeys.STYLE_KEY] ?: "minimal"
            ClockCustomization(
                style = ClockStyle.fromId(styleId),
                is24Hour = prefs[ClockPreferencesKeys.IS_24_HOUR_KEY] ?: false,
                showSeconds = prefs[ClockPreferencesKeys.SHOW_SECONDS_KEY] ?: false,
                showDate = prefs[ClockPreferencesKeys.SHOW_DATE_KEY] ?: true,
                showWeekday = prefs[ClockPreferencesKeys.SHOW_WEEKDAY_KEY] ?: true,
                fontStyle = prefs[ClockPreferencesKeys.FONT_STYLE_KEY] ?: "modern-sans",
                alignment = prefs[ClockPreferencesKeys.ALIGNMENT_KEY] ?: "left",
                textSize = prefs[ClockPreferencesKeys.TEXT_SIZE_KEY] ?: "balanced",
                cornerRadiusDp = prefs[ClockPreferencesKeys.CORNER_RADIUS_KEY] ?: 24,
                backgroundOpacityPercent = prefs[ClockPreferencesKeys.BG_OPACITY_KEY] ?: 40,
                backgroundStyle = prefs[ClockPreferencesKeys.BG_STYLE_KEY] ?: "glass",
                accentColorHex = prefs[ClockPreferencesKeys.ACCENT_COLOR_KEY] ?: 0xFFF4F4F5,
                padding = prefs[ClockPreferencesKeys.PADDING_KEY] ?: "standard"
            )
        } catch (e: Exception) {
            ClockCustomization()
        }
    }
}
