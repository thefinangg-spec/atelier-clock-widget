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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.atelier.clockwidget.data.ClockPreferencesStore
import com.atelier.clockwidget.glance.ClockGlanceWidgetReceiver
import com.atelier.clockwidget.model.ClockCustomization
import com.atelier.clockwidget.model.ClockStyle
import com.atelier.clockwidget.model.WidgetSizeCategory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AtelierAppTheme {
                MainAppScreen(
                    onSaveAndPin = { config ->
                        lifecycleScope.launch {
                            ClockPreferencesStore.saveDefaultConfig(this@MainActivity, config)
                            requestPinClockWidget(config)
                        }
                    },
                    onSaveOnly = { config ->
                        lifecycleScope.launch {
                            ClockPreferencesStore.saveDefaultConfig(this@MainActivity, config)
                            Toast.makeText(
                                this@MainActivity,
                                "Saved! All home-screen widgets updated to ${config.style.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
                Toast.makeText(this, "Saved! Long-press Home Screen and choose Widgets > Atelier Clock", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Saved! Long-press Home Screen to add Atelier Clock widget", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun AtelierAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF09090B),
            surface = Color(0xFF18181B),
            surfaceVariant = Color(0xFF27272A),
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
    val defaultAccent: Long,
    val defaultFont: String,
    val defaultAlignment: String,
    val defaultTextSize: String,
    val defaultCornerRadius: Int,
    val defaultBgOpacity: Int,
    val defaultBgStyle: String
)

val STYLE_PRESETS = listOf(
    StylePreset(
        style = ClockStyle.MINIMAL,
        name = "Minimal",
        subtitle = "Pure essentialism & crisp whitespace",
        defaultAccent = 0xFFF4F4F5,
        defaultFont = "modern-sans",
        defaultAlignment = "left",
        defaultTextSize = "balanced",
        defaultCornerRadius = 24,
        defaultBgOpacity = 40,
        defaultBgStyle = "glass"
    ),
    StylePreset(
        style = ClockStyle.EDITORIAL,
        name = "Editorial",
        subtitle = "High contrast magazine serif numerals",
        defaultAccent = 0xFFF59E0B,
        defaultFont = "editorial-serif",
        defaultAlignment = "left",
        defaultTextSize = "large",
        defaultCornerRadius = 24,
        defaultBgOpacity = 75,
        defaultBgStyle = "solid"
    ),
    StylePreset(
        style = ClockStyle.DIGITAL,
        name = "Digital",
        subtitle = "Precision technical watch instrument",
        defaultAccent = 0xFF38BDF8,
        defaultFont = "digital-mono",
        defaultAlignment = "center",
        defaultTextSize = "balanced",
        defaultCornerRadius = 16,
        defaultBgOpacity = 50,
        defaultBgStyle = "glass"
    ),
    StylePreset(
        style = ClockStyle.TERMINAL,
        name = "Terminal",
        subtitle = "Monospace developer prompt & telemetry",
        defaultAccent = 0xFF10B981,
        defaultFont = "code-mono",
        defaultAlignment = "left",
        defaultTextSize = "compact",
        defaultCornerRadius = 16,
        defaultBgOpacity = 85,
        defaultBgStyle = "solid"
    ),
    StylePreset(
        style = ClockStyle.TYPOGRAPHIC,
        name = "Typographic",
        subtitle = "Expressive oversized interlocking glyphs",
        defaultAccent = 0xFFF97316,
        defaultFont = "expressive",
        defaultAlignment = "center",
        defaultTextSize = "large",
        defaultCornerRadius = 32,
        defaultBgOpacity = 45,
        defaultBgStyle = "glass"
    ),
    StylePreset(
        style = ClockStyle.GLASS,
        name = "Glass",
        subtitle = "Frosted luminous translucent depth",
        defaultAccent = 0xFFA78BFA,
        defaultFont = "modern-sans",
        defaultAlignment = "center",
        defaultTextSize = "balanced",
        defaultCornerRadius = 32,
        defaultBgOpacity = 25,
        defaultBgStyle = "glass"
    )
)

data class AccentColorOption(
    val name: String,
    val hex: Long
)

val ACCENT_PALETTE = listOf(
    AccentColorOption("Monochrome", 0xFFF4F4F5),
    AccentColorOption("Warm Ochre", 0xFFF59E0B),
    AccentColorOption("Cobalt Blue", 0xFF38BDF8),
    AccentColorOption("Emerald", 0xFF10B981),
    AccentColorOption("Terracotta", 0xFFF97316),
    AccentColorOption("Nordic Rose", 0xFFFB7185),
    AccentColorOption("Lavender", 0xFFA78BFA),
    AccentColorOption("Citron Lime", 0xFF84CC16)
)

data class WallpaperPreviewOption(
    val name: String,
    val bgBrush: Brush,
    val isDark: Boolean
)

val WALLPAPER_OPTIONS = listOf(
    WallpaperPreviewOption(
        name = "Obsidian",
        bgBrush = Brush.radialGradient(listOf(Color(0xFF1C1917), Color(0xFF0C0A09), Color(0xFF000000))),
        isDark = true
    ),
    WallpaperPreviewOption(
        name = "Midnight",
        bgBrush = Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF020617))),
        isDark = true
    ),
    WallpaperPreviewOption(
        name = "Terracotta",
        bgBrush = Brush.linearGradient(listOf(Color(0xFF29150D), Color(0xFF1A0B06), Color(0xFF0A0402))),
        isDark = true
    ),
    WallpaperPreviewOption(
        name = "Nordic Pine",
        bgBrush = Brush.linearGradient(listOf(Color(0xFF06261C), Color(0xFF02140E), Color(0xFF010A07))),
        isDark = true
    ),
    WallpaperPreviewOption(
        name = "Porcelain",
        bgBrush = Brush.linearGradient(listOf(Color(0xFFF5F5F4), Color(0xFFE7E5E4), Color(0xFFD6D3D1))),
        isDark = false
    ),
    WallpaperPreviewOption(
        name = "Deep Blue",
        bgBrush = Brush.radialGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0F172A), Color(0xFF030712))),
        isDark = true
    )
)

enum class AppTab(val title: String, val subtitle: String) {
    STUDIO("Studio", "Customizer & Live Home Screen"),
    GALLERY("Gallery", "6 Curated Clock Layouts"),
    PROJECT("Project", "Android Studio Glance Source")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onSaveAndPin: (ClockCustomization) -> Unit,
    onSaveOnly: (ClockCustomization) -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(AppTab.STUDIO) }

    // Configuration state initialized from local store
    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    var is24Hour by remember { mutableStateOf(false) }
    var showSeconds by remember { mutableStateOf(false) }
    var showDate by remember { mutableStateOf(true) }
    var showWeekday by remember { mutableStateOf(true) }
    var selectedAccentHex by remember { mutableLongStateOf(STYLE_PRESETS[0].defaultAccent) }
    var fontStyle by remember { mutableStateOf(STYLE_PRESETS[0].defaultFont) }
    var alignment by remember { mutableStateOf(STYLE_PRESETS[0].defaultAlignment) }
    var textSize by remember { mutableStateOf(STYLE_PRESETS[0].defaultTextSize) }
    var cornerRadius by remember { mutableIntStateOf(STYLE_PRESETS[0].defaultCornerRadius) }
    var bgOpacity by remember { mutableIntStateOf(STYLE_PRESETS[0].defaultBgOpacity) }
    var bgStyle by remember { mutableStateOf(STYLE_PRESETS[0].defaultBgStyle) }
    var paddingOption by remember { mutableStateOf("standard") }

    // Wallpaper simulator state
    var selectedWallpaperIndex by remember { mutableIntStateOf(0) }
    val currentWallpaper = WALLPAPER_OPTIONS[selectedWallpaperIndex]

    // Load saved preferences on launch
    LaunchedEffect(Unit) {
        val saved = ClockPreferencesStore.loadDefaultConfig(context)
        val matchedIndex = STYLE_PRESETS.indexOfFirst { it.style == saved.style }.coerceAtLeast(0)
        selectedPresetIndex = matchedIndex
        is24Hour = saved.is24Hour
        showSeconds = saved.showSeconds
        showDate = saved.showDate
        showWeekday = saved.showWeekday
        selectedAccentHex = saved.accentColorHex
        fontStyle = saved.fontStyle
        alignment = saved.alignment
        textSize = saved.textSize
        cornerRadius = saved.cornerRadiusDp
        bgOpacity = saved.backgroundOpacityPercent
        bgStyle = saved.backgroundStyle
        paddingOption = saved.padding
    }

    val currentConfig = remember(
        selectedPresetIndex, is24Hour, showSeconds, showDate, showWeekday,
        selectedAccentHex, fontStyle, alignment, textSize, cornerRadius,
        bgOpacity, bgStyle, paddingOption
    ) {
        val preset = STYLE_PRESETS[selectedPresetIndex]
        ClockCustomization(
            style = preset.style,
            is24Hour = is24Hour,
            showSeconds = showSeconds,
            showDate = showDate,
            showWeekday = showWeekday,
            fontStyle = fontStyle,
            alignment = alignment,
            textSize = textSize,
            cornerRadiusDp = cornerRadius,
            backgroundOpacityPercent = bgOpacity,
            backgroundStyle = bgStyle,
            accentColorHex = selectedAccentHex,
            padding = paddingOption
        )
    }

    fun applyPreset(index: Int) {
        val preset = STYLE_PRESETS[index]
        selectedPresetIndex = index
        selectedAccentHex = preset.defaultAccent
        fontStyle = preset.defaultFont
        alignment = preset.defaultAlignment
        textSize = preset.defaultTextSize
        cornerRadius = preset.defaultCornerRadius
        bgOpacity = preset.defaultBgOpacity
        bgStyle = preset.defaultBgStyle
    }

    Scaffold(
        containerColor = Color(0xFF09090B),
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF09090B))) {
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
                    actions = {
                        IconButton(onClick = { onSaveOnly(currentConfig) }) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Save Configuration",
                                tint = Color(currentConfig.accentColorHex)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF09090B)
                    )
                )

                // 3 Top Navigation Tabs: Studio | Gallery | Project
                TabRow(
                    selectedTabIndex = currentTab.ordinal,
                    containerColor = Color(0xFF09090B),
                    contentColor = Color(0xFFF4F4F5),
                    divider = { Divider(color = Color(0xFF27272A), thickness = 1.dp) }
                ) {
                    AppTab.values().forEach { tab ->
                        Tab(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontSize = 14.sp,
                                    fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
            }
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentConfig.style.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFF4F4F5)
                        )
                        Text(
                            text = "Saves & updates home widgets",
                            fontSize = 11.sp,
                            color = Color(0xFF71717A)
                        )
                    }

                    OutlinedButton(
                        onClick = { onSaveOnly(currentConfig) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF4F4F5)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3F3F46)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Apply", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onSaveAndPin(currentConfig) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(currentConfig.accentColorHex),
                            contentColor = Color(0xFF09090B)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Widget",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.STUDIO -> StudioTabContent(
                    config = currentConfig,
                    selectedPresetIndex = selectedPresetIndex,
                    onSelectPreset = { applyPreset(it) },
                    selectedWallpaper = currentWallpaper,
                    selectedWallpaperIndex = selectedWallpaperIndex,
                    onSelectWallpaper = { selectedWallpaperIndex = it },
                    selectedAccentHex = selectedAccentHex,
                    onSelectAccent = { selectedAccentHex = it },
                    fontStyle = fontStyle,
                    onSelectFont = { fontStyle = it },
                    alignment = alignment,
                    onSelectAlignment = { alignment = it },
                    textSize = textSize,
                    onSelectTextSize = { textSize = it },
                    cornerRadius = cornerRadius,
                    onSelectCornerRadius = { cornerRadius = it },
                    bgOpacity = bgOpacity,
                    onSelectBgOpacity = { bgOpacity = it },
                    bgStyle = bgStyle,
                    onSelectBgStyle = { bgStyle = it },
                    is24Hour = is24Hour,
                    onToggle24Hour = { is24Hour = it },
                    showSeconds = showSeconds,
                    onToggleShowSeconds = { showSeconds = it },
                    showWeekday = showWeekday,
                    onToggleShowWeekday = { showWeekday = it },
                    showDate = showDate,
                    onToggleShowDate = { showDate = it },
                    onResetDefaults = { applyPreset(selectedPresetIndex) }
                )

                AppTab.GALLERY -> GalleryTabContent(
                    currentConfig = currentConfig,
                    onSelectStyle = { styleId ->
                        val index = STYLE_PRESETS.indexOfFirst { it.style.id == styleId }.coerceAtLeast(0)
                        applyPreset(index)
                        currentTab = AppTab.STUDIO
                    },
                    onApplyStyle = { styleId ->
                        val index = STYLE_PRESETS.indexOfFirst { it.style.id == styleId }.coerceAtLeast(0)
                        applyPreset(index)
                        onSaveOnly(STYLE_PRESETS[index].let { p ->
                            ClockCustomization(
                                style = p.style,
                                is24Hour = is24Hour,
                                showSeconds = showSeconds,
                                showDate = showDate,
                                showWeekday = showWeekday,
                                fontStyle = p.defaultFont,
                                alignment = p.defaultAlignment,
                                textSize = p.defaultTextSize,
                                cornerRadiusDp = p.defaultCornerRadius,
                                backgroundOpacityPercent = p.defaultBgOpacity,
                                backgroundStyle = p.defaultBgStyle,
                                accentColorHex = p.defaultAccent
                            )
                        })
                    }
                )

                AppTab.PROJECT -> ProjectTabContent()
            }
        }
    }
}

@Composable
fun StudioTabContent(
    config: ClockCustomization,
    selectedPresetIndex: Int,
    onSelectPreset: (Int) -> Unit,
    selectedWallpaper: WallpaperPreviewOption,
    selectedWallpaperIndex: Int,
    onSelectWallpaper: (Int) -> Unit,
    selectedAccentHex: Long,
    onSelectAccent: (Long) -> Unit,
    fontStyle: String,
    onSelectFont: (String) -> Unit,
    alignment: String,
    onSelectAlignment: (String) -> Unit,
    textSize: String,
    onSelectTextSize: (String) -> Unit,
    cornerRadius: Int,
    onSelectCornerRadius: (Int) -> Unit,
    bgOpacity: Int,
    onSelectBgOpacity: (Int) -> Unit,
    bgStyle: String,
    onSelectBgStyle: (String) -> Unit,
    is24Hour: Boolean,
    onToggle24Hour: (Boolean) -> Unit,
    showSeconds: Boolean,
    onToggleShowSeconds: (Boolean) -> Unit,
    showWeekday: Boolean,
    onToggleShowWeekday: (Boolean) -> Unit,
    showDate: Boolean,
    onToggleShowDate: (Boolean) -> Unit,
    onResetDefaults: () -> Unit
) {
    val scrollState = rememberScrollState()
    var previewSizeCategory by remember { mutableStateOf(WidgetSizeCategory.MEDIUM) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Section: Simulated Home Screen Live Canvas with Wallpaper & Size Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HOME SCREEN PREVIEW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color(0xFF71717A)
            )

            // Size Selector Toggle Bar
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF27272A))
                    .padding(2.dp)
            ) {
                listOf(
                    WidgetSizeCategory.SMALL to "2×2",
                    WidgetSizeCategory.MEDIUM to "4×2",
                    WidgetSizeCategory.LARGE to "4×4"
                ).forEach { (size, label) ->
                    val isSelected = previewSizeCategory == size
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF3F3F46) else Color.Transparent)
                            .clickable { previewSizeCategory = size }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFFA1A1AA)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val canvasHeight = when (previewSizeCategory) {
            WidgetSizeCategory.SMALL -> 190.dp
            WidgetSizeCategory.MEDIUM -> 220.dp
            WidgetSizeCategory.LARGE -> 290.dp
        }

        // Phone Canvas Box with Wallpaper
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(canvasHeight)
                .clip(RoundedCornerShape(28.dp))
                .background(selectedWallpaper.bgBrush)
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(28.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            WidgetLivePreviewCard(
                config = config,
                sizeCategory = previewSizeCategory
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wallpaper Selector Chips
        Text(
            text = "WALLPAPERS",
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
            WALLPAPER_OPTIONS.forEachIndexed { index, wp ->
                val isSelected = index == selectedWallpaperIndex
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF27272A) else Color(0xFF18181B),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) Color(config.accentColorHex) else Color(0xFF27272A)
                    ),
                    modifier = Modifier.clickable { onSelectWallpaper(index) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(wp.bgBrush)
                                .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = wp.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFFF4F4F5) else Color(0xFFA1A1AA)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Aesthetic Styles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AESTHETIC STYLES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color(0xFF71717A)
            )
            TextButton(
                onClick = onResetDefaults,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Reset Style Defaults", fontSize = 11.sp)
            }
        }
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
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Color(0xFF27272A) else Color(0xFF18181B),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) Color(preset.defaultAccent) else Color(0xFF27272A)
                    ),
                    modifier = Modifier.clickable { onSelectPreset(index) }
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

        // Section: Accent Colors
        Text(
            text = "ACCENT COLOR",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color(0xFF71717A)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ACCENT_PALETTE.forEach { opt ->
                val isSelected = selectedAccentHex == opt.hex
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(opt.hex))
                        .clickable { onSelectAccent(opt.hex) }
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

        // Section: Typography & Alignment
        Text(
            text = "TYPOGRAPHY & ALIGNMENT",
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
            Column(modifier = Modifier.padding(16.dp)) {
                // Text Alignment Row
                Text(
                    text = "Text Alignment",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFF4F4F5)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("left" to "Left", "center" to "Center", "right" to "Right").forEach { (valKey, label) ->
                        val isSel = alignment == valKey
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectAlignment(valKey) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF27272A) else Color(0xFF131316),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) Color(config.accentColorHex) else Color(0xFF27272A)
                            )
                        ) {
                            Text(
                                text = label,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFFF4F4F5) else Color(0xFFA1A1AA)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFF27272A), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Text Scale Row
                Text(
                    text = "Text Scale",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFF4F4F5)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("compact" to "Compact", "balanced" to "Balanced", "large" to "Large").forEach { (valKey, label) ->
                        val isSel = textSize == valKey
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectTextSize(valKey) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF27272A) else Color(0xFF131316),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) Color(config.accentColorHex) else Color(0xFF27272A)
                            )
                        ) {
                            Text(
                                text = label,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFFF4F4F5) else Color(0xFFA1A1AA)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Background Style & Corner Radius
        Text(
            text = "BACKGROUND & CORNERS",
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
            Column(modifier = Modifier.padding(16.dp)) {
                // Background Style
                Text(
                    text = "Background Style",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFF4F4F5)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("glass" to "Glass Frosted", "solid" to "Solid Neutral", "transparent" to "Pure Flat").forEach { (valKey, label) ->
                        val isSel = bgStyle == valKey
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectBgStyle(valKey) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF27272A) else Color(0xFF131316),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) Color(config.accentColorHex) else Color(0xFF27272A)
                            )
                        ) {
                            Text(
                                text = label,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFFF4F4F5) else Color(0xFFA1A1AA)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFF27272A), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Corner Radius Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Corner Radius", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF4F4F5))
                    Text(text = "${cornerRadius}dp", fontSize = 12.sp, color = Color(0xFFA1A1AA))
                }
                Slider(
                    value = cornerRadius.toFloat(),
                    onValueChange = { onSelectCornerRadius(it.toInt()) },
                    valueRange = 8f..36f,
                    steps = 6,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(config.accentColorHex),
                        activeTrackColor = Color(config.accentColorHex),
                        inactiveTrackColor = Color(0xFF27272A)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Background Opacity Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Background Opacity", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF4F4F5))
                    Text(text = "${bgOpacity}%", fontSize = 12.sp, color = Color(0xFFA1A1AA))
                }
                Slider(
                    value = bgOpacity.toFloat(),
                    onValueChange = { onSelectBgOpacity(it.toInt()) },
                    valueRange = 10f..100f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(config.accentColorHex),
                        activeTrackColor = Color(config.accentColorHex),
                        inactiveTrackColor = Color(0xFF27272A)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Time & Date Formats
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
                    subtitle = if (is24Hour) "14:28 format" else "2:28 PM with AM/PM",
                    checked = is24Hour,
                    onCheckedChange = onToggle24Hour
                )
                Divider(color = Color(0xFF27272A), thickness = 0.5.dp)
                SettingToggleRow(
                    title = "Display Seconds",
                    subtitle = "Include live running seconds",
                    checked = showSeconds,
                    onCheckedChange = onToggleShowSeconds
                )
                Divider(color = Color(0xFF27272A), thickness = 0.5.dp)
                SettingToggleRow(
                    title = "Show Day of Week",
                    subtitle = "Displays Monday, Tuesday, etc.",
                    checked = showWeekday,
                    onCheckedChange = onToggleShowWeekday
                )
                Divider(color = Color(0xFF27272A), thickness = 0.5.dp)
                SettingToggleRow(
                    title = "Show Calendar Date",
                    subtitle = "Month and numeric day",
                    checked = showDate,
                    onCheckedChange = onToggleShowDate
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instructions Card
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
                        text = "How to use your custom clock",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFFF4F4F5)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Tap \"Apply\" to save your customizations to all current widgets.\n" +
                           "• Tap \"Add Widget\" to automatically pin a new widget to your home screen.\n" +
                           "• Resize widgets on home screen to adapt between 2x2, 4x2, and 4x4 sizes.\n" +
                           "• Tap any widget on your home screen at any time to open this Atelier Studio.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFFA1A1AA)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun GalleryTabContent(
    currentConfig: ClockCustomization,
    onSelectStyle: (String) -> Unit,
    onApplyStyle: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var gallerySizeCategory by remember { mutableStateOf(WidgetSizeCategory.MEDIUM) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CURATED CLOCK LAYOUTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF71717A)
                )
                Text(
                    text = "6 Bespoke styles with optical typography",
                    fontSize = 12.sp,
                    color = Color(0xFFA1A1AA)
                )
            }

            // Size Selector Toggle Bar
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF27272A))
                    .padding(2.dp)
            ) {
                listOf(
                    WidgetSizeCategory.SMALL to "2×2",
                    WidgetSizeCategory.MEDIUM to "4×2",
                    WidgetSizeCategory.LARGE to "4×4"
                ).forEach { (size, label) ->
                    val isSelected = gallerySizeCategory == size
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF3F3F46) else Color.Transparent)
                            .clickable { gallerySizeCategory = size }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFFA1A1AA)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        STYLE_PRESETS.forEach { preset ->
            val presetConfig = remember(preset, currentConfig.is24Hour, currentConfig.showSeconds) {
                ClockCustomization(
                    style = preset.style,
                    is24Hour = currentConfig.is24Hour,
                    showSeconds = currentConfig.showSeconds,
                    showDate = true,
                    showWeekday = true,
                    fontStyle = preset.defaultFont,
                    alignment = preset.defaultAlignment,
                    textSize = preset.defaultTextSize,
                    cornerRadiusDp = preset.defaultCornerRadius,
                    backgroundOpacityPercent = preset.defaultBgOpacity,
                    backgroundStyle = preset.defaultBgStyle,
                    accentColorHex = preset.defaultAccent
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF18181B),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (currentConfig.style == preset.style) Color(preset.defaultAccent) else Color(0xFF27272A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = preset.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF4F4F5)
                                )
                                if (currentConfig.style == preset.style) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(preset.defaultAccent).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(preset.defaultAccent)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = preset.subtitle,
                                fontSize = 12.sp,
                                color = Color(0xFF71717A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val cardPreviewHeight = when (gallerySizeCategory) {
                        WidgetSizeCategory.SMALL -> 160.dp
                        WidgetSizeCategory.MEDIUM -> 175.dp
                        WidgetSizeCategory.LARGE -> 260.dp
                    }

                    // Live Preview Container on sleek dark surface
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardPreviewHeight)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF0D0D10))
                            .border(1.dp, Color(0xFF232328), RoundedCornerShape(18.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        WidgetLivePreviewCard(
                            config = presetConfig,
                            sizeCategory = gallerySizeCategory
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onApplyStyle(preset.style.id) },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3F3F46)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF4F4F5))
                        ) {
                            Text("Apply", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onSelectStyle(preset.style.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(preset.defaultAccent),
                                contentColor = Color(0xFF09090B)
                            )
                        ) {
                            Text("Customize in Studio", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectTabContent() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedFileIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    val sampleFiles = listOf(
        Pair("ClockGlanceWidget.kt", """
// GlanceAppWidget Implementation with multi-size responsive layouts
class ClockGlanceWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(setOf(SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE))
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val config = ClockCustomization.fromPreferences(prefs)
            GlanceTheme { ClockWidgetRoot(context, config, LocalSize.current) }
        }
    }
}
        """.trimIndent()),
        Pair("ClockGlanceWidgetReceiver.kt", """
// AppWidget Provider Receiver with WorkManager triggers
class ClockGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ClockGlanceWidget()
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            ClockUpdateWorker.enqueuePeriodicWork(context)
        }
    }
}
        """.trimIndent()),
        Pair("ClockUpdateWorker.kt", """
// Background battery-efficient periodic updater
class ClockUpdateWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        ClockGlanceWidget().updateAll(applicationContext)
        return Result.success()
    }
}
        """.trimIndent())
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = "ANDROID STUDIO PROJECT EXPLORER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color(0xFF71717A)
        )
        Text(
            text = "Modern Jetpack Compose & Glance 1.1 architecture with DataStore persistence.",
            fontSize = 12.sp,
            color = Color(0xFFA1A1AA)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // File Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sampleFiles.forEachIndexed { index, (name, _) ->
                val isSel = index == selectedFileIndex
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSel) Color(0xFF27272A) else Color(0xFF18181B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSel) Color(0xFF38BDF8) else Color(0xFF27272A)
                    ),
                    modifier = Modifier.clickable { selectedFileIndex = index }
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSel) Color(0xFF38BDF8) else Color(0xFFA1A1AA)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Code Box
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF131316),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sampleFiles[selectedFileIndex].first,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(sampleFiles[selectedFileIndex].second))
                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy code",
                            tint = Color(0xFFA1A1AA),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = sampleFiles[selectedFileIndex].second,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFFE4E4E7)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Architecture Features
        Text(
            text = "ARCHITECTURE & STACK HIGHLIGHTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color(0xFF71717A)
        )
        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            "Glance 1.1 AppWidgets" to "Modern declarative Kotlin DSL replacing legacy RemoteViews XML with fluid responsiveness.",
            "Preferences DataStore" to "Coroutines-backed transactional key-value store syncing user choices to home widgets instantly.",
            "WorkManager 2.10" to "Guaranteed background time synchronization and battery-optimized periodic update intervals.",
            "Dynamic Pinning" to "One-tap home screen placement using Android O+ AppWidgetManager.requestPinAppWidget."
        ).forEach { (title, desc) ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF18181B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF4F4F5))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = desc, fontSize = 12.sp, color = Color(0xFFA1A1AA), lineHeight = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
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
fun WidgetLivePreviewCard(
    config: ClockCustomization,
    modifier: Modifier = Modifier,
    sizeCategory: WidgetSizeCategory = WidgetSizeCategory.MEDIUM
) {
    val date = remember { Date() }
    val hourPattern = if (config.is24Hour) "HH" else "h"
    val hours = SimpleDateFormat(hourPattern, Locale.getDefault()).format(date)
    val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(date)
    val seconds = SimpleDateFormat("ss", Locale.getDefault()).format(date)
    val timePattern = if (config.is24Hour) {
        if (config.showSeconds) "HH:mm:ss" else "HH:mm"
    } else {
        if (config.showSeconds) "h:mm:ss" else "h:mm"
    }
    val timeString = SimpleDateFormat(timePattern, Locale.getDefault()).format(date)
    val amPmString = if (!config.is24Hour) SimpleDateFormat("a", Locale.getDefault()).format(date).uppercase() else ""
    val weekdayString = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    val shortWeekday = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
    val dateString = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
    val shortMonthDay = SimpleDateFormat("MMM d", Locale.getDefault()).format(date).uppercase()
    val dayNum = SimpleDateFormat("dd", Locale.getDefault()).format(date)
    val shortMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase()
    val isoDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)

    // Font family mapping
    val chosenFontFamily = when (config.fontStyle) {
        "editorial-serif" -> FontFamily.Serif
        "digital-mono", "code-mono" -> FontFamily.Monospace
        else -> FontFamily.Default
    }

    // Alignment mapping
    val horizontalAlign = when (config.alignment) {
        "center" -> Alignment.CenterHorizontally
        "right" -> Alignment.End
        else -> Alignment.Start
    }

    // Text size scaling
    val fontScale = when (config.textSize) {
        "compact" -> 0.85f
        "large" -> 1.15f
        else -> 1.0f
    }

    val accentColor = Color(config.accentColorHex)
    val subtleColor = Color(0xFFA1A1AA)
    val mutedColor = Color(0xFF71717A)

    val innerPadding = when (config.padding) {
        "compact" -> 10.dp
        "generous" -> 18.dp
        else -> 14.dp
    }

    val bgAlpha = (config.backgroundOpacityPercent.coerceIn(5, 100) / 100f)
    val (bgModifier, borderStroke) = when (config.backgroundStyle) {
        "transparent" -> Pair(
            Modifier.background(Color.Transparent),
            null
        )
        "solid" -> Pair(
            Modifier.background(Color(0xFF18181B).copy(alpha = bgAlpha)),
            androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        )
        else -> when (config.style) {
            ClockStyle.EDITORIAL -> Pair(
                Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF221F1D).copy(alpha = bgAlpha),
                            Color(0xFF12100F).copy(alpha = bgAlpha)
                        )
                    )
                ),
                androidx.compose.foundation.BorderStroke(1.2.dp, Color.White.copy(alpha = 0.18f))
            )
            ClockStyle.TERMINAL -> Pair(
                Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0C120F).copy(alpha = bgAlpha),
                            Color(0xFF040806).copy(alpha = bgAlpha)
                        )
                    )
                ),
                androidx.compose.foundation.BorderStroke(1.2.dp, Color(0x5510B981))
            )
            ClockStyle.DIGITAL -> Pair(
                Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0B1326).copy(alpha = bgAlpha),
                            Color(0xFF030712).copy(alpha = bgAlpha)
                        )
                    )
                ),
                androidx.compose.foundation.BorderStroke(1.2.dp, Color(0x4538BDF8))
            )
            ClockStyle.TYPOGRAPHIC -> Pair(
                Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF281C15).copy(alpha = bgAlpha),
                            Color(0xFF0E0B09).copy(alpha = bgAlpha)
                        )
                    )
                ),
                androidx.compose.foundation.BorderStroke(1.2.dp, Color(0x40F97316))
            )
            ClockStyle.GLASS -> Pair(
                Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0x80454A59).copy(alpha = bgAlpha * 0.85f),
                            Color(0x60262933).copy(alpha = bgAlpha * 0.85f),
                            Color(0x95181920).copy(alpha = bgAlpha)
                        )
                    )
                ),
                androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.35f))
            )
            ClockStyle.MINIMAL -> Pair(
                Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1C1C20).copy(alpha = bgAlpha),
                            Color(0xFF0C0C0E).copy(alpha = bgAlpha)
                        )
                    )
                ),
                androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            )
        }
    }

    val cardShape = RoundedCornerShape(config.cornerRadiusDp.dp)

    val widgetModifier = when (sizeCategory) {
        WidgetSizeCategory.SMALL -> modifier.size(155.dp)
        WidgetSizeCategory.MEDIUM -> modifier.fillMaxWidth().height(155.dp)
        WidgetSizeCategory.LARGE -> modifier.fillMaxWidth().height(240.dp)
    }

    Box(
        modifier = widgetModifier
            .clip(cardShape)
            .then(bgModifier)
            .then(if (borderStroke != null) Modifier.border(borderStroke.width, borderStroke.brush, cardShape) else Modifier)
            .padding(innerPadding)
    ) {
        when (config.style) {
            ClockStyle.MINIMAL -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = horizontalAlign
                ) {
                    when (sizeCategory) {
                        WidgetSizeCategory.SMALL -> {
                            Column(horizontalAlignment = horizontalAlign) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$hours:$minutes",
                                        fontSize = (30 * fontScale).sp,
                                        fontWeight = FontWeight.Light,
                                        fontFamily = chosenFontFamily,
                                        color = accentColor
                                    )
                                    if (config.showSeconds) {
                                        Text(
                                            text = ":$seconds",
                                            fontSize = (13 * fontScale).sp,
                                            fontFamily = chosenFontFamily,
                                            color = subtleColor
                                        )
                                    }
                                }
                                if (amPmString.isNotEmpty()) {
                                    Text(
                                        text = amPmString,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = subtleColor
                                    )
                                }
                            }
                            if (config.showDate || config.showWeekday) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0x30FFFFFF))
                                )
                                Text(
                                    text = "$shortWeekday / $shortMonthDay",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp,
                                    color = subtleColor
                                )
                            }
                        }
                        WidgetSizeCategory.MEDIUM -> {
                            Column(horizontalAlignment = horizontalAlign) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$hours:$minutes",
                                        fontSize = (44 * fontScale).sp,
                                        fontWeight = FontWeight.Light,
                                        fontFamily = chosenFontFamily,
                                        color = accentColor
                                    )
                                    if (config.showSeconds) {
                                        Text(
                                            text = ":$seconds",
                                            fontSize = (17 * fontScale).sp,
                                            fontFamily = chosenFontFamily,
                                            color = subtleColor
                                        )
                                    }
                                    if (amPmString.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = amPmString,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = subtleColor
                                        )
                                    }
                                }
                            }
                            if (config.showDate || config.showWeekday) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0x30FFFFFF))
                                )
                                Text(
                                    text = "$shortWeekday / $shortMonthDay",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp,
                                    color = subtleColor
                                )
                            }
                        }
                        WidgetSizeCategory.LARGE -> {
                            Column(horizontalAlignment = horizontalAlign) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$hours:$minutes",
                                        fontSize = (56 * fontScale).sp,
                                        fontWeight = FontWeight.Light,
                                        fontFamily = chosenFontFamily,
                                        color = accentColor
                                    )
                                    if (config.showSeconds) {
                                        Text(
                                            text = ":$seconds",
                                            fontSize = (22 * fontScale).sp,
                                            fontFamily = chosenFontFamily,
                                            color = subtleColor
                                        )
                                    }
                                    if (amPmString.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = amPmString,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = subtleColor
                                        )
                                    }
                                }
                            }
                            if (config.showDate || config.showWeekday) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0x30FFFFFF))
                                )
                                Text(
                                    text = "$weekdayString / $dateString".uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp,
                                    color = subtleColor
                                )
                            }
                        }
                    }
                }
            }

            ClockStyle.EDITORIAL -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    when (sizeCategory) {
                        WidgetSizeCategory.SMALL -> {
                            Column(horizontalAlignment = horizontalAlign) {
                                Text(
                                    text = hours,
                                    fontSize = (38 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = accentColor,
                                    lineHeight = (38 * fontScale).sp
                                )
                                Text(
                                    text = minutes,
                                    fontSize = (38 * fontScale).sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = FontFamily.Serif,
                                    color = subtleColor,
                                    lineHeight = (38 * fontScale).sp
                                )
                            }
                            if (config.showDate) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0x30FFFFFF))
                                )
                                Text(
                                    text = "$shortWeekday · $shortMonthDay",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = accentColor
                                )
                            }
                        }
                        WidgetSizeCategory.MEDIUM -> {
                            Row(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$hours:$minutes",
                                        fontSize = (46 * fontScale).sp,
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        fontFamily = FontFamily.Serif,
                                        color = accentColor
                                    )
                                    if (config.showSeconds) {
                                        Text(
                                            text = ".$seconds",
                                            fontSize = (18 * fontScale).sp,
                                            fontFamily = FontFamily.Serif,
                                            color = subtleColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(44.dp)
                                        .background(Color(0x35FFFFFF))
                                )
                                Spacer(modifier = Modifier.width(14.dp))

                                Column(verticalArrangement = Arrangement.Center) {
                                    if (config.showWeekday) {
                                        Text(
                                            text = weekdayString.uppercase(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Serif,
                                            letterSpacing = 1.sp,
                                            color = subtleColor
                                        )
                                    }
                                    if (config.showDate) {
                                        Text(
                                            text = shortMonthDay,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Serif,
                                            color = accentColor
                                        )
                                    }
                                    if (amPmString.isNotEmpty()) {
                                        Text(
                                            text = "$amPmString EDITION",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Serif,
                                            color = mutedColor
                                        )
                                    }
                                }
                            }
                        }
                        WidgetSizeCategory.LARGE -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CHRONICLE · VOL. $year",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = subtleColor
                                )
                                Text(
                                    text = weekdayString.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = accentColor
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0x35FFFFFF))
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = if (config.alignment == "center") Arrangement.Center else Arrangement.Start
                            ) {
                                Text(
                                    text = "$hours:$minutes",
                                    fontSize = (58 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontFamily = FontFamily.Serif,
                                    color = accentColor
                                )
                                if (config.showSeconds) {
                                    Text(
                                        text = ".$seconds",
                                        fontSize = (22 * fontScale).sp,
                                        fontFamily = FontFamily.Serif,
                                        color = subtleColor
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0x35FFFFFF))
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$dateString",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = accentColor
                                )
                                if (amPmString.isNotEmpty()) {
                                    Text(
                                        text = "$amPmString STANDARD",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Serif,
                                        color = subtleColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            ClockStyle.DIGITAL -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = horizontalAlign
                ) {
                    // Status row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF10B981)
                            )
                        }
                        Text(
                            text = if (config.is24Hour) "24HR" else amPmString,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    // Monospace Time with Seconds Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$hours:$minutes",
                            fontSize = when (sizeCategory) {
                                WidgetSizeCategory.SMALL -> (30 * fontScale).sp
                                WidgetSizeCategory.MEDIUM -> (42 * fontScale).sp
                                WidgetSizeCategory.LARGE -> (54 * fontScale).sp
                            },
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = accentColor
                        )
                        if (config.showSeconds) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = ":$seconds",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = accentColor
                                )
                            }
                        }
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x3038BDF8))
                    )

                    // Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = shortWeekday,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = subtleColor
                        )
                        Text(
                            text = "[$isoDate]",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = subtleColor
                        )
                    }
                }
            }

            ClockStyle.TERMINAL -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = horizontalAlign
                ) {
                    // Command Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "sys.clock",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = subtleColor
                            )
                        }
                        Text(
                            text = "glance_v1.1",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = mutedColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x2510B981))
                    )

                    // CLI Output
                    Column {
                        Text(
                            text = "$ read --current",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = subtleColor
                        )
                        Text(
                            text = "> $timeString █",
                            fontSize = when (sizeCategory) {
                                WidgetSizeCategory.SMALL -> (20 * fontScale).sp
                                WidgetSizeCategory.MEDIUM -> (28 * fontScale).sp
                                WidgetSizeCategory.LARGE -> (38 * fontScale).sp
                            },
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = accentColor
                        )
                    }

                    if (sizeCategory != WidgetSizeCategory.SMALL) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x2510B981))
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "  day: ${weekdayString.lowercase()}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = subtleColor
                            )
                            Text(
                                text = "  iso: $isoDate",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            ClockStyle.TYPOGRAPHIC -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = horizontalAlign
                ) {
                    when (sizeCategory) {
                        WidgetSizeCategory.SMALL -> {
                            Column(horizontalAlignment = horizontalAlign) {
                                Text(
                                    text = hours,
                                    fontSize = (38 * fontScale).sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = chosenFontFamily,
                                    letterSpacing = (-2).sp,
                                    color = accentColor,
                                    lineHeight = (38 * fontScale).sp
                                )
                                Text(
                                    text = minutes,
                                    fontSize = (38 * fontScale).sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = chosenFontFamily,
                                    letterSpacing = (-2).sp,
                                    color = subtleColor,
                                    lineHeight = (38 * fontScale).sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(accentColor.copy(alpha = 0.2f))
                                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "$dayNum $shortMonth",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                            }
                        }
                        WidgetSizeCategory.MEDIUM -> {
                            Row(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$hours : $minutes",
                                    fontSize = (46 * fontScale).sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = chosenFontFamily,
                                    letterSpacing = (-1.5).sp,
                                    color = accentColor
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(accentColor.copy(alpha = 0.2f))
                                            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "$shortWeekday $dayNum",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor
                                        )
                                    }
                                    if (config.showSeconds) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$seconds SEC",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = subtleColor
                                        )
                                    }
                                }
                            }
                        }
                        WidgetSizeCategory.LARGE -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = shortWeekday,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = subtleColor
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(accentColor.copy(alpha = 0.2f))
                                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                    Text(
                                        text = "$dayNum $shortMonth",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor
                                    )
                                }
                            }
                            Column(horizontalAlignment = horizontalAlign) {
                                Text(
                                    text = hours,
                                    fontSize = (60 * fontScale).sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = chosenFontFamily,
                                    letterSpacing = (-3).sp,
                                    color = accentColor,
                                    lineHeight = (60 * fontScale).sp
                                )
                                Text(
                                    text = minutes,
                                    fontSize = (60 * fontScale).sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = chosenFontFamily,
                                    letterSpacing = (-3).sp,
                                    color = subtleColor,
                                    lineHeight = (60 * fontScale).sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0x30FFFFFF))
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LIVE TICK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = mutedColor
                                )
                                if (config.showSeconds) {
                                    Text(
                                        text = ":$seconds",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            ClockStyle.GLASS -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = horizontalAlign
                ) {
                    // Glowing indicator header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = shortWeekday,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = accentColor
                            )
                        }
                        if (amPmString.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = amPmString,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                            }
                        }
                    }

                    // Glass Time
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$hours:$minutes",
                            fontSize = when (sizeCategory) {
                                WidgetSizeCategory.SMALL -> (30 * fontScale).sp
                                WidgetSizeCategory.MEDIUM -> (42 * fontScale).sp
                                WidgetSizeCategory.LARGE -> (56 * fontScale).sp
                            },
                            fontWeight = FontWeight.Light,
                            fontFamily = chosenFontFamily,
                            color = accentColor
                        )
                        if (config.showSeconds) {
                            Text(
                                text = ":$seconds",
                                fontSize = (16 * fontScale).sp,
                                fontFamily = chosenFontFamily,
                                color = subtleColor
                            )
                        }
                    }

                    // Glass Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x35FFFFFF))
                    )

                    // Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = shortMonthDay,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = subtleColor
                        )
                        Text(
                            text = year,
                            fontSize = 11.sp,
                            color = mutedColor
                        )
                    }
                }
            }
        }
    }
}
