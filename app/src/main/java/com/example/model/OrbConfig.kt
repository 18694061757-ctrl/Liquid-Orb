package com.example.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class AIState(val labelEn: String, val labelZh: String) {
    IDLE("Idle", "空闲"),
    THINKING("Thinking", "思考中")
}

enum class DispersionType(val labelEn: String, val labelZh: String) {
    COOL("Cool", "冷色调"),
    WARM("Warm", "暖色调"),
    NEUTRAL("Neutral", "中性")
}

enum class PreviewScene(val labelEn: String, val labelZh: String) {
    CATCH_AI("Catch AI", "渔获识别"),
    STUDIO("Studio", "水生工坊"),
    VOICE_ASSISTANT("Voice AI", "语音助手"),
    MUSIC_PLAYER("Music Player", "音乐律动"),
    CYBER_HUD("Cyber HUD", "赛博核心")
}

data class OrbConfig(
    val id: String = "catch_ai_orb",
    val nameEn: String = "Catch AI Orb",
    val nameZh: String = "渔获识别·水光生态",
    val isAudioResponsive: Boolean = true,

    // AI & Motion
    val aiState: AIState = AIState.IDLE,
    val speed: Float = 1.0f,               // 0.1 .. 3.0
    val turbulence: Float = 0.45f,         // 0.0 .. 1.0
    val waveCount: Int = 3,                // 1 .. 6
    val activationDuration: Float = 0.6f,  // 0.1 .. 2.0s
    val settleDuration: Float = 0.8f,      // 0.1 .. 2.0s

    // Colors
    val primaryColor: Color = Color(0xFF0284C7),    // Crystal Oceanic Cyan (#0284C7)
    val secondaryColor: Color = Color(0xFF0D9488),  // Deep Sea Kelp Teal (#0D9488)
    val accentColor: Color = Color(0xFFF59E0B),     // Sunlight Golden Flare (#F59E0B)
    val coreColor: Color = Color(0xFF01121A),       // Abyssal Deep Sea Midnight Navy (#01121A)
    val contourSharpness: Float = 0.75f,            // 0.0 .. 1.0

    // Glass Shell & Refraction
    val hasGlassShell: Boolean = true,
    val refractionStrength: Float = 0.95f,          // 0.0 .. 1.0
    val dispersionType: DispersionType = DispersionType.COOL,
    val dispersionStrength: Float = 0.45f,          // 0.0 .. 1.0
    val refractionWidth: Float = 0.35f,             // 0.05 .. 1.0
    val edgeIntensity: Float = 0.95f,               // 0.0 .. 1.0

    // Highlights & Glow
    val rimHighlight: Float = 0.95f,                // 0.0 .. 1.0
    val keyHighlight: Float = 0.98f,                // 0.0 .. 1.0
    val fillHighlight: Float = 0.40f,               // 0.0 .. 1.0
    val outerGlowRadius: Float = 0.32f,             // 0.0 .. 1.0 (refined contrast preservation)
    val outerGlowIntensity: Float = 0.22f,          // 0.0 .. 1.0 (eliminates haze washout)
    val glowColor: Color = Color(0xFF02212B),

    // Audio Reactivity
    val micSensitivity: Float = 1.2f,               // 0.1 .. 3.0
    val audioResponseScale: Float = 0.6f,           // 0.0 .. 1.0

    // R06 External Filaments (5条外部光丝 / 水草与金光带)
    val hasFilaments: Boolean = true,
    val filamentCount: Int = 5,
    val filamentVisibility: Float = 0.98f,          // 0.0 .. 1.0 可见度
    val filamentOrbitSpeed: Float = 0.95f,          // 0.1 .. 3.0 公转与流动速度
    val filamentTwist: Float = 2.8f,                // 0.5 .. 5.0 几何扭曲螺旋度
    val filamentRadiusRatio: Float = 1.22f,         // 1.05 .. 1.45 外部几何浮动半径
    val filamentThickness: Float = 4.0f,            // 1.0 .. 8.0 光丝粗细
    val filamentAudioExpand: Float = 0.35f,         // 0.0 .. 1.0 音频振幅扩张

    // Ecosystem: 阳光、水草、水滴 (Sunlight, Seagrass, Droplets)
    val sunlightIntensity: Float = 1.0f,            // 阳光透射与星芒耀斑强度
    val seagrassRibbonWidth: Float = 14.0f,         // 水草丝带实体厚度 (wider, leaf-like)
    val seagrassWaveAmount: Float = 0.92f,          // 水草水流柔顺摆动感
    val waterDropletCount: Int = 14,                // 悬浮晶莹微水滴数量
    val sunlightAngle: Float = 125f                 // 阳光入射角 (11点钟方向)
) {
    companion object {
        val PRESETS: List<OrbConfig> = listOf(
            OrbConfig(
                id = "catch_ai_orb",
                nameEn = "Catch AI Aquatic",
                nameZh = "渔获识别·水光生态",
                isAudioResponsive = true,
                aiState = AIState.IDLE,
                speed = 1.05f,
                turbulence = 0.48f,
                waveCount = 4,
                primaryColor = Color(0xFF0284C7),
                secondaryColor = Color(0xFF0D9488),
                accentColor = Color(0xFFF59E0B),
                coreColor = Color(0xFF01121A),
                glowColor = Color(0xFF02212B),
                outerGlowIntensity = 0.22f,
                outerGlowRadius = 0.32f,
                sunlightIntensity = 1.0f,
                seagrassRibbonWidth = 14.0f,
                seagrassWaveAmount = 0.92f,
                waterDropletCount = 14,
                hasFilaments = true,
                filamentCount = 5,
                filamentVisibility = 0.98f,
                filamentTwist = 2.8f,
                filamentRadiusRatio = 1.22f,
                filamentThickness = 4.0f,
                refractionStrength = 0.95f,
                edgeIntensity = 0.95f,
                keyHighlight = 0.98f,
                rimHighlight = 0.95f
            ),
            OrbConfig(
                id = "siri_wave",
                nameEn = "Siri Wave",
                nameZh = "Siri 声波",
                isAudioResponsive = true,
                aiState = AIState.THINKING,
                speed = 1.2f,
                turbulence = 0.55f,
                waveCount = 4,
                primaryColor = Color(0xFF8B5CF6),
                secondaryColor = Color(0xFF06B6D4),
                accentColor = Color(0xFFD946EF),
                coreColor = Color(0xFF3B82F6),
                refractionStrength = 0.7f,
                dispersionType = DispersionType.COOL,
                dispersionStrength = 0.5f,
                rimHighlight = 0.85f,
                keyHighlight = 0.9f,
                outerGlowIntensity = 0.8f,
                glowColor = Color(0xFF8B5CF6)
            ),
            OrbConfig(
                id = "voice_membrane",
                nameEn = "Voice Membrane",
                nameZh = "语音膜",
                isAudioResponsive = true,
                aiState = AIState.IDLE,
                speed = 0.9f,
                turbulence = 0.4f,
                waveCount = 3,
                primaryColor = Color(0xFF10B981),
                secondaryColor = Color(0xFF14B8A6),
                accentColor = Color(0xFF6EE7B7),
                coreColor = Color(0xFF047857),
                refractionStrength = 0.6f,
                dispersionType = DispersionType.NEUTRAL,
                dispersionStrength = 0.35f,
                rimHighlight = 0.75f,
                keyHighlight = 0.8f,
                outerGlowIntensity = 0.65f,
                glowColor = Color(0xFF10B981)
            ),
            OrbConfig(
                id = "particle_ribbons",
                nameEn = "Particle Ribbons",
                nameZh = "粒子流光带",
                isAudioResponsive = false,
                aiState = AIState.THINKING,
                speed = 1.4f,
                turbulence = 0.68f,
                waveCount = 5,
                primaryColor = Color(0xFFF59E0B),
                secondaryColor = Color(0xFFF43F5E),
                accentColor = Color(0xFFFB7185),
                coreColor = Color(0xFFD97706),
                refractionStrength = 0.62f,
                dispersionType = DispersionType.WARM,
                dispersionStrength = 0.6f,
                rimHighlight = 0.8f,
                keyHighlight = 0.88f,
                outerGlowIntensity = 0.75f,
                glowColor = Color(0xFFF59E0B)
            ),
            OrbConfig(
                id = "crystal_drop",
                nameEn = "Crystal Drop",
                nameZh = "水晶水滴",
                isAudioResponsive = false,
                aiState = AIState.IDLE,
                speed = 0.65f,
                turbulence = 0.25f,
                waveCount = 2,
                primaryColor = Color(0xFF38BDF8),
                secondaryColor = Color(0xFFE0F2FE),
                accentColor = Color(0xFFFFFFFF),
                coreColor = Color(0xFF0284C7),
                refractionStrength = 0.92f,
                dispersionType = DispersionType.COOL,
                dispersionStrength = 0.7f,
                refractionWidth = 0.5f,
                rimHighlight = 0.95f,
                keyHighlight = 0.98f,
                fillHighlight = 0.6f,
                outerGlowIntensity = 0.5f,
                glowColor = Color(0xFF38BDF8)
            ),
            OrbConfig(
                id = "violet_ember",
                nameEn = "Violet Ember",
                nameZh = "紫火余烬",
                isAudioResponsive = true,
                aiState = AIState.THINKING,
                speed = 1.35f,
                turbulence = 0.62f,
                waveCount = 4,
                primaryColor = Color(0xFF701A75),
                secondaryColor = Color(0xFFE11D48),
                accentColor = Color(0xFFFB923C),
                coreColor = Color(0xFF4C0519),
                refractionStrength = 0.58f,
                dispersionType = DispersionType.WARM,
                dispersionStrength = 0.55f,
                rimHighlight = 0.82f,
                keyHighlight = 0.78f,
                outerGlowIntensity = 0.85f,
                glowColor = Color(0xFFE11D48)
            ),
            OrbConfig(
                id = "refractive_gel",
                nameEn = "Refractive Gel",
                nameZh = "折射凝胶",
                isAudioResponsive = false,
                aiState = AIState.IDLE,
                speed = 0.75f,
                turbulence = 0.5f,
                waveCount = 3,
                primaryColor = Color(0xFF84CC16),
                secondaryColor = Color(0xFF22C55E),
                accentColor = Color(0xFFD9F99D),
                coreColor = Color(0xFF15803D),
                refractionStrength = 0.88f,
                dispersionType = DispersionType.NEUTRAL,
                dispersionStrength = 0.4f,
                refractionWidth = 0.42f,
                rimHighlight = 0.78f,
                keyHighlight = 0.85f,
                outerGlowIntensity = 0.6f,
                glowColor = Color(0xFF84CC16)
            ),
            OrbConfig(
                id = "chromatic_metal",
                nameEn = "Chromatic Metal",
                nameZh = "多彩金属",
                isAudioResponsive = false,
                aiState = AIState.THINKING,
                speed = 1.15f,
                turbulence = 0.48f,
                waveCount = 4,
                primaryColor = Color(0xFF94A3B8),
                secondaryColor = Color(0xFF6366F1),
                accentColor = Color(0xFFF1F5F9),
                coreColor = Color(0xFF334155),
                refractionStrength = 0.85f,
                dispersionType = DispersionType.COOL,
                dispersionStrength = 0.8f,
                rimHighlight = 0.95f,
                keyHighlight = 0.92f,
                outerGlowIntensity = 0.7f,
                glowColor = Color(0xFF6366F1)
            ),
            OrbConfig(
                id = "aurora_veil",
                nameEn = "Aurora Veil",
                nameZh = "极光轻纱",
                isAudioResponsive = true,
                aiState = AIState.IDLE,
                speed = 0.85f,
                turbulence = 0.58f,
                waveCount = 4,
                primaryColor = Color(0xFF10B981),
                secondaryColor = Color(0xFF8B5CF6),
                accentColor = Color(0xFF06B6D4),
                coreColor = Color(0xFF1E1B4B),
                refractionStrength = 0.65f,
                dispersionType = DispersionType.COOL,
                dispersionStrength = 0.6f,
                rimHighlight = 0.88f,
                keyHighlight = 0.82f,
                outerGlowIntensity = 0.9f,
                glowColor = Color(0xFF10B981)
            ),
            OrbConfig(
                id = "frost_flow",
                nameEn = "Frost Flow",
                nameZh = "霜雪流动",
                isAudioResponsive = false,
                aiState = AIState.IDLE,
                speed = 0.7f,
                turbulence = 0.35f,
                waveCount = 3,
                primaryColor = Color(0xFF38BDF8),
                secondaryColor = Color(0xFFC7D2FE),
                accentColor = Color(0xFFF0F9FF),
                coreColor = Color(0xFF1E3A8A),
                refractionStrength = 0.82f,
                dispersionType = DispersionType.COOL,
                dispersionStrength = 0.65f,
                rimHighlight = 0.9f,
                keyHighlight = 0.92f,
                outerGlowIntensity = 0.65f,
                glowColor = Color(0xFF38BDF8)
            ),
            OrbConfig(
                id = "liquid_chrome",
                nameEn = "Liquid Chrome",
                nameZh = "流动铬金属",
                isAudioResponsive = false,
                aiState = AIState.THINKING,
                speed = 1.25f,
                turbulence = 0.52f,
                waveCount = 4,
                primaryColor = Color(0xFFCBD5E1),
                secondaryColor = Color(0xFF2563EB),
                accentColor = Color(0xFFFFFFFF),
                coreColor = Color(0xFF0F172A),
                refractionStrength = 0.9f,
                dispersionType = DispersionType.NEUTRAL,
                dispersionStrength = 0.45f,
                rimHighlight = 0.98f,
                keyHighlight = 0.95f,
                outerGlowIntensity = 0.72f,
                glowColor = Color(0xFF2563EB)
            ),
            OrbConfig(
                id = "iridescent_opal",
                nameEn = "Iridescent Opal",
                nameZh = "彩虹欧泊",
                isAudioResponsive = false,
                aiState = AIState.IDLE,
                speed = 0.95f,
                turbulence = 0.42f,
                waveCount = 3,
                primaryColor = Color(0xFFDDD6FE),
                secondaryColor = Color(0xFFFED7AA),
                accentColor = Color(0xFFA7F3D0),
                coreColor = Color(0xFFC084FC),
                refractionStrength = 0.78f,
                dispersionType = DispersionType.WARM,
                dispersionStrength = 0.75f,
                rimHighlight = 0.86f,
                keyHighlight = 0.9f,
                outerGlowIntensity = 0.78f,
                glowColor = Color(0xFFDDD6FE)
            ),
            OrbConfig(
                id = "prismatic_field",
                nameEn = "Prismatic Field",
                nameZh = "棱镜色场",
                isAudioResponsive = true,
                aiState = AIState.THINKING,
                speed = 1.4f,
                turbulence = 0.65f,
                waveCount = 5,
                primaryColor = Color(0xFFEF4444),
                secondaryColor = Color(0xFFEAB308),
                accentColor = Color(0xFF3B82F6),
                coreColor = Color(0xFF10B981),
                refractionStrength = 0.85f,
                dispersionType = DispersionType.COOL,
                dispersionStrength = 0.95f,
                rimHighlight = 0.92f,
                keyHighlight = 0.94f,
                outerGlowIntensity = 0.88f,
                glowColor = Color(0xFFEAB308)
            ),
            OrbConfig(
                id = "neural_plasma",
                nameEn = "Neural Plasma",
                nameZh = "神经元等离子",
                isAudioResponsive = true,
                aiState = AIState.THINKING,
                speed = 1.6f,
                turbulence = 0.75f,
                waveCount = 5,
                primaryColor = Color(0xFFEC4899),
                secondaryColor = Color(0xFFA855F7),
                accentColor = Color(0xFF06B6D4),
                coreColor = Color(0xFF312E81),
                refractionStrength = 0.72f,
                dispersionType = DispersionType.WARM,
                dispersionStrength = 0.65f,
                rimHighlight = 0.92f,
                keyHighlight = 0.88f,
                outerGlowIntensity = 0.95f,
                glowColor = Color(0xFFEC4899)
            )
        )
    }

    fun toColorHex(color: Color): String {
        val argb = color.toArgb()
        return String.format("#%06X", 0xFFFFFF and argb)
    }

    fun exportToJson(): String {
        return """
        {
          "preset": "$id",
          "name": "$nameEn",
          "aiState": "${aiState.name}",
          "speed": $speed,
          "turbulence": $turbulence,
          "waveCount": $waveCount,
          "colors": {
            "primary": "${toColorHex(primaryColor)}",
            "secondary": "${toColorHex(secondaryColor)}",
            "accent": "${toColorHex(accentColor)}",
            "core": "${toColorHex(coreColor)}",
            "glow": "${toColorHex(glowColor)}"
          },
          "glass": {
            "enabled": $hasGlassShell,
            "refractionStrength": $refractionStrength,
            "dispersionType": "${dispersionType.name}",
            "dispersionStrength": $dispersionStrength,
            "refractionWidth": $refractionWidth,
            "edgeIntensity": $edgeIntensity
          },
          "highlights": {
            "rim": $rimHighlight,
            "key": $keyHighlight,
            "fill": $fillHighlight,
            "glowRadius": $outerGlowRadius,
            "glowIntensity": $outerGlowIntensity
          },
          "r06Filaments": {
            "enabled": $hasFilaments,
            "count": $filamentCount,
            "visibility": $filamentVisibility,
            "twist": $filamentTwist,
            "radiusRatio": $filamentRadiusRatio,
            "orbitSpeed": $filamentOrbitSpeed,
            "thickness": $filamentThickness,
            "audioExpand": $filamentAudioExpand
          }
        }
        """.trimIndent()
    }

    fun exportToWebWGSL(): String {
        return """
        // --- WebGPU / WGSL Liquid Orb Shader Snippet ---
        // Preset: $nameEn ($id)
        // Feature: AI Orb R06 - 5 External Light Filaments Geometric Mapping
        struct OrbUniforms {
          speed: f32,
          turbulence: f32,
          refractionStrength: f32,
          dispersionStrength: f32,
          rimHighlight: f32,
          filamentVisibility: f32,
          primaryColor: vec4<f32>,
          secondaryColor: vec4<f32>,
          accentColor: vec4<f32>,
          glowColor: vec4<f32>,
        };

        @group(0) @binding(0) var<uniform> orb: OrbUniforms;

        // R06: 5 External Filaments evaluation
        fn evalFilaments(uv: vec2<f32>, time: f32) -> vec4<f32> {
          let center = vec2<f32>(0.5, 0.5);
          let p = uv - center;
          let r = length(p);
          let theta = atan2(p.y, p.x);
          var filamentAcc = vec4<f32>(0.0);

          for (var k: i32 = 0; k < 5; k = k + 1) {
            let phase = f32(k) * 1.256637; // 2.0 * PI / 5.0
            let targetR = 0.42 * ${filamentRadiusRatio}f + sin(theta * ${filamentTwist}f + time + phase) * 0.03;
            let distToLine = abs(r - targetR);
            let taper = sin(clamp((theta + 3.14159) / 6.28318, 0.0, 1.0) * 3.14159);
            let intensity = smoothstep(0.015, 0.0, distToLine) * taper * orb.filamentVisibility;
            filamentAcc = filamentAcc + orb.accentColor * intensity;
          }
          return filamentAcc;
        }

        @fragment
        fn fs_main(@location(0) uv: vec2<f32>) -> @location(0) vec4<f32> {
          let center = vec2<f32>(0.5, 0.5);
          let dist = length(uv - center) * 2.0;
          if (dist > 1.0) {
            let glow = max(0.0, 1.0 - (dist - 1.0) / (${outerGlowRadius}f * 0.8));
            let filamentCol = evalFilaments(uv, 1.0);
            return orb.glowColor * glow * ${outerGlowIntensity}f + filamentCol;
          }
          
          let fresnel = pow(dist, ${2.0f / (edgeIntensity + 0.1f)}f);
          let fluidWave = sin(uv.x * ${waveCount * 4.0f}f + uv.y * ${waveCount * 3.0f}f) * ${turbulence}f;
          var col = mix(orb.primaryColor, orb.secondaryColor, clamp(dist + fluidWave, 0.0, 1.0));
          col = mix(col, orb.accentColor, fresnel * ${rimHighlight}f);
          let filamentOverlay = evalFilaments(uv, 1.0);
          return vec4<f32>(col.rgb + filamentOverlay.rgb, 1.0);
        }
        """.trimIndent()
    }

    fun exportToSwiftUIMetal(): String {
        return """
        // --- SwiftUI + Metal Shader Code Snippet ---
        // Preset: $nameEn ($id)
        import SwiftUI

        struct LiquidOrbView: View {
            @State private var time: Double = 0
            
            var body: some View {
                TimelineView(.animation) { timeline in
                    Canvas { context, size in
                        let center = CGPoint(x: size.width / 2, y: size.height / 2)
                        let radius = min(size.width, size.height) * 0.45
                        
                        // Outer Glow
                        context.fill(
                            Path(ellipseIn: CGRect(x: center.x - radius * 1.3, y: center.y - radius * 1.3, width: radius * 2.6, height: radius * 2.6)),
                            with: .color(Color(hex: "${toColorHex(glowColor)}").opacity(${outerGlowIntensity}))
                        )
                        // Liquid Glass Core
                        context.fill(
                            Path(ellipseIn: CGRect(x: center.x - radius, y: center.y - radius, width: radius * 2, height: radius * 2)),
                            with: .radialGradient(
                                Gradient(colors: [
                                    Color(hex: "${toColorHex(accentColor)}"),
                                    Color(hex: "${toColorHex(primaryColor)}"),
                                    Color(hex: "${toColorHex(secondaryColor)}")
                                ]),
                                center: center,
                                startRadius: 0,
                                endRadius: radius
                            )
                        )
                    }
                }
                .frame(width: 320, height: 320)
            }
        }
        """.trimIndent()
    }

    fun exportToComposeSnippet(): String {
        return """
        // --- Jetpack Compose Liquid Orb Snippet ---
        // Preset: $nameEn ($id)
        @Composable
        fun StandaloneLiquidOrb(modifier: Modifier = Modifier) {
            val infiniteTransition = rememberInfiniteTransition(label = "orb")
            val time by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 6.28318f,
                animationSpec = infiniteRepeatable(
                    animation = tween(${(4000 / speed).toInt()}, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "time"
            )

            Canvas(modifier = modifier.size(280.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = min(size.width, size.height) * 0.42f
                
                // Outer Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x${toColorHex(glowColor).removePrefix("#").uppercase()}).copy(alpha = ${outerGlowIntensity}f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * (1f + ${outerGlowRadius}f)
                    ),
                    radius = radius * (1f + ${outerGlowRadius}f),
                    center = center
                )
                // Fluid Core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x${toColorHex(accentColor).removePrefix("#").uppercase()}),
                            Color(0x${toColorHex(primaryColor).removePrefix("#").uppercase()}),
                            Color(0x${toColorHex(secondaryColor).removePrefix("#").uppercase()})
                        ),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }
        }
        """.trimIndent()
    }
}
