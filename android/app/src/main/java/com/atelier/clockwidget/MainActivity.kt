package com.atelier.clockwidget

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
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.clockwidget.glance.ClockGlanceWidgetReceiver
import com.atelier.clockwidget.model.ClockCustomization
import com.atelier.clockwidget.model.ClockStyle
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AtelierAppTheme {
                MainGalleryScreen(
                    onPinWidget = { config ->
                        requestPinClockWidget(config)
                    }
                )
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
                Toast.makeText(this, "Pinned ${config.style.name} Clock to Home Screen", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Long-press your Home Screen and choose Widgets > Atelier Clock", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Long-press your Home Screen to add Atelier Clock widget", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun AtelierAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF09090B),
            surface = Color(0xFF18181B),
            primary = Color(0xFFF4F4F5),
            onPrimary = Color(0xFF09090B),
            onBackground = Color(0xFFF4F4F5),
            onSurface = Color(0xFFE4E4E7),
            outline = Color(0xFF27272A)
        ),
        content = content
    )
}

data class StylePreset(
    val style: ClockStyle,
    val name: String,
    val subtitle: String,
    val defaultAccent: Long
)

val STYLE_PRESETS = listOf(
    StylePreset(ClockStyle.MINIMAL, "Minimal", "Pure essentialism & whitespace", 0xFFF4F4F5),
    StylePreset(ClockStyle.EDITORIAL, "Editorial", "High contrast magazine serif", 0xFFF59E0B),
    StylePreset(ClockStyle.DIGITAL, "Digital", "Precision technical instrument", 0xFF38BDF8),
    StylePreset(ClockStyle.TERMINAL, "Terminal", "Monospace developer prompt", 0xFF10B981),
    StylePreset(ClockStyle.TYPOGRAPHIC, "Typographic", "Expressive oversized glyphs", 0xFFFB7185),
    StylePreset(ClockStyle.GLASS, "Glass", "Frosted luminous depth", 0xFFA78BFA)
)

val ACCENT_PALETTE = listOf(
    Pair("Frost", 0xFFF4F4F5),
    Pair("Amber", 0xFFF59E0B),
    Pair("Cyan", 0xFF38BDF8),
    Pair("Emerald", 0xFF10B981),
    Pair("Rose", 0xFFFB7185),
    Pair("Violet", 0xFFA78BFA)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGalleryScreen(
    onPinWidget: (ClockCustomization) -> Unit
) {
    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    val currentPreset = STYLE_PRESETS[selectedPresetIndex]

    var is24Hour by remember { mutableStateOf(false) }
    var showSeconds by remember { mutableStateOf(false) }
    var showDate by remember { mutableStateOf(true) }
    var showWeekday by remember { mutableStateOf(true) }
    var selectedAccentHex by remember { mutableLongStateOf(currentPreset.defaultAccent) }
    var bgOpacity by remember { mutableIntStateOf(40) }

    val currentConfig = remember(
        currentPreset, is24Hour, showSeconds, showDate, showWeekday, selectedAccentHex, bgOpacity
    ) {
        ClockCustomization(
            style = currentPreset.style,
            is24Hour = is24Hour,
            showSeconds = showSeconds,
            showDate = showDate,
            showWeekday = showWeekday,
            accentColorHex = selectedAccentHex,
            backgroundOpacityPercent = bgOpacity
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF09090B),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ATELIER CLOCK",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = Color(0xFFF4F4F5)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF27272A)
                        ) {
                            Text(
                                text = "GLANCE 1.1",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF09090B)
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF18181B),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentPreset.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFF4F4F5)
                        )
                        Text(
                            text = "Tap to add to Home Screen",
                            fontSize = 12.sp,
                            color = Color(0xFF71717A)
                        )
                    }
                    Button(
                        onClick = { onPinWidget(currentConfig) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(currentConfig.accentColorHex),
                            contentColor = Color(0xFF09090B)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Widget",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Live Interactive Preview Canvas
            Text(
                text = "WIDGET PREVIEW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color(0xFF71717A)
            )
            Spacer(modifier = Modifier.height(8.dp))

            WidgetLivePreviewCard(config = currentConfig)

            Spacer(modifier = Modifier.height(24.dp))

            // Style Selection Chips
            Text(
                text = "AESTHETIC STYLES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color(0xFF71717A)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                STYLE_PRESETS.forEachIndexed { index, preset ->
                    val isSelected = index == selectedPresetIndex
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF27272A) else Color(0xFF18181B),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(preset.defaultAccent)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A)),
                        modifier = Modifier.clickable {
                            selectedPresetIndex = index
                            selectedAccentHex = preset.defaultAccent
                        }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = preset.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = if (isSelected) Color(preset.defaultAccent) else Color(0xFFE4E4E7)
                            )
                            Text(
                                text = preset.subtitle,
                                fontSize = 11.sp,
                                color = Color(0xFF71717A)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Accent Colors
            Text(
                text = "ACCENT COLOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color(0xFF71717A)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ACCENT_PALETTE.forEach { (name, hex) ->
                    val isSelected = selectedAccentHex == hex
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(hex))
                            .clickable { selectedAccentHex = hex }
                            .then(
                                if (isSelected) Modifier.border(3.dp, Color.White, CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF09090B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Toggles
            Text(
                text = "TIME & DATE OPTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color(0xFF71717A)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF18181B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SettingToggleRow(
                        title = "24-Hour Military Format",
                        subtitle = if (is24Hour) "14:28" else "2:28 PM",
                        checked = is24Hour,
                        onCheckedChange = { is24Hour = it }
                    )
                    Divider(color = Color(0xFF27272A), thickness = 0.5.dp)
                    SettingToggleRow(
                        title = "Display Seconds",
                        subtitle = "Include live seconds cadence",
                        checked = showSeconds,
                        onCheckedChange = { showSeconds = it }
                    )
                    Divider(color = Color(0xFF27272A), thickness = 0.5.dp)
                    SettingToggleRow(
                        title = "Show Day of Week",
                        subtitle = "Displays Monday, Tuesday, etc.",
                        checked = showWeekday,
                        onCheckedChange = { showWeekday = it }
                    )
                    Divider(color = Color(0xFF27272A), thickness = 0.5.dp)
                    SettingToggleRow(
                        title = "Show Calendar Date",
                        subtitle = "Month and numeric day",
                        checked = showDate,
                        onCheckedChange = { showDate = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // How to Add Widget Manual Instructions
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF131316),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How to place on Home Screen",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFFF4F4F5)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Tap the \"Add Widget\" button above.\n" +
                               "2. Or long-press any empty space on your Android home screen.\n" +
                               "3. Select \"Widgets\" > \"Atelier Clock\" and drop it anywhere.\n" +
                               "4. Resize the widget horizontally or vertically to switch between 2x2, 4x2, and 4x4 layouts!",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFA1A1AA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF4F4F5)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF71717A)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF09090B),
                checkedTrackColor = Color(0xFFF4F4F5),
                uncheckedThumbColor = Color(0xFFA1A1AA),
                uncheckedTrackColor = Color(0xFF27272A)
            )
        )
    }
}

@Composable
fun WidgetLivePreviewCard(config: ClockCustomization) {
    val date = remember { Date() }
    val timePattern = if (config.is24Hour) {
        if (config.showSeconds) "HH:mm:ss" else "HH:mm"
    } else {
        if (config.showSeconds) "h:mm:ss" else "h:mm"
    }
    val timeString = SimpleDateFormat(timePattern, Locale.getDefault()).format(date)
    val amPmString = if (!config.is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(date).uppercase() else ""
    val weekdayString = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    val dateString = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(config.cornerRadiusDp.dp),
        color = Color(0xFF18181B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            when (config.style) {
                ClockStyle.MINIMAL -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = timeString,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Light,
                                color = Color(config.accentColorHex)
                            )
                            if (amPmString.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = amPmString,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF71717A)
                                )
                            }
                        }
                        if (config.showWeekday || config.showDate) {
                            Spacer(modifier = Modifier.height(4.dp))
                            val parts = mutableListOf<String>()
                            if (config.showWeekday) parts.add(weekdayString.uppercase())
                            if (config.showDate) parts.add(dateString.uppercase())
                            Text(
                                text = parts.joinToString(" • "),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                    }
                }
                ClockStyle.EDITORIAL -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeString,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = Color(config.accentColorHex)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            if (config.showWeekday) {
                                Text(
                                    text = weekdayString,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFA1A1AA)
                                )
                            }
                            if (config.showDate) {
                                Text(
                                    text = dateString,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = Color(config.accentColorHex)
                                )
                            }
                        }
                    }
                }
                ClockStyle.DIGITAL -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF09090B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(config.accentColorHex).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = timeString,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(config.accentColorHex)
                                )
                                if (amPmString.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = amPmString,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(config.accentColorHex).copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                        if (config.showDate) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "UTC+00:00 • $dateString".uppercase(),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF71717A)
                            )
                        }
                    }
                }
                ClockStyle.TERMINAL -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "$ sys.clock --live",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF71717A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "> $timeString ${if (amPmString.isNotEmpty()) "[$amPmString]" else ""}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(config.accentColorHex)
                        )
                        if (config.showDate) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "  [${weekdayString.take(3).uppercase()} $dateString]",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
                ClockStyle.TYPOGRAPHIC -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeString,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-2).sp,
                            color = Color(config.accentColorHex)
                        )
                        if (config.showDate) {
                            Text(
                                text = "$weekdayString • $dateString",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                    }
                }
                ClockStyle.GLASS -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF27272A).copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = timeString,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(config.accentColorHex)
                            )
                            if (config.showDate) {
                                Text(
                                    text = "$weekdayString, $dateString",
                                    fontSize = 11.sp,
                                    color = Color(0xFFA1A1AA)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
