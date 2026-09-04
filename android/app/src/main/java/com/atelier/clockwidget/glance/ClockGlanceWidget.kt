package com.atelier.clockwidget.glance

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.atelier.clockwidget.model.ClockCustomization
import com.atelier.clockwidget.model.ClockStyle
import com.atelier.clockwidget.model.WidgetSizeCategory
import java.text.SimpleDateFormat
import java.util.*

class ClockGlanceWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val SIZE_SMALL = DpSize(110.dp, 110.dp)   // 2x2
        val SIZE_MEDIUM = DpSize(240.dp, 110.dp)  // 4x2
        val SIZE_LARGE = DpSize(240.dp, 240.dp)   // 4x4
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            // If the widget has explicit preferences set, use them; otherwise, fall back to global saved config
            val styleKey = prefs[com.atelier.clockwidget.data.ClockPreferencesKeys.STYLE_KEY]
            val config = if (styleKey != null) {
                ClockCustomization.fromPreferences(prefs)
            } else {
                // Read from global preferences synchronously
                var loadedConfig = ClockCustomization()
                try {
                    kotlinx.coroutines.runBlocking {
                        loadedConfig = com.atelier.clockwidget.data.ClockPreferencesStore.loadDefaultConfig(context)
                    }
                } catch (e: Exception) {
                    loadedConfig = ClockCustomization()
                }
                loadedConfig
            }
            val size = LocalSize.current
            val widthVal = size.width.value
            val heightVal = size.height.value
            val sizeCategory = when {
                widthVal >= 200f && heightVal >= 200f -> WidgetSizeCategory.LARGE
                widthVal >= 200f -> WidgetSizeCategory.MEDIUM
                else -> WidgetSizeCategory.SMALL
            }

            GlanceTheme {
                ClockWidgetRoot(
                    context = context,
                    config = config,
                    sizeCategory = sizeCategory
                )
            }
        }
    }

    @Composable
    private fun ClockWidgetRoot(
        context: Context,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory
    ) {
        val now = Date()
        val openClockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val mainActivityIntent = Intent(context, com.atelier.clockwidget.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val paddingDp = when (config.padding) {
            "compact" -> 10.dp
            "generous" -> 20.dp
            else -> 14.dp
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(config.cornerRadiusDp.dp)
                .background(config.resolveBackgroundColor())
                .padding(paddingDp)
                .clickable(actionStartActivity<com.atelier.clockwidget.MainActivity>()),
            contentAlignment = config.resolveGlanceAlignment()
        ) {
            when (config.style) {
                ClockStyle.MINIMAL -> MinimalClockLayout(now, config, sizeCategory)
                ClockStyle.EDITORIAL -> EditorialClockLayout(now, config, sizeCategory)
                ClockStyle.DIGITAL -> DigitalClockLayout(now, config, sizeCategory)
                ClockStyle.TERMINAL -> TerminalClockLayout(now, config, sizeCategory)
                ClockStyle.TYPOGRAPHIC -> TypographicClockLayout(now, config, sizeCategory)
                ClockStyle.GLASS -> GlassClockLayout(now, config, sizeCategory)
            }
        }
    }

    @Composable
    private fun MinimalClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory
    ) {
        val timePattern = if (config.is24Hour) {
            if (config.showSeconds) "HH:mm:ss" else "HH:mm"
        } else {
            if (config.showSeconds) "h:mm:ss" else "h:mm"
        }
        val timeString = SimpleDateFormat(timePattern, Locale.getDefault()).format(date)
        val amPmString = if (!config.is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(date).uppercase() else ""
        val dateString = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date)

        val timeFontSize = when (sizeCategory) {
            WidgetSizeCategory.SMALL -> 32.sp
            WidgetSizeCategory.MEDIUM -> 44.sp
            WidgetSizeCategory.LARGE -> 56.sp
        }

        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Vertical.Bottom) {
                Text(
                    text = timeString,
                    style = TextStyle(
                        color = ColorProvider(config.accentColor),
                        fontSize = timeFontSize,
                        fontWeight = FontWeight.Normal
                    )
                )
                if (amPmString.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = amPmString,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            if (config.showDate && sizeCategory != WidgetSizeCategory.SMALL) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = dateString.uppercase(),
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }

    @Composable
    private fun EditorialClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory
    ) {
        val hourFormat = if (config.is24Hour) "HH" else "h"
        val hours = SimpleDateFormat(hourFormat, Locale.getDefault()).format(date)
        val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(date)
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
        val monthDay = SimpleDateFormat("MMMM d", Locale.getDefault()).format(date)

        when (sizeCategory) {
            WidgetSizeCategory.SMALL -> {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    Text(
                        text = hours,
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Text(
                        text = minutes,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }
            WidgetSizeCategory.MEDIUM -> {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = "$hours:$minutes",
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Column(horizontalAlignment = Alignment.Horizontal.End) {
                        if (config.showWeekday) {
                            Text(
                                text = dayName,
                                style = TextStyle(
                                    color = ColorProvider(config.subtleTextColor),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        if (config.showDate) {
                            Text(
                                text = monthDay,
                                style = TextStyle(
                                    color = ColorProvider(config.accentColor),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                    }
                }
            }
            WidgetSizeCategory.LARGE -> {
                Column(modifier = GlanceModifier.fillMaxSize()) {
                    if (config.showWeekday) {
                        Text(
                            text = dayName.uppercase(),
                            style = TextStyle(
                                color = ColorProvider(config.subtleTextColor),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Text(
                        text = "$hours:$minutes",
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    if (config.showDate) {
                        Text(
                            text = monthDay,
                            style = TextStyle(
                                color = ColorProvider(config.subtleTextColor),
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Serif
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun DigitalClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory
    ) {
        val timePattern = if (config.is24Hour) {
            if (config.showSeconds) "HH:mm:ss" else "HH:mm"
        } else {
            if (config.showSeconds) "hh:mm:ss" else "hh:mm"
        }
        val timeString = SimpleDateFormat(timePattern, Locale.getDefault()).format(date)
        val amPm = SimpleDateFormat("a", Locale.getDefault()).format(date)
        val dateString = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(date)

        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                Text(
                    text = timeString,
                    style = TextStyle(
                        color = ColorProvider(config.accentColor),
                        fontSize = if (sizeCategory == WidgetSizeCategory.SMALL) 28.sp else 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
                if (!config.is24Hour) {
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = amPm,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
            if (config.showDate && sizeCategory != WidgetSizeCategory.SMALL) {
                Spacer(modifier = GlanceModifier.height(6.dp))
                Text(
                    text = "[$dateString]",
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }

    @Composable
    private fun TerminalClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory
    ) {
        val timeString = SimpleDateFormat(if (config.is24Hour) "HH:mm:ss" else "hh:mm:ss a", Locale.getDefault()).format(date)
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        val zoneString = TimeZone.getDefault().id

        Column(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                text = "$ sys.clock --live",
                style = TextStyle(
                    color = ColorProvider(config.subtleTextColor),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "> $timeString",
                style = TextStyle(
                    color = ColorProvider(config.accentColor),
                    fontSize = if (sizeCategory == WidgetSizeCategory.SMALL) 20.sp else 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            if (sizeCategory != WidgetSizeCategory.SMALL) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "  locale: $zoneString",
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
                if (config.showDate) {
                    Text(
                        text = "  stamp : $dateString",
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun TypographicClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory
    ) {
        val hourFormat = if (config.is24Hour) "HH" else "h"
        val hours = SimpleDateFormat(hourFormat, Locale.getDefault()).format(date)
        val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(date)
        val dateString = SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = hours,
                style = TextStyle(
                    color = ColorProvider(config.accentColor),
                    fontSize = if (sizeCategory == WidgetSizeCategory.SMALL) 40.sp else 58.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Column {
                Text(
                    text = minutes,
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = if (sizeCategory == WidgetSizeCategory.SMALL) 26.sp else 38.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                if (config.showDate && sizeCategory != WidgetSizeCategory.SMALL) {
                    Text(
                        text = dateString,
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun GlassClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory
    ) {
        val timeFormat = if (config.is24Hour) "HH:mm" else "h:mm"
        val timeString = SimpleDateFormat(timeFormat, Locale.getDefault()).format(date)
        val amPm = if (!config.is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(date) else ""
        val dayDate = SimpleDateFormat("EEE · d MMM", Locale.getDefault()).format(date)

        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Vertical.Bottom) {
                Text(
                    text = timeString,
                    style = TextStyle(
                        color = ColorProvider(config.accentColor),
                        fontSize = if (sizeCategory == WidgetSizeCategory.SMALL) 34.sp else 46.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
                if (amPm.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = amPm,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 12.sp
                        )
                    )
                }
            }
            if (config.showDate) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = dayDate,
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}
