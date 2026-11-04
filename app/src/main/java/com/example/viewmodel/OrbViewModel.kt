package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioMode
import com.example.audio.AudioProcessor
import com.example.model.AIState
import com.example.model.DispersionType
import com.example.model.OrbConfig
import com.example.model.PreviewScene
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.random.Random

class OrbViewModel(application: Application) : AndroidViewModel(application) {

    private val audioProcessor = AudioProcessor(application.applicationContext, viewModelScope)

    // Current config
    private val _currentConfig = MutableStateFlow(OrbConfig.PRESETS.first())
    val currentConfig: StateFlow<OrbConfig> = _currentConfig.asStateFlow()

    // Current preset ID
    private val _currentPresetId = MutableStateFlow(OrbConfig.PRESETS.first().id)
    val currentPresetId: StateFlow<String> = _currentPresetId.asStateFlow()

    // Preview Scene
    private val _previewScene = MutableStateFlow(PreviewScene.STUDIO)
    val previewScene: StateFlow<PreviewScene> = _previewScene.asStateFlow()

    // Language: "zh" or "en"
    private val isDefaultZh = Locale.getDefault().language.startsWith("zh")
    private val _language = MutableStateFlow(if (isDefaultZh) "zh" else "en")
    val language: StateFlow<String> = _language.asStateFlow()

    // Fullscreen state
    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    // User saved presets
    private val _savedPresets = MutableStateFlow<List<OrbConfig>>(emptyList())
    val savedPresets: StateFlow<List<OrbConfig>> = _savedPresets.asStateFlow()

    // Audio level directly from audio processor
    val audioLevel: StateFlow<Float> = audioProcessor.audioLevel
    val audioMode: StateFlow<AudioMode> = audioProcessor.audioMode

    init {
        audioProcessor.start()
    }

    override fun onCleared() {
        super.onCleared()
        audioProcessor.stop()
    }

    fun selectPreset(preset: OrbConfig) {
        _currentPresetId.value = preset.id
        _currentConfig.value = preset
    }

    fun updateConfig(transform: (OrbConfig) -> OrbConfig) {
        _currentConfig.value = transform(_currentConfig.value)
    }

    fun toggleAIState() {
        val next = if (_currentConfig.value.aiState == AIState.IDLE) AIState.THINKING else AIState.IDLE
        _currentConfig.value = _currentConfig.value.copy(aiState = next)
    }

    fun setAIState(state: AIState) {
        _currentConfig.value = _currentConfig.value.copy(aiState = state)
    }

    fun setPreviewScene(scene: PreviewScene) {
        _previewScene.value = scene
    }

    fun setAudioMode(mode: AudioMode) {
        audioProcessor.setAudioMode(mode)
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == "zh") "en" else "zh"
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun resetToCurrentPreset() {
        val original = OrbConfig.PRESETS.find { it.id == _currentPresetId.value }
            ?: _savedPresets.value.find { it.id == _currentPresetId.value }
            ?: OrbConfig.PRESETS.first()
        _currentConfig.value = original
    }

    fun randomize() {
        val randomHue1 = Random.nextFloat() * 360f
        val randomHue2 = (randomHue1 + 120f + Random.nextFloat() * 60f) % 360f
        val randomHue3 = (randomHue1 + 240f + Random.nextFloat() * 60f) % 360f

        val color1 = Color.hsl(randomHue1, 0.85f, 0.55f)
        val color2 = Color.hsl(randomHue2, 0.9f, 0.5f)
        val color3 = Color.hsl(randomHue3, 0.8f, 0.65f)
        val core = Color.hsl(randomHue1, 0.7f, 0.25f)

        _currentPresetId.value = "random_${System.currentTimeMillis()}"
        _currentConfig.value = _currentConfig.value.copy(
            id = _currentPresetId.value,
            nameEn = "Prism Morph #${Random.nextInt(100, 999)}",
            nameZh = "幻彩形态 #${Random.nextInt(100, 999)}",
            speed = 0.6f + Random.nextFloat() * 1.6f,
            turbulence = 0.3f + Random.nextFloat() * 0.5f,
            waveCount = Random.nextInt(2, 6),
            primaryColor = color1,
            secondaryColor = color2,
            accentColor = color3,
            coreColor = core,
            glowColor = color1,
            refractionStrength = 0.5f + Random.nextFloat() * 0.45f,
            dispersionStrength = 0.3f + Random.nextFloat() * 0.6f,
            outerGlowIntensity = 0.6f + Random.nextFloat() * 0.35f,
            keyHighlight = 0.75f + Random.nextFloat() * 0.25f,
            rimHighlight = 0.7f + Random.nextFloat() * 0.25f
        )
    }

    fun saveCurrentAsCustom(name: String) {
        val id = "custom_${System.currentTimeMillis()}"
        val custom = _currentConfig.value.copy(
            id = id,
            nameEn = name,
            nameZh = name
        )
        _savedPresets.value = _savedPresets.value + custom
        _currentPresetId.value = id
    }

    fun deleteCustomPreset(presetId: String) {
        _savedPresets.value = _savedPresets.value.filter { it.id != presetId }
        if (_currentPresetId.value == presetId) {
            selectPreset(OrbConfig.PRESETS.first())
        }
    }
}
