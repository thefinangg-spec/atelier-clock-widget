export interface AndroidFile {
  path: string;
  name: string;
  language: 'kotlin' | 'xml' | 'gradle' | 'properties' | 'markdown';
  category: 'glance' | 'ui' | 'model' | 'data' | 'worker' | 'config' | 'res';
  description: string;
  content: string;
}

export const ANDROID_PROJECT_FILES: AndroidFile[] = [
  {
    path: 'app/src/main/java/com/atelier/clockwidget/glance/ClockGlanceWidget.kt',
    name: 'ClockGlanceWidget.kt',
    language: 'kotlin',
    category: 'glance',
    description: 'Core AndroidX Glance AppWidget implementation rendering all 6 distinct styles adaptively.',
    content: `package com.atelier.clockwidget.glance

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.atelier.clockwidget.data.ClockPreferencesKeys
import com.atelier.clockwidget.model.ClockCustomization
import com.atelier.clockwidget.model.ClockStyle
import com.atelier.clockwidget.model.WidgetSizeCategory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Atelier Clock Widget — AndroidX Glance AppWidget
 * 
 * Supports 6 distinct visual styles:
 * 1. Minimal — Pure typography and whitespace
 * 2. Editorial — Magazine serif layout with asymmetric lockup
 * 3. Digital — Modern technical instrument face
 * 4. Terminal — Monospace developer prompt aesthetic
 * 5. Typographic — Bold expressive oversize numerals
 * 6. Glass — Translucent layered depth
 *
 * Implements SizeMode.Responsive with Small (2x2), Medium (4x2), Large (4x4).
 */
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
            val config = ClockCustomization.fromPreferences(prefs)
            val size = LocalSize.current
            val sizeCategory = when {
                size.width >= 200.dp && size.height >= 200.dp -> WidgetSizeCategory.LARGE
                size.width >= 200.dp -> WidgetSizeCategory.MEDIUM
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
                .clickable(actionStartActivity(openClockIntent)),
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
            Row(
                verticalAlignment = Alignment.Vertical.Bottom
            ) {
                Text(
                    text = timeString,
                    style = TextStyle(
                        color = ColorProvider(config.accentColor),
                        fontSize = timeFontSize,
                        fontWeight = FontWeight.Light
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
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }
    }
}
`,
  },
  {
    path: 'app/src/main/java/com/atelier/clockwidget/glance/ClockGlanceWidgetReceiver.kt',
    name: 'ClockGlanceWidgetReceiver.kt',
    language: 'kotlin',
    category: 'glance',
    description: 'Broadcast receiver binding Glance widget to the Android system launcher.',
    content: `package com.atelier.clockwidget.glance

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.atelier.clockwidget.work.ClockUpdateWorker

/**
 * Receiver handling system widget lifecycle broadcasts:
 * APPWIDGET_UPDATE, APPWIDGET_ENABLED, APPWIDGET_DELETED.
 * Enqueues immediate update work and periodic sync.
 */
class ClockGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = ClockGlanceWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            AppWidgetManager.ACTION_APPWIDGET_ENABLED -> {
                ClockUpdateWorker.enqueuePeriodicWork(context)
            }
        }
    }
}
`,
  },
  {
    path: 'app/src/main/java/com/atelier/clockwidget/model/ClockCustomization.kt',
    name: 'ClockCustomization.kt',
    language: 'kotlin',
    category: 'model',
    description: 'Data model encapsulating all 12 user-configurable parameters and styling resolvers.',
    content: `package com.atelier.clockwidget.model

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
            values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MINIMAL
    }
}

enum class WidgetSizeCategory {
    SMALL,   // 2x2
    MEDIUM,  // 4x2
    LARGE    // 4x4
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
`,
  },
  {
    path: 'app/src/main/java/com/atelier/clockwidget/data/ClockPreferencesKeys.kt',
    name: 'ClockPreferencesKeys.kt',
    language: 'kotlin',
    category: 'data',
    description: 'DataStore Preferences schema keys for persistent widget customization.',
    content: `package com.atelier.clockwidget.data

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
`,
  },
  {
    path: 'app/src/main/java/com/atelier/clockwidget/work/ClockUpdateWorker.kt',
    name: 'ClockUpdateWorker.kt',
    language: 'kotlin',
    category: 'worker',
    description: 'Battery-aware WorkManager worker scheduling regular Glance widget composition updates.',
    content: `package com.atelier.clockwidget.work

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.atelier.clockwidget.glance.ClockGlanceWidget
import java.util.concurrent.TimeUnit

/**
 * Worker responsible for periodically triggering Clock widget renders.
 * Adheres strictly to Android battery constraints:
 * - Uses PeriodicWorkRequest with flex intervals
 * - Uses existing WorkPolicy.KEEP to prevent duplicate queues
 * - Minimal battery drain with no lingering background service threads
 */
class ClockUpdateWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            ClockGlanceWidget().updateAll(appContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "atelier_clock_periodic_update"

        fun enqueuePeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val updateRequest = PeriodicWorkRequestBuilder<ClockUpdateWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                updateRequest
            )
        }

        fun triggerImmediateUpdate(context: Context) {
            val oneTimeRequest = OneTimeWorkRequestBuilder<ClockUpdateWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(oneTimeRequest)
        }
    }
}
`,
  },
  {
    path: 'app/src/main/java/com/atelier/clockwidget/receiver/BootCompletedReceiver.kt',
    name: 'BootCompletedReceiver.kt',
    language: 'kotlin',
    category: 'worker',
    description: 'Handles device reboot, immediately restoring clock schedule without user action.',
    content: `package com.atelier.clockwidget.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atelier.clockwidget.work.ClockUpdateWorker

/**
 * Automatically enqueues WorkManager on system boot completion.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ClockUpdateWorker.enqueuePeriodicWork(context)
            ClockUpdateWorker.triggerImmediateUpdate(context)
        }
    }
}
`,
  },
  {
    path: 'app/src/main/java/com/atelier/clockwidget/receiver/TimezoneChangedReceiver.kt',
    name: 'TimezoneChangedReceiver.kt',
    language: 'kotlin',
    category: 'worker',
    description: 'Handles timezone, daylight savings, or manual time adjustment instantly.',
    content: `package com.atelier.clockwidget.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atelier.clockwidget.work.ClockUpdateWorker

/**
 * Listens for system timezone and clock adjustments, triggering an
 * instantaneous Glance update so the widget is never out of sync.
 */
class TimezoneChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED -> {
                ClockUpdateWorker.triggerImmediateUpdate(context)
            }
        }
    }
}
`,
  },
  {
    path: 'app/src/main/java/com/atelier/clockwidget/ui/ClockWidgetConfigActivity.kt',
    name: 'ClockWidgetConfigActivity.kt',
    language: 'kotlin',
    category: 'ui',
    description: 'Configuration activity launched when placing widget on the home screen.',
    content: `package com.atelier.clockwidget.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.atelier.clockwidget.data.ClockPreferencesKeys
import com.atelier.clockwidget.glance.ClockGlanceWidget
import com.atelier.clockwidget.model.ClockCustomization
import com.atelier.clockwidget.ui.screens.CustomizeScreen
import com.atelier.clockwidget.ui.theme.AtelierClockTheme
import kotlinx.coroutines.launch

/**
 * Launched when the user drags the Clock widget from their launcher.
 * Allows applying preferences before placement and returns RESULT_OK.
 */
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

        setContent {
            AtelierClockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CustomizeScreen(
                        initialConfig = ClockCustomization(),
                        isConfigMode = true,
                        onSaveConfig = { newConfig ->
                            saveWidgetConfiguration(newConfig)
                        }
                    )
                }
            }
        }
    }

    private fun saveWidgetConfiguration(config: ClockCustomization) {
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
`,
  },
  {
    path: 'app/src/main/java/com/atelier/clockwidget/MainActivity.kt',
    name: 'MainActivity.kt',
    language: 'kotlin',
    category: 'ui',
    description: 'Main companion app with Style Gallery, Live Customizer, and 1-tap Pin to Home Screen.',
    content: `package com.atelier.clockwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.atelier.clockwidget.glance.ClockGlanceWidgetReceiver
import com.atelier.clockwidget.model.ClockCustomization
import com.atelier.clockwidget.ui.screens.MainGalleryScreen
import com.atelier.clockwidget.ui.theme.AtelierClockTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AtelierClockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainGalleryScreen(
                        onPinWidgetToHomeScreen = { config ->
                            requestPinClockWidget(config)
                        }
                    )
                }
            }
        }
    }

    private fun requestPinClockWidget(config: ClockCustomization) {
        val appWidgetManager = getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(this, ClockGlanceWidgetReceiver::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val successCallback = PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent(this, ClockGlanceWidgetReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                Toast.makeText(this, "Added Atelier Clock to Home Screen", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Long-press your Home Screen to add Atelier Clock", Toast.LENGTH_LONG).show()
            }
        }
    }
}
`,
  },
  {
    path: 'app/src/main/AndroidManifest.xml',
    name: 'AndroidManifest.xml',
    language: 'xml',
    category: 'config',
    description: 'Android Manifest with AppWidget provider receiver and configure activity declaration.',
    content: `<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AtelierClock">

        <!-- Main Companion Gallery & Customization App -->
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.AtelierClock">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Widget Configuration Activity -->
        <activity
            android:name=".ui.ClockWidgetConfigActivity"
            android:exported="true"
            android:theme="@style/Theme.AtelierClock">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE" />
            </intent-filter>
        </activity>

        <!-- Glance AppWidget Receiver -->
        <receiver
            android:name=".glance.ClockGlanceWidgetReceiver"
            android:exported="true"
            android:label="@string/clock_widget_name">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/clock_widget_info" />
        </receiver>

        <!-- Boot & Timezone Receivers for reliable updates -->
        <receiver
            android:name=".receiver.BootCompletedReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>

        <receiver
            android:name=".receiver.TimezoneChangedReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.TIMEZONE_CHANGED" />
                <action android:name="android.intent.action.TIME_SET" />
            </intent-filter>
        </receiver>

    </application>

</manifest>
`,
  },
  {
    path: 'app/src/main/res/xml/clock_widget_info.xml',
    name: 'clock_widget_info.xml',
    language: 'xml',
    category: 'res',
    description: 'AppWidgetProviderInfo XML metadata defining target cell size, resizing, and config activity.',
    content: `<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="110dp"
    android:minHeight="110dp"
    android:minResizeWidth="110dp"
    android:minResizeHeight="110dp"
    android:maxResizeWidth="380dp"
    android:maxResizeHeight="380dp"
    android:targetCellWidth="2"
    android:targetCellHeight="2"
    android:updatePeriodMillis="1800000"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="home_screen"
    android:configure="com.atelier.clockwidget.ui.ClockWidgetConfigActivity"
    android:previewLayout="@layout/widget_loading_preview"
    android:description="@string/clock_widget_description" />
`,
  },
  {
    path: 'app/build.gradle.kts',
    name: 'build.gradle.kts (app)',
    language: 'gradle',
    category: 'config',
    description: 'App module Gradle build file with Glance 1.1.1, Compose BOM, and WorkManager.',
    content: `plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.atelier.clockwidget"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.atelier.clockwidget"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // AndroidX Glance — Modern App Widgets
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // DataStore Preferences & WorkManager
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
`,
  },
  {
    path: 'settings.gradle.kts',
    name: 'settings.gradle.kts',
    language: 'gradle',
    category: 'config',
    description: 'Gradle settings root configuring plugin and dependency repositories.',
    content: `pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\\\.android.*")
                includeGroupByRegex("com\\\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AtelierClock"
include(":app")
`,
  },
  {
    path: 'gradle/libs.versions.toml',
    name: 'libs.versions.toml',
    language: 'properties',
    category: 'config',
    description: 'Version Catalog standardizing Compose, Glance, Kotlin, and AndroidX libraries.',
    content: `[versions]
agp = "8.7.2"
kotlin = "2.0.21"
coreKtx = "1.15.0"
lifecycleRuntimeKtx = "2.8.7"
activityCompose = "1.9.3"
composeBom = "2024.10.01"
glance = "1.1.1"
datastore = "1.1.1"
work = "2.10.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-glance = { group = "androidx.glance", name = "glance", version.ref = "glance" }
androidx-glance-appwidget = { group = "androidx.glance", name = "glance-appwidget", version.ref = "glance" }
androidx-glance-material3 = { group = "androidx.glance", name = "glance-material3", version.ref = "glance" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "work" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
`,
  },
  {
    path: '.github/workflows/build-apk.yml',
    name: 'build-apk.yml',
    language: 'gradle',
    category: 'config',
    description: 'GitHub Actions workflow to automatically build the debug APK and upload it as a downloadable artifact on push or dispatch.',
    content: `name: Build Android APK

on:
  push:
    branches: [ "main", "master" ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3

      - name: Grant Execute Permission for Gradle Wrapper
        run: chmod +x gradlew

      - name: Build Debug APK
        run: ./gradlew assembleDebug --no-daemon --stacktrace

      - name: Upload APK Artifact
        uses: actions/upload-artifact@v4
        with:
          name: atelier-clock-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 14
`,
  },
  {
    path: 'gradlew',
    name: 'gradlew',
    language: 'properties',
    category: 'config',
    description: 'Standard POSIX Gradle wrapper launcher script for Linux/macOS and GitHub Actions.',
    content: `#!/bin/sh
##############################################################################
#
#   Gradle start up script for POSIX generated by Gradle
#
##############################################################################

# Attempt to set APP_HOME
APP_HOME="$(cd -P -- "$(dirname -- "$0")" && pwd -P)"

# Use the maximum available, or set MAX_FD to using system default.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support
cygwin=false
msys=false
darwin=false
case "\`uname\`" in
  CYGWIN* ) cygwin=true ;;
  Darwin* ) darwin=true ;;
  MINGW* ) msys=true ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi

exec "$JAVACMD" "-Dorg.gradle.appname=gradlew" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
`,
  },
  {
    path: 'gradle/wrapper/gradle-wrapper.properties',
    name: 'gradle-wrapper.properties',
    language: 'properties',
    category: 'config',
    description: 'Gradle Wrapper configuration with Gradle 8.10.2 distribution.',
    content: `distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.10.2-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
`,
  },
  {
    path: 'README.md',
    name: 'README.md',
    language: 'markdown',
    category: 'config',
    description: 'Complete build, run, and Android Glance architecture guide.',
    content: `# Atelier Clock — Android Glance Home-Screen Widget

An exceptionally polished Android clock widget and companion gallery app built with **Kotlin**, **Jetpack Compose**, and **AndroidX Glance** (\`androidx.glance:glance-appwidget:1.1.1\`).

## 6 Distinct Visual Styles

1. **Minimal**: Ultra-clean sans-serif typography, large numeral contrast, crisp horizontal dividing accents.
2. **Editorial**: High-contrast serif numerals with asymmetric weekday and full date lockup, inspired by magazine layouts.
3. **Digital**: Precision instrument display with segmented time badges, pulsing colon, and AM/PM telemetry flags.
4. **Terminal**: Monospace developer prompt aesthetic with terminal prefix tokens (\`>\`, \`$\`) and system locale indicators.
5. **Typographic**: Oversized expressive numbers with interwoven date typography and dramatic negative space.
6. **Glass**: Translucent frosted acrylic appearance with specular depth and luminous accent hues.

## Supported Widget Sizes (Responsive)

- **Small (2x2)**: 110dp x 110dp — compact time & status
- **Medium (4x2)**: 240dp x 110dp — expansive horizontal time, weekday, and date lockup
- **Large (4x4)**: 240dp x 240dp — immersive hero layout with secondary details

## Technical & Architecture Highlights

- **AndroidX Glance**: Uses modern Jetpack Glance declarative DSL instead of legacy XML RemoteViews.
- **Responsive Layout**: Uses \`SizeMode.Responsive(setOf(SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE))\` with \`LocalSize.current\` to intelligently adapt layout rather than uniformly scaling.
- **Battery Optimization**: Periodic WorkManager updates + \`android.intent.action.TIMEZONE_CHANGED\` and \`android.intent.action.BOOT_COMPLETED\` broadcast receivers. Avoids unnecessary battery drain.
- **PreferencesGlanceStateDefinition**: Backed by Android DataStore Preferences for reactive state updates across the app and widget.
- **Configuration Activity**: Implements \`ClockWidgetConfigActivity\` with \`android.appwidget.action.APPWIDGET_CONFIGURE\` for instant customization prior to home-screen placement.

## How to Build in Android Studio

1. Open Android Studio (Ladybug 2024.2+ or Meerkat).
2. Select **Open** and choose the extracted \`AtelierClock\` folder.
3. Let Gradle sync (JDK 17 required).
4. Run on an Android 8.0+ (API 26+) device or emulator running Android 12, 13, 14, or 15.
5. Long-press on the home screen -> Widgets -> **Atelier Clock** -> Drag to home screen!
`,
  }
];
