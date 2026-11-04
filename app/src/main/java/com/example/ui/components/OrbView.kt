package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphics.OrbRenderer
import com.example.model.AIState
import com.example.model.OrbConfig
import com.example.model.PreviewScene
import kotlinx.coroutines.launch

@Composable
fun OrbView(
    config: OrbConfig,
    audioLevel: Float,
    previewScene: PreviewScene,
    isZh: Boolean,
    modifier: Modifier = Modifier,
    onDoubleTap: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val touchOffsetX = remember { Animatable(0f) }
    val touchOffsetY = remember { Animatable(0f) }
    var tapPulse by remember { mutableStateOf(0f) }

    // Smooth infinite time loop for 60fps simulation
    val infiniteTransition = rememberInfiniteTransition(label = "orb_time")
    val timeSeconds by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 62.8318f, // 10 * 2*pi
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time_seconds"
    )

    Box(
        modifier = modifier
            .testTag("orb_view_container")
            .background(Color(0xFF090B10))
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() },
                    onTap = {
                        coroutineScope.launch {
                            tapPulse = 1f
                            touchOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            touchOffsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            touchOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                            touchOffsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            touchOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            touchOffsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            touchOffsetX.snapTo((touchOffsetX.value + dragAmount.x).coerceIn(-120f, 120f))
                            touchOffsetY.snapTo((touchOffsetY.value + dragAmount.y).coerceIn(-120f, 120f))
                        }
                    }
                )
            }
    ) {
        // Scene background effects
        SceneBackdrop(scene = previewScene, isZh = isZh, config = config, audioLevel = audioLevel)

        // Orb Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("orb_canvas")
        ) {
            OrbRenderer.drawOrb(
                drawScope = this,
                config = config,
                timeSeconds = timeSeconds,
                audioLevel = audioLevel,
                touchOffset = Offset(touchOffsetX.value, touchOffsetY.value)
            )
        }

        // Scene overlay foreground details (like voice speech transcript or HUD text)
        SceneOverlayForeground(scene = previewScene, isZh = isZh, config = config, audioLevel = audioLevel)

        // Subtle interactive hint at top
        Text(
            text = if (isZh) "拖动球体交互 • 双击切换AI状态" else "Drag to tilt • Double tap to toggle AI state",
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )
    }
}
