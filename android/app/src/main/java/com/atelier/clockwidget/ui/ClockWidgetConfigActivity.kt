package com.atelier.clockwidget.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.atelier.clockwidget.data.ClockPreferencesKeys
import com.atelier.clockwidget.glance.ClockGlanceWidget
import com.atelier.clockwidget.model.ClockCustomization
import kotlinx.coroutines.launch

class ClockWidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // Apply configuration and complete setup
        applyConfigurationAndFinish(ClockCustomization())
    }

    private fun applyConfigurationAndFinish(config: ClockCustomization) {
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(this@ClockWidgetConfigActivity)
                .getGlanceIdBy(appWidgetId)

            updateAppWidgetState(this@ClockWidgetConfigActivity, glanceId) { prefs ->
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

            ClockGlanceWidget().update(this@ClockWidgetConfigActivity, glanceId)

            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }
}
