package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.AudioMode
import com.example.model.AIState
import com.example.model.DispersionType
import com.example.model.OrbConfig
import com.example.model.PreviewScene
import com.example.ui.components.ColorPickerSheet
import com.example.ui.components.LabeledSlider
import com.example.ui.components.LabeledSwitch
import com.example.ui.components.OrbView
import com.example.ui.components.PresetSelectorBar
import com.example.ui.dialogs.CodeExportDialog
import com.example.ui.dialogs.PresetManagerDialog
import com.example.viewmodel.OrbViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbEditorScreen(viewModel: OrbViewModel) {
    val context = LocalContext.current
    val config by viewModel.currentConfig.collectAsState()
    val currentPresetId by viewModel.currentPresetId.collectAsState()
    val previewScene by viewModel.previewScene.collectAsState()
    val language by viewModel.language.collectAsState()
    val isFullscreen by viewModel.isFullscreen.collectAsState()
    val savedPresets by viewModel.savedPresets.collectAsState()
    val audioLevel by viewModel.audioLevel.collectAsState()
    val audioMode by viewModel.audioMode.collectAsState()

    val isZh = language == "zh"

    // Dialog & Sheet States
    var showExportDialog by remember { mutableStateOf(false) }
    var showPresetDialog by remember { mutableStateOf(false) }
    var activeColorPickerTarget by remember { mutableStateOf<String?>(null) } // "primary", "secondary", etc.
    var selectedParamTab by remember { mutableIntStateOf(0) }

    // Microphone permission launcher
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setAudioMode(AudioMode.MICROPHONE)
        } else {
            viewModel.setAudioMode(AudioMode.SIMULATED_RHYTHM)
        }
    }

    val allPresets = remember(savedPresets) {
        OrbConfig.PRESETS + savedPresets
    }

    Scaffold(
        topBar = {
            if (!isFullscreen) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(config.primaryColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isZh) "流体球体编辑器" else "Liquid Orb Editor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.toggleLanguage() },
                            modifier = Modifier.testTag("lang_toggle_btn")
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E2330))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isZh) "中/EN" else "EN/中",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    actions = {
                        // Randomize morph
                        IconButton(
                            onClick = { viewModel.randomize() },
                            modifier = Modifier.testTag("random_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Randomize",
                                tint = Color(0xFFFBBF24)
                            )
                        }

                        // Presets Bookmark
                        IconButton(
                            onClick = { showPresetDialog = true },
                            modifier = Modifier.testTag("presets_dialog_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Presets",
                                tint = Color(0xFFA78BFA)
                            )
                        }

                        // Export Code
                        IconButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier.testTag("export_code_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Export Code",
                                tint = Color(0xFF38BDF8)
                            )
                        }

                        // Fullscreen
                        IconButton(
                            onClick = { viewModel.toggleFullscreen() },
                            modifier = Modifier.testTag("fullscreen_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.LightGray
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF0B0D14)
                    )
                )
            }
        },
        containerColor = Color(0xFF0B0D14)
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isExpanded = maxWidth >= 600.dp

            if (isFullscreen) {
                // Fullscreen mode with overlay exit button
                Box(modifier = Modifier.fillMaxSize()) {
                    OrbView(
                        config = config,
                        audioLevel = audioLevel,
                        previewScene = previewScene,
                        isZh = isZh,
                        modifier = Modifier.fillMaxSize(),
                        onDoubleTap = { viewModel.toggleAIState() }
                    )

                    IconButton(
                        onClick = { viewModel.toggleFullscreen() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color(0xFF161922).copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White
                        )
                    }
                }
            } else if (isExpanded) {
                // Tablet / Landscape Split Layout
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left: Orb View
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                    ) {
                        OrbView(
                            config = config,
                            audioLevel = audioLevel,
                            previewScene = previewScene,
                            isZh = isZh,
                            modifier = Modifier.fillMaxSize(),
                            onDoubleTap = { viewModel.toggleAIState() }
                        )

                        // Scene Selector Overlaid at top
                        SceneSelectorBar(
                            currentScene = previewScene,
                            isZh = isZh,
                            onSelectScene = { viewModel.setPreviewScene(it) },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 10.dp)
                        )
                    }

                    // Right: Inspector Panel
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFF10131C))
                    ) {
                        PresetSelectorBar(
                            presets = allPresets,
                            selectedPresetId = currentPresetId,
                            isZh = isZh,
                            onSelectPreset = { viewModel.selectPreset(it) }
                        )

                        EditorTabs(
                            selectedTab = selectedParamTab,
                            isZh = isZh,
                            onTabSelected = { selectedParamTab = it }
                        )

                        EditorTabContent(
                            tabIndex = selectedParamTab,
                            config = config,
                            audioMode = audioMode,
                            audioLevel = audioLevel,
                            isZh = isZh,
                            onUpdate = { viewModel.updateConfig(it) },
                            onToggleAIState = { viewModel.toggleAIState() },
                            onSelectAudioMode = { mode ->
                                if (mode == AudioMode.MICROPHONE) {
                                    val hasMic = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (!hasMic) {
                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    } else {
                                        viewModel.setAudioMode(AudioMode.MICROPHONE)
                                    }
                                } else {
                                    viewModel.setAudioMode(mode)
                                }
                            },
                            onRequestColorPicker = { activeColorPickerTarget = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Mobile Portrait Layout
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top: Orb Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f)
                    ) {
                        OrbView(
                            config = config,
                            audioLevel = audioLevel,
                            previewScene = previewScene,
                            isZh = isZh,
                            modifier = Modifier.fillMaxSize(),
                            onDoubleTap = { viewModel.toggleAIState() }
                        )

                        // Scene Selector floating
                        SceneSelectorBar(
                            currentScene = previewScene,
                            isZh = isZh,
                            onSelectScene = { viewModel.setPreviewScene(it) },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 6.dp)
                        )
                    }

                    // Presets Carousel
                    PresetSelectorBar(
                        presets = allPresets,
                        selectedPresetId = currentPresetId,
                        isZh = isZh,
                        onSelectPreset = { viewModel.selectPreset(it) }
                    )

                    // Tabs
                    EditorTabs(
                        selectedTab = selectedParamTab,
                        isZh = isZh,
                        onTabSelected = { selectedParamTab = it }
                    )

                    // Inspector controls
                    EditorTabContent(
                        tabIndex = selectedParamTab,
                        config = config,
                        audioMode = audioMode,
                        audioLevel = audioLevel,
                        isZh = isZh,
                        onUpdate = { viewModel.updateConfig(it) },
                        onToggleAIState = { viewModel.toggleAIState() },
                        onSelectAudioMode = { mode ->
                            if (mode == AudioMode.MICROPHONE) {
                                val hasMic = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                                if (!hasMic) {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    viewModel.setAudioMode(AudioMode.MICROPHONE)
                                }
                            } else {
                                viewModel.setAudioMode(mode)
                            }
                        },
                        onRequestColorPicker = { activeColorPickerTarget = it },
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }
        }
    }

    // Color Picker Sheet
    activeColorPickerTarget?.let { target ->
        val (title, currentColor) = when (target) {
            "primary" -> (if (isZh) "主色调" else "Primary Color") to config.primaryColor
            "secondary" -> (if (isZh) "次色调" else "Secondary Color") to config.secondaryColor
            "accent" -> (if (isZh) "强调色" else "Accent Color") to config.accentColor
            "core" -> (if (isZh) "核心深色" else "Core Color") to config.coreColor
            "glow" -> (if (isZh) "外部光晕色" else "Outer Glow Color") to config.glowColor
            else -> ("Color" to Color.White)
        }

        ColorPickerSheet(
            title = title,
            initialColor = currentColor,
            onColorSelected = { newColor ->
                viewModel.updateConfig { current ->
                    when (target) {
                        "primary" -> current.copy(primaryColor = newColor)
                        "secondary" -> current.copy(secondaryColor = newColor)
                        "accent" -> current.copy(accentColor = newColor)
                        "core" -> current.copy(coreColor = newColor)
                        "glow" -> current.copy(glowColor = newColor)
                        else -> current
                    }
                }
            },
            onDismiss = { activeColorPickerTarget = null }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        CodeExportDialog(
            config = config,
            isZh = isZh,
            onDismiss = { showExportDialog = false }
        )
    }

    // Preset Manager Dialog
    if (showPresetDialog) {
        PresetManagerDialog(
            savedPresets = savedPresets,
            isZh = isZh,
            onSaveCurrent = { viewModel.saveCurrentAsCustom(it) },
            onSelectPreset = { viewModel.selectPreset(it) },
            onDeletePreset = { viewModel.deleteCustomPreset(it) },
            onResetToDefault = { viewModel.resetToCurrentPreset() },
            onDismiss = { showPresetDialog = false }
        )
    }
}

@Composable
fun SceneSelectorBar(
    currentScene: PreviewScene,
    isZh: Boolean,
    onSelectScene: (PreviewScene) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color(0xFF131722).copy(alpha = 0.85f), RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        PreviewScene.values().forEach { scene ->
            val isSelected = scene == currentScene
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelectScene(scene) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isZh) scene.labelZh else scene.labelEn,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EditorTabs(
    selectedTab: Int,
    isZh: Boolean,
    onTabSelected: (Int) -> Unit
) {
    val tabTitles = if (isZh) {
        listOf("AI与运动", "颜色外观", "玻璃折射", "高光辉光", "阳光·水草·水滴", "音频响应")
    } else {
        listOf("AI & Motion", "Colors", "Refraction", "Highlights", "Sun & Seagrass", "Audio")
    }

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFF0F121A),
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 12.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == index) Color.White else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
fun EditorTabContent(
    tabIndex: Int,
    config: OrbConfig,
    audioMode: AudioMode,
    audioLevel: Float,
    isZh: Boolean,
    onUpdate: ((OrbConfig) -> OrbConfig) -> Unit,
    onToggleAIState: () -> Unit,
    onSelectAudioMode: (AudioMode) -> Unit,
    onRequestColorPicker: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF10131C))
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        when (tabIndex) {
            0 -> {
                // Tab 0: AI & Motion
                AIStateCard(config = config, isZh = isZh, onToggle = onToggleAIState)
                Spacer(modifier = Modifier.height(10.dp))

                LabeledSlider(
                    label = if (isZh) "运动速度 (Speed)" else "Animation Speed",
                    value = config.speed,
                    range = 0.1f..3.0f,
                    onValueChange = { v -> onUpdate { it.copy(speed = v) } },
                    valueFormatter = { String.format("%.1fx", it) },
                    defaultValue = 1.0f,
                    testTag = "speed_slider"
                )

                LabeledSlider(
                    label = if (isZh) "流体湍流度 (Turbulence)" else "Fluid Turbulence",
                    value = config.turbulence,
                    range = 0.0f..1.0f,
                    onValueChange = { v -> onUpdate { it.copy(turbulence = v) } },
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    defaultValue = 0.45f,
                    testTag = "turbulence_slider"
                )

                LabeledSlider(
                    label = if (isZh) "波动节点数 (Wave Count)" else "Wave Complexity",
                    value = config.waveCount.toFloat(),
                    range = 1.0f..6.0f,
                    onValueChange = { v -> onUpdate { it.copy(waveCount = v.toInt()) } },
                    valueFormatter = { "${it.toInt()} 阶" },
                    defaultValue = 3.0f,
                    testTag = "wave_count_slider"
                )

                LabeledSlider(
                    label = if (isZh) "激活响应时长 (Activation)" else "Activation Duration",
                    value = config.activationDuration,
                    range = 0.1f..2.0f,
                    onValueChange = { v -> onUpdate { it.copy(activationDuration = v) } },
                    valueFormatter = { String.format("%.1fs", it) },
                    defaultValue = 0.6f
                )

                LabeledSlider(
                    label = if (isZh) "平息过度时长 (Settle)" else "Settle Duration",
                    value = config.settleDuration,
                    range = 0.1f..2.0f,
                    onValueChange = { v -> onUpdate { it.copy(settleDuration = v) } },
                    valueFormatter = { String.format("%.1fs", it) },
                    defaultValue = 0.8f
                )
            }
            1 -> {
                // Tab 1: Colors
                Text(
                    text = if (isZh) "多层色彩着色器" else "Shader Color Layers",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))

                ColorButtonRow(
                    label = if (isZh) "主波纹色彩 (Primary)" else "Primary Gradient Color",
                    color = config.primaryColor,
                    onClick = { onRequestColorPicker("primary") }
                )

                ColorButtonRow(
                    label = if (isZh) "次波纹色彩 (Secondary)" else "Secondary Gradient Color",
                    color = config.secondaryColor,
                    onClick = { onRequestColorPicker("secondary") }
                )

                ColorButtonRow(
                    label = if (isZh) "高亮强调色彩 (Accent)" else "Accent Highlight Color",
                    color = config.accentColor,
                    onClick = { onRequestColorPicker("accent") }
                )

                ColorButtonRow(
                    label = if (isZh) "内部核心色彩 (Core)" else "Inner Nucleus Color",
                    color = config.coreColor,
                    onClick = { onRequestColorPicker("core") }
                )

                ColorButtonRow(
                    label = if (isZh) "外部光晕色彩 (Glow)" else "Outer Halo Glow Color",
                    color = config.glowColor,
                    onClick = { onRequestColorPicker("glow") }
                )

                Spacer(modifier = Modifier.height(8.dp))
                LabeledSlider(
                    label = if (isZh) "边缘色彩过渡锐度 (Contour)" else "Contour Sharpness",
                    value = config.contourSharpness,
                    range = 0.0f..1.0f,
                    onValueChange = { v -> onUpdate { it.copy(contourSharpness = v) } },
                    valueFormatter = { "${(it * 100).toInt()}%" }
                )
            }
            2 -> {
                // Tab 2: Glass & Refraction
                LabeledSwitch(
                    label = if (isZh) "启用外层玻璃外壳 (Glass Shell)" else "Outer Glass Shell",
                    checked = config.hasGlassShell,
                    onCheckedChange = { chk -> onUpdate { it.copy(hasGlassShell = chk) } },
                    description = if (isZh) "模拟3D实体光学球体物理反射" else "Simulates 3D optical sphere reflections"
                )

                if (config.hasGlassShell) {
                    LabeledSlider(
                        label = if (isZh) "折射强度 (Refraction Strength)" else "Refraction Strength",
                        value = config.refractionStrength,
                        range = 0.0f..1.0f,
                        onValueChange = { v -> onUpdate { it.copy(refractionStrength = v) } },
                        valueFormatter = { "${(it * 100).toInt()}%" },
                        defaultValue = 0.65f
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isZh) "色散类型 (Dispersion Mode)" else "Dispersion Mode",
                        color = Color(0xFFD1D5DB),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DispersionType.values().forEach { disp ->
                            val isSel = config.dispersionType == disp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color(0xFF1E2330))
                                    .clickable { onUpdate { it.copy(dispersionType = disp) } }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isZh) disp.labelZh else disp.labelEn,
                                    color = if (isSel) Color.White else Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LabeledSlider(
                        label = if (isZh) "色散分离距离 (Dispersion Spread)" else "Dispersion Spread",
                        value = config.dispersionStrength,
                        range = 0.0f..1.0f,
                        onValueChange = { v -> onUpdate { it.copy(dispersionStrength = v) } },
                        valueFormatter = { "${(it * 100).toInt()}%" }
                    )

                    LabeledSlider(
                        label = if (isZh) "玻璃壁厚度 (Refraction Width)" else "Shell Wall Width",
                        value = config.refractionWidth,
                        range = 0.05f..0.8f,
                        onValueChange = { v -> onUpdate { it.copy(refractionWidth = v) } },
                        valueFormatter = { "${(it * 100).toInt()}%" }
                    )

                    LabeledSlider(
                        label = if (isZh) "菲涅尔边缘亮度 (Edge Intensity)" else "Fresnel Edge Intensity",
                        value = config.edgeIntensity,
                        range = 0.0f..1.0f,
                        onValueChange = { v -> onUpdate { it.copy(edgeIntensity = v) } },
                        valueFormatter = { "${(it * 100).toInt()}%" }
                    )
                }
            }
            3 -> {
                // Tab 3: Highlights & Glow
                LabeledSlider(
                    label = if (isZh) "外轮廓边缘光 (Rim Highlight)" else "Rim Highlight Sheen",
                    value = config.rimHighlight,
                    range = 0.0f..1.0f,
                    onValueChange = { v -> onUpdate { it.copy(rimHighlight = v) } },
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    defaultValue = 0.8f
                )

                LabeledSlider(
                    label = if (isZh) "主镜面高光 (Key Specular)" else "Key Specular Highlight",
                    value = config.keyHighlight,
                    range = 0.0f..1.0f,
                    onValueChange = { v -> onUpdate { it.copy(keyHighlight = v) } },
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    defaultValue = 0.85f
                )

                LabeledSlider(
                    label = if (isZh) "辅助环境反射 (Fill Highlight)" else "Fill Ambient Highlight",
                    value = config.fillHighlight,
                    range = 0.0f..1.0f,
                    onValueChange = { v -> onUpdate { it.copy(fillHighlight = v) } },
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    defaultValue = 0.4f
                )

                Spacer(modifier = Modifier.height(10.dp))
                LabeledSlider(
                    label = if (isZh) "外部光晕扩散半径 (Glow Radius)" else "Outer Glow Radius",
                    value = config.outerGlowRadius,
                    range = 0.1f..1.0f,
                    onValueChange = { v -> onUpdate { it.copy(outerGlowRadius = v) } },
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    defaultValue = 0.55f
                )

                LabeledSlider(
                    label = if (isZh) "外部光晕强度 (Glow Intensity)" else "Outer Glow Intensity",
                    value = config.outerGlowIntensity,
                    range = 0.0f..1.0f,
                    onValueChange = { v -> onUpdate { it.copy(outerGlowIntensity = v) } },
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    defaultValue = 0.7f
                )
            }
            4 -> {
                // Tab 4: 阳光·水草·水滴 生态系统 (Ecosystem Elements)
                Text(
                    text = if (isZh) "渔获识别水生核心生态要素" else "Fishery Ecosystem Elements",
                    color = Color(0xFF34D399),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                LabeledSlider(
                    label = if (isZh) "阳光穿透与星芒耀斑 (Sunlight Flare)" else "Sunlight Flare & Glint",
                    value = config.sunlightIntensity,
                    range = 0.0f..1.0f,
                    onValueChange = { v -> onUpdate { it.copy(sunlightIntensity = v) } },
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    defaultValue = 0.95f
                )

                LabeledSlider(
                    label = if (isZh) "水草丝带实体宽度 (Seagrass Width)" else "Seagrass Ribbon Width",
                    value = config.seagrassRibbonWidth,
                    range = 2.0f..18.0f,
                    onValueChange = { v -> onUpdate { it.copy(seagrassRibbonWidth = v) } },
                    valueFormatter = { String.format("%.1fdp", it) },
                    defaultValue = 8.0f
                )

                LabeledSlider(
                    label = if (isZh) "水草水流摆动幅度 (Current Sway)" else "Seagrass Wave Amount",
                    value = config.seagrassWaveAmount,
                    range = 0.1f..2.0f,
                    onValueChange = { v -> onUpdate { it.copy(seagrassWaveAmount = v) } },
                    valueFormatter = { String.format("%.1fx", it) },
                    defaultValue = 0.85f
                )

                LabeledSlider(
                    label = if (isZh) "悬浮晶莹微水滴数 (Water Droplets)" else "Floating Micro-Droplets",
                    value = config.waterDropletCount.toFloat(),
                    range = 0f..24f,
                    onValueChange = { v -> onUpdate { it.copy(waterDropletCount = v.toInt()) } },
                    valueFormatter = { "${it.toInt()} 颗" },
                    defaultValue = 12f
                )

                Spacer(modifier = Modifier.height(10.dp))
                LabeledSwitch(
                    label = if (isZh) "启用五条水草与金光带 (External Ribbons)" else "Enable 5 Orbiting Ribbons",
                    checked = config.hasFilaments,
                    onCheckedChange = { chk -> onUpdate { it.copy(hasFilaments = chk) } },
                    description = if (isZh) "3D空间环绕映射的暖金阳光带与翡翠水草带" else "3D toroidal orbiting golden & emerald water ribbons"
                )

                if (config.hasFilaments) {
                    LabeledSlider(
                        label = if (isZh) "水草丝带可见度 (Visibility)" else "Ribbon Visibility",
                        value = config.filamentVisibility,
                        range = 0.0f..1.0f,
                        onValueChange = { v -> onUpdate { it.copy(filamentVisibility = v) } },
                        valueFormatter = { "${(it * 100).toInt()}%" },
                        defaultValue = 0.95f
                    )

                    LabeledSlider(
                        label = if (isZh) "几何缠绕螺旋度 (Twist)" else "Geometric Twist",
                        value = config.filamentTwist,
                        range = 0.5f..5.0f,
                        onValueChange = { v -> onUpdate { it.copy(filamentTwist = v) } },
                        valueFormatter = { String.format("%.1fπ", it) },
                        defaultValue = 2.8f
                    )

                    LabeledSlider(
                        label = if (isZh) "外部浮动半径比 (Radius Ratio)" else "Orbital Radius Ratio",
                        value = config.filamentRadiusRatio,
                        range = 1.05f..1.45f,
                        onValueChange = { v -> onUpdate { it.copy(filamentRadiusRatio = v) } },
                        valueFormatter = { String.format("%.2fR", it) },
                        defaultValue = 1.22f
                    )

                    LabeledSlider(
                        label = if (isZh) "轨道公转流动速度 (Orbit Speed)" else "Orbital Speed",
                        value = config.filamentOrbitSpeed,
                        range = 0.2f..3.0f,
                        onValueChange = { v -> onUpdate { it.copy(filamentOrbitSpeed = v) } },
                        valueFormatter = { String.format("%.1fx", it) },
                        defaultValue = 1.0f
                    )
                }
            }
            5 -> {
                // Tab 5: Audio Reactivity
                LabeledSwitch(
                    label = if (isZh) "启用音频响应 (Audio Responsive)" else "Audio Reactivity",
                    checked = config.isAudioResponsive,
                    onCheckedChange = { chk -> onUpdate { it.copy(isAudioResponsive = chk) } },
                    description = if (isZh) "球体随着音频振幅与节奏律动膨胀" else "Orb reacts dynamically to sound frequencies"
                )

                if (config.isAudioResponsive) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isZh) "音频输入模式 (Audio Input Mode)" else "Audio Input Mode",
                        color = Color(0xFFD1D5DB),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AudioMode.values().forEach { mode ->
                            val isSel = audioMode == mode
                            val label = when (mode) {
                                AudioMode.OFF -> if (isZh) "关闭" else "Mute"
                                AudioMode.SIMULATED_RHYTHM -> if (isZh) "模拟律动" else "Simulated"
                                AudioMode.MICROPHONE -> if (isZh) "实时麦克风" else "Microphone"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color(0xFF1E2330))
                                    .clickable { onSelectAudioMode(mode) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color.White else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Level Meter
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF171B26))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isZh) "实时振幅监测" else "Live Audio Amplitude",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${(audioLevel * 100).toInt()}%",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF262C3D))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(audioLevel.coerceIn(0f, 1f))
                                    .height(6.dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LabeledSlider(
                        label = if (isZh) "麦克风灵敏度 (Sensitivity)" else "Microphone Sensitivity",
                        value = config.micSensitivity,
                        range = 0.2f..3.0f,
                        onValueChange = { v -> onUpdate { it.copy(micSensitivity = v) } },
                        valueFormatter = { String.format("%.1fx", it) },
                        defaultValue = 1.2f
                    )

                    LabeledSlider(
                        label = if (isZh) "球体脉冲缩放幅度 (Scale)" else "Orb Response Scale",
                        value = config.audioResponseScale,
                        range = 0.1f..1.0f,
                        onValueChange = { v -> onUpdate { it.copy(audioResponseScale = v) } },
                        valueFormatter = { "${(it * 100).toInt()}%" },
                        defaultValue = 0.6f
                    )
                }
            }
        }
    }
}

@Composable
fun AIStateCard(
    config: OrbConfig,
    isZh: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (config.aiState == AIState.THINKING) Color(0xFF2A1C3E) else Color(0xFF161B26)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (config.aiState == AIState.THINKING) config.accentColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI State",
                    tint = if (config.aiState == AIState.THINKING) config.accentColor else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isZh) "AI 交互状态 (AI State)" else "AI Interactive State",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (config.aiState == AIState.THINKING) {
                            if (isZh) "思考中：高速涡流与多重能量脉冲" else "Thinking: high-speed vortex and energetic pulses"
                        } else {
                            if (isZh) "空闲中：舒缓呼吸式自然波动" else "Idle: serene rhythmic breathing motion"
                        },
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (config.aiState == AIState.THINKING) config.accentColor else Color(0xFF262C3D))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isZh) config.aiState.labelZh else config.aiState.labelEn,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ColorButtonRow(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF161A24))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFFD1D5DB),
            fontSize = 13.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            val argb = color.toArgb()
            Text(
                text = String.format("#%06X", 0xFFFFFF and argb),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}
