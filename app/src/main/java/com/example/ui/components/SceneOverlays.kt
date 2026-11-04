package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AIState
import com.example.model.OrbConfig
import com.example.model.PreviewScene
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SceneBackdrop(
    scene: PreviewScene,
    isZh: Boolean,
    config: OrbConfig,
    audioLevel: Float
) {
    when (scene) {
        PreviewScene.CATCH_AI -> {
            // Oceanic aquatic atmosphere with diagonal sunbeams and water caustics
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background deep abyss
                drawRect(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF03161C), Color(0xFF06232D), Color(0xFF020E13))
                    )
                )
                // Subtle sunbeams from top-left
                val beamColor = Color(0xFFFEF08A).copy(alpha = 0.04f * config.sunlightIntensity)
                for (i in 0..4) {
                    val startX = size.width * (0.1f + i * 0.18f)
                    drawLine(
                        color = beamColor,
                        start = Offset(startX, 0f),
                        end = Offset(startX + size.width * 0.35f, size.height),
                        strokeWidth = 32.dp.toPx()
                    )
                }
            }
        }
        PreviewScene.STUDIO -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridStep = 40.dp.toPx()
                val dotRadius = 1.2.dp.toPx()
                val cols = (size.width / gridStep).toInt()
                val rows = (size.height / gridStep).toInt()

                for (c in 0..cols) {
                    for (r in 0..rows) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = dotRadius,
                            center = Offset(c * gridStep, r * gridStep)
                        )
                    }
                }
            }
        }
        PreviewScene.VOICE_ASSISTANT -> {
            // Ambient dark gradient with subtle pulsing halo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF07090E))
            )
        }
        PreviewScene.MUSIC_PLAYER -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0814))
            )
        }
        PreviewScene.CYBER_HUD -> {
            val infiniteTransition = rememberInfiniteTransition(label = "hud_rot")
            val rotAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(30000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rot"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = minOf(size.width, size.height) * 0.44f

                // Target rings
                drawCircle(
                    color = config.primaryColor.copy(alpha = 0.18f),
                    radius = baseRadius * 1.15f,
                    center = center,
                    style = Stroke(width = 1.5f)
                )

                drawCircle(
                    color = config.secondaryColor.copy(alpha = 0.12f),
                    radius = baseRadius * 1.28f,
                    center = center,
                    style = Stroke(width = 1f)
                )

                // Reticle ticks
                val tickCount = 24
                for (i in 0 until tickCount) {
                    val angle = Math.toRadians((i * (360.0 / tickCount) + rotAngle).toDouble())
                    val r1 = baseRadius * 1.15f
                    val r2 = baseRadius * 1.18f
                    val p1 = Offset(center.x + (r1 * cos(angle)).toFloat(), center.y + (r1 * sin(angle)).toFloat())
                    val p2 = Offset(center.x + (r2 * cos(angle)).toFloat(), center.y + (r2 * sin(angle)).toFloat())
                    drawLine(
                        color = config.accentColor.copy(alpha = 0.35f),
                        start = p1,
                        end = p2,
                        strokeWidth = 1.5f
                    )
                }
            }
        }
    }
}

@Composable
fun SceneOverlayForeground(
    scene: PreviewScene,
    isZh: Boolean,
    config: OrbConfig,
    audioLevel: Float
) {
    when (scene) {
        PreviewScene.CATCH_AI -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Scanner HUD
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF041922).copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFF59E0B), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isZh) "渔获识别雷达 • 水生生态" else "AI Catch Scanner • Aquatic",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (config.aiState == AIState.THINKING) Color(0xFFF59E0B).copy(alpha = 0.25f) else Color(0xFF064E3B).copy(alpha = 0.7f),
                                RoundedCornerShape(16.dp)
                            )
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (config.aiState == AIState.THINKING) {
                                if (isZh) "正在深度识别鱼种..." else "Analyzing Catch..."
                            } else {
                                if (isZh) "水草与阳光已锁定" else "Ecosystem Locked"
                            },
                            color = if (config.aiState == AIState.THINKING) Color(0xFFFEF08A) else Color(0xFFA7F3D0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Bottom Catch Telemetry Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = Color(0xFF051B24).copy(alpha = 0.9f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFF10B981).copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isZh) "识别鱼种: 加州鲈鱼 (Largemouth Bass)" else "Species: Micropterus salmoides",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "99.4%",
                                color = Color(0xFF34D399),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isZh) "体态健康 • 预估体长 38.5cm • 重量约 1.35kg" else "Healthy condition • Est. 38.5cm • 1.35kg",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isZh) "阳光穿透 ${(config.sunlightIntensity * 100).toInt()}%" else "Sunlight ${(config.sunlightIntensity * 100).toInt()}%",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 10.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isZh) "水草丝带 ${config.seagrassRibbonWidth.toInt()}dp" else "Seagrass ${config.seagrassRibbonWidth.toInt()}dp",
                                    color = Color(0xFF34D399),
                                    fontSize = 10.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF06B6D4).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isZh) "微水滴 ${config.waterDropletCount}颗" else "${config.waterDropletCount} Droplets",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        PreviewScene.STUDIO -> {
            // Bottom stats badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Preset badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFF161922).copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(config.primaryColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isZh) config.nameZh else config.nameEn,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // AI state badge
                Box(
                    modifier = Modifier
                        .background(
                            if (config.aiState == AIState.THINKING)
                                config.accentColor.copy(alpha = 0.2f)
                            else
                                Color(0xFF161922).copy(alpha = 0.85f),
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            1.dp,
                            if (config.aiState == AIState.THINKING) config.accentColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.12f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isZh) config.aiState.labelZh else config.aiState.labelEn,
                        color = if (config.aiState == AIState.THINKING) config.accentColor else Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        PreviewScene.VOICE_ASSISTANT -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top assistant query status
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .background(Color(0xFF131722).copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic",
                            tint = config.primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isZh) "正在聆听语音指令..." else "Listening for instructions...",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }

                // Bottom assistant speech response
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = if (config.aiState == AIState.THINKING) {
                            if (isZh) "正在生成实时流体响应..." else "Synthesizing dynamic response..."
                        } else {
                            if (isZh) "\"请问有什么可以帮助您？\"" else "\"How can I assist you today?\""
                        },
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Audio bar visualizer
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val barCount = 14
                        for (i in 0 until barCount) {
                            val factor = sin(i * 0.45f + audioLevel * 6f).toFloat()
                            val multiplier = (audioLevel * (0.5f + factor * 0.5f)).coerceIn(0f, 1f)
                            val barHeight = (8.dp + (24.dp * multiplier)).coerceIn(4.dp, 32.dp)
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(barHeight)
                                    .background(
                                        if (i % 2 == 0) config.primaryColor else config.accentColor,
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
        PreviewScene.MUSIC_PLAYER -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Track Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Text(
                        text = if (isZh) "超维流体协奏曲" else "Cosmic Orb Harmonics",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isZh) "WebGPU 原声大碟 • 48kHz 无损" else "WebGPU Original Soundtrack • 48kHz",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }

                // Bottom player controls
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Prev",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(config.primaryColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        PreviewScene.CYBER_HUD -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top telemetry
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "CORE TELEMETRY",
                            color = config.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "STABILITY: 99.48%",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "FREQ: ${(config.speed * 440f).toInt()} Hz",
                            color = config.secondaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "TURBULENCE: ${(config.turbulence * 100).toInt()}%",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Bottom telemetry
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SHADER: GLSL / WGSL CORE",
                        color = Color.LightGray.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "STATUS: ${config.aiState.name}",
                        color = config.primaryColor,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
