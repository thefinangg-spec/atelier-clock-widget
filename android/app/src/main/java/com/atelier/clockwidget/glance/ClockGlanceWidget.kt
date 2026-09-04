package com.atelier.clockwidget.glance

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
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
import com.atelier.clockwidget.R
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
            val styleKey = prefs[com.atelier.clockwidget.data.ClockPreferencesKeys.STYLE_KEY]
            val config = if (styleKey != null) {
                ClockCustomization.fromPreferences(prefs)
            } else {
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

        val paddingDp = when (config.padding) {
            "compact" -> 10.dp
            "generous" -> 18.dp
            else -> 14.dp
        }

        val fontScale = when (config.textSize) {
            "compact" -> 0.85f
            "large" -> 1.15f
            else -> 1.0f
        }

        val bgModifier = when (config.backgroundStyle) {
            "transparent" -> GlanceModifier.background(ColorProvider(Color.Transparent))
            "solid" -> GlanceModifier.background(ImageProvider(R.drawable.glance_widget_solid_bg))
            else -> when (config.style) {
                ClockStyle.EDITORIAL -> GlanceModifier.background(ImageProvider(R.drawable.glance_widget_editorial_bg))
                ClockStyle.TERMINAL -> GlanceModifier.background(ImageProvider(R.drawable.glance_widget_terminal_bg))
                ClockStyle.DIGITAL -> GlanceModifier.background(ImageProvider(R.drawable.glance_widget_digital_bg))
                ClockStyle.TYPOGRAPHIC -> GlanceModifier.background(ImageProvider(R.drawable.glance_widget_typographic_bg))
                ClockStyle.GLASS -> GlanceModifier.background(ImageProvider(R.drawable.glance_widget_glass_bg))
                ClockStyle.MINIMAL -> GlanceModifier.background(ImageProvider(R.drawable.glance_widget_minimal_bg))
            }
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(config.cornerRadiusDp.dp)
                .then(bgModifier)
                .padding(paddingDp)
                .clickable(actionStartActivity<com.atelier.clockwidget.MainActivity>()),
            contentAlignment = config.resolveGlanceAlignment()
        ) {
            when (config.style) {
                ClockStyle.MINIMAL -> MinimalClockLayout(now, config, sizeCategory, fontScale)
                ClockStyle.EDITORIAL -> EditorialClockLayout(now, config, sizeCategory, fontScale)
                ClockStyle.DIGITAL -> DigitalClockLayout(now, config, sizeCategory, fontScale)
                ClockStyle.TERMINAL -> TerminalClockLayout(now, config, sizeCategory, fontScale)
                ClockStyle.TYPOGRAPHIC -> TypographicClockLayout(now, config, sizeCategory, fontScale)
                ClockStyle.GLASS -> GlassClockLayout(now, config, sizeCategory, fontScale)
            }
        }
    }

    @Composable
    private fun MinimalClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory,
        fontScale: Float
    ) {
        val hAlign = when (config.alignment) {
            "center" -> Alignment.Horizontal.CenterHorizontally
            "right" -> Alignment.Horizontal.End
            else -> Alignment.Horizontal.Start
        }
        val timePattern = if (config.is24Hour) "HH:mm" else "h:mm"
        val timeString = SimpleDateFormat(timePattern, Locale.getDefault()).format(date)
        val secondsString = SimpleDateFormat("ss", Locale.getDefault()).format(date)
        val amPmString = if (!config.is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(date).uppercase() else ""
        val weekday = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
        val monthDay = SimpleDateFormat("MMM d", Locale.getDefault()).format(date).uppercase()

        val baseSize = when (sizeCategory) {
            WidgetSizeCategory.SMALL -> (32 * fontScale).sp
            WidgetSizeCategory.MEDIUM -> (46 * fontScale).sp
            WidgetSizeCategory.LARGE -> (58 * fontScale).sp
        }

        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = hAlign
        ) {
            Row(verticalAlignment = Alignment.Vertical.Bottom) {
                Text(
                    text = timeString,
                    style = TextStyle(
                        color = ColorProvider(config.accentColor),
                        fontSize = baseSize,
                        fontWeight = FontWeight.Normal
                    )
                )
                if (config.showSeconds) {
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = ":$secondsString",
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = (baseSize.value * 0.45f).sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
                if (amPmString.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = amPmString,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            if (config.showWeekday || config.showDate) {
                Spacer(modifier = GlanceModifier.height(8.dp))
                // Clean Divider Line
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ColorProvider(Color(0x30FFFFFF)))
                ) {}
                Spacer(modifier = GlanceModifier.height(6.dp))

                val dateText = when {
                    config.showWeekday && config.showDate -> "$weekday / $monthDay"
                    config.showWeekday -> weekday
                    else -> monthDay
                }
                Text(
                    text = dateText,
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }

    @Composable
    private fun EditorialClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory,
        fontScale: Float
    ) {
        val hourFormat = if (config.is24Hour) "HH" else "h"
        val hours = SimpleDateFormat(hourFormat, Locale.getDefault()).format(date)
        val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(date)
        val seconds = SimpleDateFormat("ss", Locale.getDefault()).format(date)
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
        val shortDay = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
        val monthDay = SimpleDateFormat("MMMM d", Locale.getDefault()).format(date)
        val shortMonthDay = SimpleDateFormat("MMM d", Locale.getDefault()).format(date).uppercase()
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
        val amPm = if (!config.is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(date).uppercase() else ""

        when (sizeCategory) {
            WidgetSizeCategory.SMALL -> {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    Text(
                        text = hours,
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = (38 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Text(
                        text = minutes,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = (38 * fontScale).sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(6.dp))
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ColorProvider(Color(0x30FFFFFF)))
                    ) {}
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "$shortDay · $shortMonthDay",
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
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
                    Row(verticalAlignment = Alignment.Vertical.Bottom) {
                        Text(
                            text = "$hours:$minutes",
                            style = TextStyle(
                                color = ColorProvider(config.accentColor),
                                fontSize = (46 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        )
                        if (config.showSeconds) {
                            Text(
                                text = ".$seconds",
                                style = TextStyle(
                                    color = ColorProvider(config.subtleTextColor),
                                    fontSize = (20 * fontScale).sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.width(12.dp))
                    // Vertical dividing line
                    Box(
                        modifier = GlanceModifier
                            .width(1.dp)
                            .height(44.dp)
                            .background(ColorProvider(Color(0x30FFFFFF)))
                    ) {}
                    Spacer(modifier = GlanceModifier.width(12.dp))

                    Column(horizontalAlignment = Alignment.Horizontal.Start) {
                        if (config.showWeekday) {
                            Text(
                                text = dayName.uppercase(),
                                style = TextStyle(
                                    color = ColorProvider(config.subtleTextColor),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                        if (config.showDate) {
                            Text(
                                text = monthDay,
                                style = TextStyle(
                                    color = ColorProvider(config.accentColor),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                        if (amPm.isNotEmpty()) {
                            Text(
                                text = "$amPm EDITION",
                                style = TextStyle(
                                    color = ColorProvider(config.subtleTextColor),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                    }
                }
            }
            WidgetSizeCategory.LARGE -> {
                Column(modifier = GlanceModifier.fillMaxSize()) {
                    // Header Bar
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = "CHRONICLE · VOL. $year",
                            style = TextStyle(
                                color = ColorProvider(config.subtleTextColor),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        if (config.showWeekday) {
                            Text(
                                text = dayName.uppercase(),
                                style = TextStyle(
                                    color = ColorProvider(config.accentColor),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                    }
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ColorProvider(Color(0x30FFFFFF)))
                    ) {}
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    Row(verticalAlignment = Alignment.Vertical.Bottom) {
                        Text(
                            text = "$hours:$minutes",
                            style = TextStyle(
                                color = ColorProvider(config.accentColor),
                                fontSize = (58 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        )
                        if (config.showSeconds) {
                            Text(
                                text = ".$seconds",
                                style = TextStyle(
                                    color = ColorProvider(config.subtleTextColor),
                                    fontSize = (24 * fontScale).sp,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                    }
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ColorProvider(Color(0x30FFFFFF)))
                    ) {}
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        if (config.showDate) {
                            Text(
                                text = "$monthDay, $year",
                                style = TextStyle(
                                    color = ColorProvider(config.accentColor),
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = if (amPm.isNotEmpty()) "$amPm Standard" else "UTC Live",
                            style = TextStyle(
                                color = ColorProvider(config.subtleTextColor),
                                fontSize = 11.sp,
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
        sizeCategory: WidgetSizeCategory,
        fontScale: Float
    ) {
        val timePattern = if (config.is24Hour) "HH:mm" else "hh:mm"
        val timeString = SimpleDateFormat(timePattern, Locale.getDefault()).format(date)
        val secondsString = SimpleDateFormat("ss", Locale.getDefault()).format(date)
        val amPm = if (!config.is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(date).uppercase() else ""
        val weekday = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
        val isoDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(date)

        val baseSize = when (sizeCategory) {
            WidgetSizeCategory.SMALL -> (28 * fontScale).sp
            WidgetSizeCategory.MEDIUM -> (42 * fontScale).sp
            WidgetSizeCategory.LARGE -> (54 * fontScale).sp
        }

        Column(modifier = GlanceModifier.fillMaxWidth()) {
            // Header: Dot + ACTIVE | 24HR or AM/PM
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "● ACTIVE",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF10B981)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = if (config.is24Hour) "24HR" else amPm,
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Middle Time Row + Seconds Badge
            Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                Text(
                    text = timeString,
                    style = TextStyle(
                        color = ColorProvider(config.accentColor),
                        fontSize = baseSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
                if (config.showSeconds) {
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Box(
                        modifier = GlanceModifier
                            .cornerRadius(6.dp)
                            .background(ImageProvider(R.drawable.glance_badge_pill))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = ":$secondsString",
                            style = TextStyle(
                                color = ColorProvider(config.accentColor),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            // Divider Line
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ColorProvider(Color(0x30FFFFFF)))
            ) {}

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Bottom technical footer
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                if (config.showWeekday) {
                    Text(
                        text = weekday,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                if (config.showDate) {
                    Text(
                        text = "[$isoDate]",
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun TerminalClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory,
        fontScale: Float
    ) {
        val timeString = SimpleDateFormat(
            if (config.is24Hour) {
                if (config.showSeconds) "HH:mm:ss" else "HH:mm"
            } else {
                if (config.showSeconds) "hh:mm:ss a" else "hh:mm a"
            },
            Locale.getDefault()
        ).format(date)
        val isoDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        val weekday = SimpleDateFormat("EEEE", Locale.getDefault()).format(date).lowercase()

        Column(modifier = GlanceModifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "● sys.clock",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF10B981)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = "glance_v1.1",
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(4.dp))
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ColorProvider(Color(0x3510B981)))
            ) {}
            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = "$ read --current",
                style = TextStyle(
                    color = ColorProvider(config.subtleTextColor),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = "> $timeString █",
                style = TextStyle(
                    color = ColorProvider(config.accentColor),
                    fontSize = if (sizeCategory == WidgetSizeCategory.SMALL) (18 * fontScale).sp else (26 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )

            if (sizeCategory != WidgetSizeCategory.SMALL) {
                Spacer(modifier = GlanceModifier.height(6.dp))
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ColorProvider(Color(0x25FFFFFF)))
                ) {}
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "  day: $weekday",
                    style = TextStyle(
                        color = ColorProvider(config.subtleTextColor),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
                if (config.showDate) {
                    Text(
                        text = "  iso: $isoDate",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF10B981)),
                            fontSize = 9.sp,
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
        sizeCategory: WidgetSizeCategory,
        fontScale: Float
    ) {
        val hourFormat = if (config.is24Hour) "HH" else "h"
        val hours = SimpleDateFormat(hourFormat, Locale.getDefault()).format(date)
        val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(date)
        val seconds = SimpleDateFormat("ss", Locale.getDefault()).format(date)
        val dayNum = SimpleDateFormat("dd", Locale.getDefault()).format(date)
        val monthShort = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase()
        val weekdayShort = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()

        when (sizeCategory) {
            WidgetSizeCategory.SMALL -> {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    Text(
                        text = hours,
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = (38 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = minutes,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = (38 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Box(
                        modifier = GlanceModifier
                            .cornerRadius(8.dp)
                            .background(ImageProvider(R.drawable.glance_badge_pill))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$dayNum $monthShort",
                            style = TextStyle(
                                color = ColorProvider(config.accentColor),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            WidgetSizeCategory.MEDIUM -> {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = "$hours : $minutes",
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = (44 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Column(horizontalAlignment = Alignment.Horizontal.End) {
                        Box(
                            modifier = GlanceModifier
                                .cornerRadius(8.dp)
                                .background(ImageProvider(R.drawable.glance_badge_pill))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$weekdayShort $dayNum",
                                style = TextStyle(
                                    color = ColorProvider(config.accentColor),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        if (config.showSeconds) {
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Text(
                                text = "$seconds SEC",
                                style = TextStyle(
                                    color = ColorProvider(config.subtleTextColor),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
            WidgetSizeCategory.LARGE -> {
                Column(modifier = GlanceModifier.fillMaxSize()) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = weekdayShort,
                            style = TextStyle(
                                color = ColorProvider(config.subtleTextColor),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Box(
                            modifier = GlanceModifier
                                .cornerRadius(8.dp)
                                .background(ImageProvider(R.drawable.glance_badge_pill))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$dayNum $monthShort",
                                style = TextStyle(
                                    color = ColorProvider(config.accentColor),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Text(
                        text = hours,
                        style = TextStyle(
                            color = ColorProvider(config.accentColor),
                            fontSize = (64 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = minutes,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = (64 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ColorProvider(Color(0x30FFFFFF)))
                    ) {}
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = "LIVE TICK",
                            style = TextStyle(
                                color = ColorProvider(config.subtleTextColor),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = ":$seconds",
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
    }

    @Composable
    private fun GlassClockLayout(
        date: Date,
        config: ClockCustomization,
        sizeCategory: WidgetSizeCategory,
        fontScale: Float
    ) {
        val timeFormat = if (config.is24Hour) "HH:mm" else "h:mm"
        val timeString = SimpleDateFormat(timeFormat, Locale.getDefault()).format(date)
        val seconds = SimpleDateFormat("ss", Locale.getDefault()).format(date)
        val amPm = if (!config.is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(date).uppercase() else ""
        val weekday = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
        val monthDay = SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)

        val baseSize = when (sizeCategory) {
            WidgetSizeCategory.SMALL -> (32 * fontScale).sp
            WidgetSizeCategory.MEDIUM -> (46 * fontScale).sp
            WidgetSizeCategory.LARGE -> (58 * fontScale).sp
        }

        Column(modifier = GlanceModifier.fillMaxWidth()) {
            // Glass Header Bar: Glowing Dot + Weekday | AM/PM Badge
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "● $weekday",
                    style = TextStyle(
                        color = ColorProvider(config.accentColor),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                if (amPm.isNotEmpty()) {
                    Box(
                        modifier = GlanceModifier
                            .cornerRadius(6.dp)
                            .background(ImageProvider(R.drawable.glance_badge_pill))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = amPm,
                            style = TextStyle(
                                color = ColorProvider(config.accentColor),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Main Time Display
            Row(verticalAlignment = Alignment.Vertical.Bottom) {
                Text(
                    text = timeString,
                    style = TextStyle(
                        color = ColorProvider(config.accentColor),
                        fontSize = baseSize,
                        fontWeight = FontWeight.Light
                    )
                )
                if (config.showSeconds) {
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = ":$seconds",
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = (baseSize.value * 0.45f).sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Divider Line
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ColorProvider(Color(0x30FFFFFF)))
            ) {}

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Bottom Split Row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                if (config.showDate) {
                    Text(
                        text = monthDay,
                        style = TextStyle(
                            color = ColorProvider(config.subtleTextColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = year,
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

