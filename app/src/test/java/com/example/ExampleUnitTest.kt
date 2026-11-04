package com.example

import com.example.model.AIState
import com.example.model.DispersionType
import com.example.model.OrbConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun verifyBuiltInPresetsExist() {
        assertEquals(14, OrbConfig.PRESETS.size)
        val presetIds = OrbConfig.PRESETS.map { it.id }
        assertTrue(presetIds.contains("catch_ai_orb"))
        assertTrue(presetIds.contains("siri_wave"))
        assertTrue(presetIds.contains("voice_membrane"))
        assertTrue(presetIds.contains("particle_ribbons"))
        assertTrue(presetIds.contains("crystal_drop"))
        assertTrue(presetIds.contains("violet_ember"))
        assertTrue(presetIds.contains("refractive_gel"))
        assertTrue(presetIds.contains("chromatic_metal"))
        assertTrue(presetIds.contains("aurora_veil"))
        assertTrue(presetIds.contains("frost_flow"))
        assertTrue(presetIds.contains("liquid_chrome"))
        assertTrue(presetIds.contains("iridescent_opal"))
        assertTrue(presetIds.contains("prismatic_field"))
        assertTrue(presetIds.contains("neural_plasma"))
    }

    @Test
    fun verifyJsonExportContainsRequiredFields() {
        val siriWave = OrbConfig.PRESETS.first { it.id == "siri_wave" }
        val json = siriWave.exportToJson()

        assertTrue(json.contains("\"preset\": \"siri_wave\""))
        assertTrue(json.contains("\"aiState\": \"THINKING\""))
        assertTrue(json.contains("\"speed\": 1.2"))
        assertTrue(json.contains("\"refractionStrength\": 0.7"))
    }

    @Test
    fun verifyWGSLShaderExport() {
        val preset = OrbConfig.PRESETS.first()
        val wgsl = preset.exportToWebWGSL()

        assertTrue(wgsl.contains("@fragment"))
        assertTrue(wgsl.contains("fs_main"))
        assertTrue(wgsl.contains("struct OrbUniforms"))
    }

    @Test
    fun verifySwiftUIMetalExport() {
        val preset = OrbConfig.PRESETS.first()
        val metal = preset.exportToSwiftUIMetal()

        assertTrue(metal.contains("struct LiquidOrbView: View"))
        assertTrue(metal.contains("Canvas"))
    }

    @Test
    fun verifyR06FilamentsDefaultConfiguration() {
        val preset = OrbConfig.PRESETS.first()
        assertTrue(preset.hasFilaments)
        assertEquals(5, preset.filamentCount)
        assertTrue(preset.filamentVisibility > 0f)
        val json = preset.exportToJson()
        assertTrue(json.contains("\"r06Filaments\""))
        assertTrue(json.contains("\"count\": 5"))
    }
}
