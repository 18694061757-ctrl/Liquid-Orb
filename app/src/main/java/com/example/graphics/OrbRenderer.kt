package com.example.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.model.AIState
import com.example.model.DispersionType
import com.example.model.OrbConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object OrbRenderer {

    fun drawOrb(
        drawScope: DrawScope,
        config: OrbConfig,
        timeSeconds: Float,
        audioLevel: Float,
        touchOffset: Offset = Offset.Zero
    ) {
        val size = drawScope.size
        val center = Offset(size.width / 2f, size.height / 2f)
        val minDim = minOf(size.width, size.height)
        val baseRadius = minDim * 0.38f

        // Audio reactivity scaling
        val effectiveAudio = if (config.isAudioResponsive) audioLevel * config.audioResponseScale else 0f
        val audioPulse = 1f + effectiveAudio * 0.16f
        val currentRadius = baseRadius * audioPulse

        // AI State animation multipliers
        val stateSpeedMult = if (config.aiState == AIState.THINKING) 1.6f else 0.85f
        val stateTurbulenceMult = if (config.aiState == AIState.THINKING) 1.35f else 0.8f
        val animTime = timeSeconds * config.speed * stateSpeedMult

        // Dynamic center offset with touch spring tilt
        val fluidCenter = center + touchOffset * 0.35f

        // 0. Draw Background Bokeh (深海虚化景深光斑)
        drawBackgroundBokeh(
            drawScope = drawScope,
            center = fluidCenter,
            radius = currentRadius,
            config = config,
            animTime = animTime
        )

        // 1. Draw Outer Glow (high-contrast ambient halo, non-washing)
        drawOuterGlow(
            drawScope = drawScope,
            center = fluidCenter,
            radius = currentRadius,
            config = config,
            effectiveAudio = effectiveAudio,
            timeSeconds = animTime
        )

        // 2. Draw Audio Ripple Shockwaves (if sound is active)
        if (effectiveAudio > 0.15f) {
            drawAudioRipples(
                drawScope = drawScope,
                center = fluidCenter,
                radius = currentRadius,
                config = config,
                audioLevel = effectiveAudio,
                timeSeconds = animTime
            )
        }

        // 3. Draw External Filaments (Back Pass - occluded by core)
        if (config.hasFilaments) {
            drawExternalFilaments(
                drawScope = drawScope,
                center = fluidCenter,
                radius = currentRadius,
                config = config,
                animTime = animTime,
                effectiveAudio = effectiveAudio,
                touchOffset = touchOffset,
                isBackPass = true
            )
        }

        // 4. Draw Inner Fluid / Plasma Layers (with Water Meniscus & Caustics)
        drawFluidCore(
            drawScope = drawScope,
            center = fluidCenter,
            radius = currentRadius,
            config = config,
            animTime = animTime,
            effectiveAudio = effectiveAudio,
            stateTurbulence = stateTurbulenceMult
        )

        // 5. Draw Glass Shell, Refraction and Rim Lighting
        if (config.hasGlassShell) {
            drawGlassShell(
                drawScope = drawScope,
                center = fluidCenter,
                radius = currentRadius,
                config = config,
                animTime = animTime,
                touchOffset = touchOffset
            )
        }

        // 6. Draw External Filaments (Front Pass - in front of glass)
        if (config.hasFilaments) {
            drawExternalFilaments(
                drawScope = drawScope,
                center = fluidCenter,
                radius = currentRadius,
                config = config,
                animTime = animTime,
                effectiveAudio = effectiveAudio,
                touchOffset = touchOffset,
                isBackPass = false
            )
        }

        // 7. Draw Specular Highlights (Key, Fill, Rim)
        drawSpecularHighlights(
            drawScope = drawScope,
            center = fluidCenter,
            radius = currentRadius,
            config = config,
            touchOffset = touchOffset
        )

        // 8. Draw Sunlight Flare & Anamorphic Starburst (11点钟方向烈阳耀斑与星芒)
        if (config.sunlightIntensity > 0.05f) {
            drawSunlightAndStarburst(
                drawScope = drawScope,
                center = fluidCenter,
                radius = currentRadius,
                config = config,
                animTime = animTime
            )
        }

        // 9. Draw Micro Water Droplets (悬浮晶莹微水滴)
        if (config.waterDropletCount > 0) {
            drawMicroWaterDroplets(
                drawScope = drawScope,
                center = fluidCenter,
                radius = currentRadius,
                config = config,
                animTime = animTime,
                effectiveAudio = effectiveAudio
            )
        }
    }

    /**
     * 景深背景虚化光斑 (Aquatic Bokeh Orbs in Dark Water)
     */
    private fun drawBackgroundBokeh(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        animTime: Float
    ) {
        val bokehCount = 10
        val bokehColors = listOf(
            Color(0xFF042F2E).copy(alpha = 0.12f),
            Color(0xFF0F766E).copy(alpha = 0.08f),
            Color(0xFF78350F).copy(alpha = 0.07f),
            Color(0xFF064E3B).copy(alpha = 0.10f),
            Color(0xFFD97706).copy(alpha = 0.06f)
        )
        for (i in 0 until bokehCount) {
            val angle = (i * 2.0 * PI / bokehCount) + animTime * 0.12f * (if (i % 2 == 0) 1f else -1f)
            val dist = radius * (1.35f + (i % 4) * 0.22f + sin(animTime * 0.8f + i) * 0.08f)
            val bx = center.x + (dist * cos(angle)).toFloat()
            val by = center.y + (dist * sin(angle) * 0.85f).toFloat()
            val bRadius = radius * (0.08f + (i % 3) * 0.05f)

            drawScope.drawCircle(
                color = bokehColors[i % bokehColors.size],
                radius = bRadius,
                center = Offset(bx, by),
                blendMode = BlendMode.Screen
            )
        }
    }

    /**
     * 外层水光光晕 (高对比度暗调光晕，杜绝泛白过曝)
     */
    private fun drawOuterGlow(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        effectiveAudio: Float,
        timeSeconds: Float
    ) {
        val glowRadius = radius * (1.05f + config.outerGlowRadius * 0.65f + effectiveAudio * 0.2f)
        val pulse = 1f + sin(timeSeconds * 1.8f) * 0.05f
        val alpha = (config.outerGlowIntensity * pulse).coerceIn(0f, 1f)
        if (alpha <= 0.01f) return

        // 仅在球体外部边缘绽放环状光晕，中心保持通透深邃
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to Color.Transparent,
                    0.72f to Color.Transparent,
                    0.92f to config.glowColor.copy(alpha = alpha * 0.45f),
                    1.05f to config.secondaryColor.copy(alpha = alpha * 0.20f),
                    1.35f to Color.Transparent
                ),
                center = center,
                radius = glowRadius
            ),
            radius = glowRadius,
            center = center,
            blendMode = BlendMode.Screen
        )
    }

    private fun drawAudioRipples(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        audioLevel: Float,
        timeSeconds: Float
    ) {
        val rippleCount = 2
        for (i in 0 until rippleCount) {
            val phase = (timeSeconds * 1.5f + i * 0.5f) % 1.0f
            val rippleRadius = radius + phase * radius * 0.55f * (audioLevel + 0.5f)
            val rippleAlpha = (1f - phase) * audioLevel * 0.35f
            drawScope.drawCircle(
                color = config.accentColor.copy(alpha = rippleAlpha),
                radius = rippleRadius,
                center = center,
                style = Stroke(width = (2.2f * (1f - phase)).coerceAtLeast(1f)),
                blendMode = BlendMode.Screen
            )
        }
    }

    /**
     * 水晶球体内流水与焦散核心 (Fluid Core with Internal Meniscus & Caustics)
     */
    private fun drawFluidCore(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        animTime: Float,
        effectiveAudio: Float,
        stateTurbulence: Float
    ) {
        val turbulence = config.turbulence * stateTurbulence
        val waveCount = config.waveCount

        // 1. 深海明暗反差球体基底 (Abyssal Navy Base with Cyan Horizon)
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to config.coreColor,                            // 深邃深蓝黑 (#01121A)
                    0.45f to Color(0xFF032230),                          // 暗部海洋蓝
                    0.75f to config.secondaryColor.copy(alpha = 0.95f),   // 湖深青 (#0D9488)
                    1.0f to config.primaryColor                          // 晶透水蓝边缘 (#0284C7)
                ),
                center = center + Offset(-radius * 0.15f, -radius * 0.22f),
                radius = radius * 1.15f
            ),
            radius = radius,
            center = center
        )

        // 2. 球内透光水体界面 (Undulating Water Surface Meniscus)
        drawInternalWaterBody(
            drawScope = drawScope,
            center = center,
            radius = radius * 0.95f,
            config = config,
            animTime = animTime,
            waveCount = waveCount,
            turbulence = turbulence
        )

        // 3. 水下真实日光焦散纹理网络 (Sunlight Water Caustics Lattice)
        drawWaterCaustics(
            drawScope = drawScope,
            center = center,
            radius = radius * 0.88f,
            animTime = animTime,
            turbulence = turbulence
        )

        // 4. 球芯深海灵动微光核 (Cyan aquatic nucleus, not washing out)
        val coreRadius = radius * (0.28f + effectiveAudio * 0.12f)
        val corePulsar = 1f + sin(animTime * 3.0f) * 0.10f
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to Color.White.copy(alpha = 0.55f),
                    0.35f to Color(0xFF38BDF8).copy(alpha = 0.45f),
                    0.75f to config.primaryColor.copy(alpha = 0.18f),
                    1.0f to Color.Transparent
                ),
                center = center + Offset(0f, radius * 0.10f),
                radius = coreRadius * corePulsar
            ),
            radius = coreRadius * corePulsar,
            center = center + Offset(0f, radius * 0.10f),
            blendMode = BlendMode.Plus
        )
    }

    /**
     * 内部水体液面起伏 (Water volume inside sphere)
     */
    private fun drawInternalWaterBody(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        animTime: Float,
        waveCount: Int,
        turbulence: Float
    ) {
        val path = Path()
        val numPoints = 48
        val leftX = center.x - radius * 0.92f
        val rightX = center.x + radius * 0.92f
        val waterLevelY = center.y - radius * 0.05f

        path.moveTo(leftX, waterLevelY)

        // 水面起伏波动
        for (i in 0..numPoints) {
            val u = i.toFloat() / numPoints
            val x = leftX + u * (rightX - leftX)
            val w = sin(u * PI.toFloat() * waveCount + animTime * 1.5f) * (radius * 0.08f * turbulence)
            val y = waterLevelY + w
            path.lineTo(x, y)
        }

        // 下半球边缘闭合
        for (i in 0..numPoints) {
            val theta = (i.toFloat() / numPoints) * PI.toFloat()
            val x = center.x + radius * 0.92f * cos(theta)
            val y = center.y + radius * 0.92f * sin(theta)
            path.lineTo(x, y)
        }
        path.close()

        drawScope.drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0284C7).copy(alpha = 0.50f),
                    Color(0xFF0D9488).copy(alpha = 0.70f),
                    Color(0xFF042F2E).copy(alpha = 0.85f)
                ),
                startY = waterLevelY - radius * 0.1f,
                endY = center.y + radius
            ),
            blendMode = BlendMode.SrcOver
        )
    }

    /**
     * 水光日光焦散网格 (Interfering Light Caustics Web)
     */
    private fun drawWaterCaustics(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        animTime: Float,
        turbulence: Float
    ) {
        val causticCount = 7
        for (k in 0 until causticCount) {
            val path = Path()
            val points = 32
            val yBase = center.y + (k - causticCount / 2f) * (radius * 0.22f) + radius * 0.15f
            val phase = animTime * 1.1f + k * 1.4f

            var started = false
            for (p in 0..points) {
                val u = p.toFloat() / points
                val x = center.x - radius * 0.75f + u * radius * 1.5f
                val dx = x - center.x
                val distSq = dx * dx
                val maxDy = radius * radius * 0.85f - distSq
                if (maxDy > 0) {
                    val dyLimit = sqrt(maxDy)
                    val wave1 = sin(u * 8f + phase) * (radius * 0.07f * (0.6f + turbulence * 0.4f))
                    val wave2 = cos(u * 12f - phase * 0.8f + k) * (radius * 0.04f)
                    val y = (yBase + wave1 + wave2).coerceIn(center.y - dyLimit * 0.9f, center.y + dyLimit * 0.9f)

                    if (!started) {
                        path.moveTo(x, y)
                        started = true
                    } else {
                        path.lineTo(x, y)
                    }
                }
            }
            if (started) {
                val alpha = (0.28f + sin(phase) * 0.12f).coerceIn(0.1f, 0.42f)
                drawScope.drawPath(
                    path = path,
                    color = if (k % 2 == 0) Color(0xFF38BDF8).copy(alpha = alpha) else Color(0xFF67E8F9).copy(alpha = alpha * 0.75f),
                    style = Stroke(width = if (k % 3 == 0) 2.0f else 1.3f, cap = StrokeCap.Round),
                    blendMode = BlendMode.Plus
                )
            }
        }
    }

    /**
     * 水晶球壳与折射边缘 (Glass Shell & Crystal Containment Wall)
     */
    private fun drawGlassShell(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        animTime: Float,
        touchOffset: Offset
    ) {
        val edgeAlpha = config.edgeIntensity.coerceIn(0f, 1f)

        // 1. 水晶外壳环状菲涅尔边缘光 (Crisp Crystal Boundary Ring)
        drawScope.drawCircle(
            brush = Brush.sweepGradient(
                listOf(
                    Color.White.copy(alpha = edgeAlpha * 0.85f),
                    config.primaryColor.copy(alpha = edgeAlpha * 0.4f),
                    Color.White.copy(alpha = edgeAlpha * 0.92f),
                    config.secondaryColor.copy(alpha = edgeAlpha * 0.35f),
                    Color.White.copy(alpha = edgeAlpha * 0.85f)
                ),
                center = center
            ),
            radius = radius - 1.5f,
            center = center,
            style = Stroke(width = 2.8f),
            blendMode = BlendMode.Screen
        )

        // 2. 玻璃厚度折射带 (Simulating 3D Glass Wall Thickness)
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to Color.Transparent,
                    0.84f to Color.Transparent,
                    0.94f to Color.White.copy(alpha = config.refractionStrength * 0.25f),
                    1.0f to Color.White.copy(alpha = config.refractionStrength * 0.65f)
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center,
            blendMode = BlendMode.Screen
        )
    }

    /**
     * 高保真高光反射系统 (Key, Fill, Rim Specular Highlights)
     */
    private fun drawSpecularHighlights(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        touchOffset: Offset
    ) {
        // 1. Key Highlight (穹顶主光源月牙高光)
        if (config.keyHighlight > 0.05f) {
            val keyOffset = Offset(
                center.x - radius * 0.40f + touchOffset.x * 0.1f,
                center.y - radius * 0.44f + touchOffset.y * 0.1f
            )
            val keySize = Size(radius * 0.72f, radius * 0.32f)

            drawScope.rotate(degrees = -36f, pivot = keyOffset) {
                drawOval(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to Color.White.copy(alpha = config.keyHighlight * 0.92f),
                            0.45f to Color.White.copy(alpha = config.keyHighlight * 0.45f),
                            0.8f to Color.White.copy(alpha = config.keyHighlight * 0.12f),
                            1.0f to Color.Transparent
                        ),
                        center = keyOffset,
                        radius = keySize.width * 0.55f
                    ),
                    topLeft = Offset(keyOffset.x - keySize.width / 2f, keyOffset.y - keySize.height / 2f),
                    size = keySize,
                    blendMode = BlendMode.Screen
                )
            }
        }

        // 2. Fill Highlight (水下环境水青色反弹光)
        if (config.fillHighlight > 0.05f) {
            val fillOffset = Offset(
                center.x + radius * 0.45f + touchOffset.x * 0.05f,
                center.y + radius * 0.45f + touchOffset.y * 0.05f
            )
            val fillSize = Size(radius * 0.52f, radius * 0.25f)

            drawScope.rotate(degrees = -45f, pivot = fillOffset) {
                drawOval(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to config.primaryColor.copy(alpha = config.fillHighlight * 0.60f),
                            0.5f to config.secondaryColor.copy(alpha = config.fillHighlight * 0.22f),
                            1.0f to Color.Transparent
                        ),
                        center = fillOffset,
                        radius = fillSize.width * 0.55f
                    ),
                    topLeft = Offset(fillOffset.x - fillSize.width / 2f, fillOffset.y - fillSize.height / 2f),
                    size = fillSize,
                    blendMode = BlendMode.Screen
                )
            }
        }

        // 3. Rim Highlight (右侧晶亮轮廓反光)
        if (config.rimHighlight > 0.05f) {
            val rimAlpha = (config.rimHighlight * 0.85f).coerceIn(0f, 1f)
            drawScope.drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = rimAlpha),
                        Color.White.copy(alpha = rimAlpha * 0.35f),
                        Color.Transparent
                    ),
                    center = center
                ),
                startAngle = 175f,
                sweepAngle = 115f,
                useCenter = false,
                topLeft = Offset(center.x - radius + 2f, center.y - radius + 2f),
                size = Size((radius - 2f) * 2f, (radius - 2f) * 2f),
                style = Stroke(width = 3.0f, cap = StrokeCap.Round),
                blendMode = BlendMode.Screen
            )
        }
    }

    /**
     * R06: 五条外部水草与金光能量带 (3D Ribbon Mesh Strips with Dual-Rim Speculars)
     * 水草实体叶片、透光水体流线、暖金光环与边缘纯白反光线
     */
    private fun drawExternalFilaments(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        animTime: Float,
        effectiveAudio: Float,
        touchOffset: Offset,
        isBackPass: Boolean
    ) {
        if (!config.hasFilaments || config.filamentVisibility <= 0.01f) return

        val filamentCount = config.filamentCount.coerceIn(1, 5)

        // 真实水草与光芒多光谱带配置:
        // 0: 暖金耀光能量带 (Sunlight Gold Orbit)
        // 1: 真实翠绿水草阔叶片 (Organic Emerald Seagrass Leaf)
        // 2: 晶莹清澈透流水体环 (Liquid Glass Stream Jet)
        // 3: 柔美回旋嫩绿水草带 (Spring Kelp Twisting Ribbon)
        // 4: 太阳耀斑金光等离子带 (Solar Plasma Arc)
        val filamentConfigs = listOf(
            FilamentStyle(Color(0xFFFBBF24), Color(0xFFFEF08A), 1.0f, isSunRibbon = true),
            FilamentStyle(Color(0xFF15803D), Color(0xFFD9F99D), 1.35f, isSunRibbon = false),
            FilamentStyle(Color(0xFF38BDF8), Color.White, 0.95f, isSunRibbon = false),
            FilamentStyle(Color(0xFF84CC16), Color(0xFFBEF264), 1.15f, isSunRibbon = false),
            FilamentStyle(Color(0xFFF59E0B), Color.White, 0.85f, isSunRibbon = true)
        )

        // View Rotation from touch drag
        val yaw = (touchOffset.x * 0.005f).toDouble()
        val pitch = (touchOffset.y * 0.005f).toDouble()
        val cosYaw = cos(yaw)
        val sinYaw = sin(yaw)
        val cosPitch = cos(pitch)
        val sinPitch = sin(pitch)

        // 1. Visibility Logic
        val stateVisibility = if (config.aiState == AIState.THINKING) 1.0f else (0.75f + 0.20f * sin(animTime * 1.4f))
        val audioBoost = 1.0f + effectiveAudio * 0.45f
        val baseVisibility = (config.filamentVisibility * stateVisibility * audioBoost).coerceIn(0f, 1f)

        // Orbit speed & angle
        val orbitSpeed = config.filamentOrbitSpeed * (if (config.aiState == AIState.THINKING) 1.5f else 0.85f)
        val orbitAngle = animTime * orbitSpeed * 0.8f

        val steps = 38

        for (k in 0 until filamentCount) {
            val basePhase = (k * 2.0 * PI / filamentCount).toFloat()
            val tiltBase = sin(k * 1.25) * 0.65
            val style = filamentConfigs[k % filamentConfigs.size]

            var prevPoint: Offset? = null
            var prevNormal: Offset? = null

            for (step in 0..steps) {
                val s = step.toFloat() / steps

                // Azimuth angle with aquatic current twist
                val azimuth = (basePhase + s * (PI.toFloat() * config.filamentTwist) + orbitAngle).toDouble()

                // Latitude / Elevation wave (水流起伏)
                val latitude = tiltBase + sin(2.0 * PI * s + animTime * 1.2 + k) * (0.35 * config.seagrassWaveAmount)

                // Radial distance from orb center
                val waveR = sin(3.0 * PI * s + animTime * 2.0 + k) * 0.07
                val rExt = (radius * (config.filamentRadiusRatio + waveR + effectiveAudio * config.filamentAudioExpand)).toDouble()

                // 3D Cartesian coordinates
                val x3D = rExt * cos(latitude) * cos(azimuth)
                val y3D = rExt * sin(latitude)
                val z3D = rExt * cos(latitude) * sin(azimuth)

                // View transformation
                val xRot = x3D * cosYaw + z3D * sinYaw
                val zRot1 = -x3D * sinYaw + z3D * cosYaw
                val yRot = y3D * cosPitch - zRot1 * sinPitch
                val zRot2 = y3D * sinPitch + zRot1 * cosPitch

                val currentPoint = Offset(
                    (center.x + xRot).toFloat(),
                    (center.y + yRot).toFloat()
                )

                val segmentIsBack = zRot2 < 0

                if (prevPoint != null) {
                    val dx = currentPoint.x - prevPoint.x
                    val dy = currentPoint.y - prevPoint.y
                    val segLen = hypot(dx, dy)

                    if (segLen > 0.05f) {
                        val nx = -dy / segLen
                        val ny = dx / segLen
                        val currNormal = Offset(nx, ny)
                        val pNorm = prevNormal ?: currNormal

                        if (segmentIsBack == isBackPass) {
                            val taper = (sin(PI * s).pow(1.15)).toFloat()
                            val distFromCenter = hypot(xRot, yRot)
                            val occlusion = if (isBackPass && distFromCenter < radius) 0.35f else 1.0f
                            val segmentAlpha = (baseVisibility * taper * occlusion).coerceIn(0f, 1f)

                            if (segmentAlpha > 0.02f) {
                                // 1. 3D Ribbon Mesh Strip (实体水草阔叶/水体能量带)
                                val halfWidth = (config.seagrassRibbonWidth * style.widthMultiplier * taper * 0.5f * (if (isBackPass) 0.8f else 1.15f)).coerceAtLeast(1.4f)

                                val p1Left = prevPoint + pNorm * halfWidth
                                val p1Right = prevPoint - pNorm * halfWidth
                                val p2Left = currentPoint + currNormal * halfWidth
                                val p2Right = currentPoint - currNormal * halfWidth

                                val ribbonPath = Path().apply {
                                    moveTo(p1Left.x, p1Left.y)
                                    lineTo(p2Left.x, p2Left.y)
                                    lineTo(p2Right.x, p2Right.y)
                                    lineTo(p1Right.x, p1Right.y)
                                    close()
                                }

                                // Ribbon body fill
                                drawScope.drawPath(
                                    path = ribbonPath,
                                    color = style.bodyColor.copy(alpha = segmentAlpha * 0.65f),
                                    style = Fill,
                                    blendMode = if (style.isSunRibbon) BlendMode.Screen else BlendMode.SrcOver
                                )

                                // 2. Dual-Rim Specular Edge Lines (双侧水光高反光纯白边缘线)
                                // 赋予水草与水体带真正的玻璃/水光物理厚度与折射质感
                                val edgeAlpha = (segmentAlpha * 0.85f).coerceIn(0f, 1f)
                                drawScope.drawLine(
                                    color = style.edgeHighlight.copy(alpha = edgeAlpha),
                                    start = p1Left,
                                    end = p2Left,
                                    strokeWidth = 1.3f,
                                    cap = StrokeCap.Round,
                                    blendMode = BlendMode.Plus
                                )
                                drawScope.drawLine(
                                    color = style.edgeHighlight.copy(alpha = edgeAlpha),
                                    start = p1Right,
                                    end = p2Right,
                                    strokeWidth = 1.3f,
                                    cap = StrokeCap.Round,
                                    blendMode = BlendMode.Plus
                                )

                                // 3. Core Thread (中央高光细核)
                                drawScope.drawLine(
                                    color = Color.White.copy(alpha = segmentAlpha * 0.80f),
                                    start = prevPoint,
                                    end = currentPoint,
                                    strokeWidth = (config.filamentThickness * taper * 0.35f).coerceAtLeast(0.9f),
                                    cap = StrokeCap.Round,
                                    blendMode = BlendMode.Plus
                                )

                                // 4. Light Knot Starburst (金光带在受光顶点的星芒节点)
                                if (!isBackPass && style.isSunRibbon && s in 0.42f..0.48f) {
                                    drawLightKnot(
                                        drawScope = drawScope,
                                        center = currentPoint,
                                        size = radius * 0.24f * config.sunlightIntensity,
                                        color = style.bodyColor
                                    )
                                }
                            }
                        }
                        prevNormal = currNormal
                    }
                }

                prevPoint = currentPoint
            }
        }
    }

    private data class FilamentStyle(
        val bodyColor: Color,
        val edgeHighlight: Color,
        val widthMultiplier: Float,
        val isSunRibbon: Boolean
    )

    private fun drawLightKnot(
        drawScope: DrawScope,
        center: Offset,
        size: Float,
        color: Color
    ) {
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, color.copy(alpha = 0.85f), Color.Transparent),
                center = center,
                radius = size * 0.55f
            ),
            radius = size * 0.55f,
            center = center,
            blendMode = BlendMode.Plus
        )

        val rayLen = size * 0.85f
        drawScope.drawLine(
            color = Color.White.copy(alpha = 0.95f),
            start = Offset(center.x - rayLen, center.y),
            end = Offset(center.x + rayLen, center.y),
            strokeWidth = 1.8f,
            blendMode = BlendMode.Plus
        )
        drawScope.drawLine(
            color = Color.White.copy(alpha = 0.95f),
            start = Offset(center.x, center.y - rayLen),
            end = Offset(center.x, center.y + rayLen),
            strokeWidth = 1.8f,
            blendMode = BlendMode.Plus
        )
    }

    /**
     * 11点钟方向烈阳耀斑与星芒耀斑 (Sunlight Flare & Anamorphic Starburst)
     * 紧密附着在球体左上缘，白炽高光核 + 金光光晕 + 四角尖刺星芒 + 边缘反射弧
     */
    private fun drawSunlightAndStarburst(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        animTime: Float
    ) {
        val sunOffset = Offset(
            center.x - radius * 0.46f,
            center.y - radius * 0.52f
        )
        val sunHotspot = radius * 0.22f * config.sunlightIntensity

        // 1. 白炽核心与金色耀斑光晕 (Incandescent Core & Golden Halo)
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to Color.White,
                    0.35f to Color(0xFFFEF08A).copy(alpha = 0.95f),
                    0.70f to Color(0xFFF59E0B).copy(alpha = 0.45f),
                    1.0f to Color.Transparent
                ),
                center = sunOffset,
                radius = sunHotspot
            ),
            radius = sunHotspot,
            center = sunOffset,
            blendMode = BlendMode.Plus
        )

        // 2. 变形宽银幕切向眩光 (Anamorphic Lens Flare Horizontal Streak)
        val streakWidth = radius * 1.35f * config.sunlightIntensity
        val streakHeight = radius * 0.08f
        drawScope.rotate(degrees = -25f, pivot = sunOffset) {
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.92f),
                        Color(0xFFFEF08A).copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    center = sunOffset,
                    radius = streakWidth / 2f
                ),
                topLeft = Offset(sunOffset.x - streakWidth / 2f, sunOffset.y - streakHeight / 2f),
                size = Size(streakWidth, streakHeight),
                blendMode = BlendMode.Plus
            )
        }

        // 3. 四角星芒钻石刺芒 (4-point Diamond Starburst Diffraction Rays)
        val rayLength = radius * 0.72f * config.sunlightIntensity
        val rayWidth = 2.2f
        val pulse = 1f + sin(animTime * 2.5f) * 0.08f

        drawScope.rotate(degrees = -15f, pivot = sunOffset) {
            // Main Spikes
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = Offset(sunOffset.x - rayLength * pulse, sunOffset.y),
                end = Offset(sunOffset.x + rayLength * pulse, sunOffset.y),
                strokeWidth = rayWidth,
                blendMode = BlendMode.Plus
            )
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = Offset(sunOffset.x, sunOffset.y - rayLength * pulse),
                end = Offset(sunOffset.x, sunOffset.y + rayLength * pulse),
                strokeWidth = rayWidth,
                blendMode = BlendMode.Plus
            )
            // Diagonal Shorter Spikes
            val diagLen = rayLength * 0.48f * pulse
            drawLine(
                color = Color(0xFFFEF08A).copy(alpha = 0.75f),
                start = Offset(sunOffset.x - diagLen, sunOffset.y - diagLen),
                end = Offset(sunOffset.x + diagLen, sunOffset.y + diagLen),
                strokeWidth = 1.3f,
                blendMode = BlendMode.Plus
            )
            drawLine(
                color = Color(0xFFFEF08A).copy(alpha = 0.75f),
                start = Offset(sunOffset.x - diagLen, sunOffset.y + diagLen),
                end = Offset(sunOffset.x + diagLen, sunOffset.y - diagLen),
                strokeWidth = 1.3f,
                blendMode = BlendMode.Plus
            )
        }

        // 4. 受光面球壳切边金弧 (Sunlight Rim Crescent Sheen)
        drawScope.drawArc(
            brush = Brush.sweepGradient(
                listOf(
                    Color.Transparent,
                    Color(0xFFFEF08A).copy(alpha = 0.90f),
                    Color.White,
                    Color(0xFFF59E0B).copy(alpha = 0.60f),
                    Color.Transparent
                ),
                center = center
            ),
            startAngle = 205f,
            sweepAngle = 75f,
            useCenter = false,
            topLeft = Offset(center.x - radius + 1.5f, center.y - radius + 1.5f),
            size = Size((radius - 1.5f) * 2f, (radius - 1.5f) * 2f),
            style = Stroke(width = 3.2f, cap = StrokeCap.Round),
            blendMode = BlendMode.Plus
        )
    }

    /**
     * 悬浮微水滴系统 (Micro Water Droplets with Pinpoint Speculars)
     */
    private fun drawMicroWaterDroplets(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        config: OrbConfig,
        animTime: Float,
        effectiveAudio: Float
    ) {
        val count = config.waterDropletCount.coerceIn(1, 24)

        for (i in 0 until count) {
            val anglePhase = (i * 2.0 * PI / count) + animTime * (0.32f + (i % 3) * 0.08f)
            val baseDistRatio = 1.12f + (i % 4) * 0.08f
            val buoyancyY = sin(animTime * 1.8f + i) * (radius * 0.04f)

            val dist = radius * (baseDistRatio + sin(i * 1.5f + animTime * 0.8f) * 0.05f + effectiveAudio * 0.12f)
            val dropX = center.x + (dist * cos(anglePhase)).toFloat()
            val dropY = center.y + (dist * sin(anglePhase) * 0.75f).toFloat() + buoyancyY.toFloat()
            val dropPos = Offset(dropX, dropY)

            val dropRadius = (3.2f + (i % 5) * 1.1f).coerceAtLeast(2.0f)

            // 1. Droplet Body with glass refraction tint
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to Color.White.copy(alpha = 0.45f),
                        0.5f to Color(0xFF38BDF8).copy(alpha = 0.30f),
                        0.9f to config.primaryColor.copy(alpha = 0.15f),
                        1.0f to Color.Transparent
                    ),
                    center = dropPos,
                    radius = dropRadius * 1.3f
                ),
                radius = dropRadius,
                center = dropPos,
                blendMode = BlendMode.Screen
            )

            // 2. Droplet Glass Rim Edge
            drawScope.drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = dropRadius,
                center = dropPos,
                style = Stroke(width = 1.1f),
                blendMode = BlendMode.Screen
            )

            // 3. Droplet Specular Key Glint (top-left pinpoint glint at 11 o'clock)
            val glintPos = Offset(dropPos.x - dropRadius * 0.35f, dropPos.y - dropRadius * 0.35f)
            drawScope.drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = dropRadius * 0.32f,
                center = glintPos,
                blendMode = BlendMode.Plus
            )
        }
    }
}
