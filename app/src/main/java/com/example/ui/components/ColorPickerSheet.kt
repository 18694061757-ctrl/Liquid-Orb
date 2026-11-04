package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val PALETTE_PRESETS = listOf(
    Color(0xFF8B5CF6), // Purple
    Color(0xFF06B6D4), // Cyan
    Color(0xFFEC4899), // Pink
    Color(0xFF3B82F6), // Blue
    Color(0xFF10B981), // Emerald
    Color(0xFFF59E0B), // Amber
    Color(0xFFEF4444), // Crimson
    Color(0xFF6366F1), // Indigo
    Color(0xFF14B8A6), // Teal
    Color(0xFFD946EF), // Magenta
    Color(0xFF84CC16), // Lime
    Color(0xFFCBD5E1), // Silver / Chrome
    Color(0xFFFFFFFF), // White
    Color(0xFF1E293B)  // Dark Slate
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerSheet(
    title: String,
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var r by remember { mutableFloatStateOf(initialColor.red) }
    var g by remember { mutableFloatStateOf(initialColor.green) }
    var b by remember { mutableFloatStateOf(initialColor.blue) }

    val currentColor = Color(red = r, green = g, blue = b)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF12151E),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Color Preview Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1C212E))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(currentColor)
                        .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    val argb = currentColor.toArgb()
                    val hex = String.format("#%06X", 0xFFFFFF and argb)
                    Text(
                        text = hex,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RGB(${(r * 255).toInt()}, ${(g * 255).toInt()}, ${(b * 255).toInt()})",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Color Palette
            Text(
                text = "Preset Swatches",
                color = Color.LightGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(PALETTE_PRESETS) { color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (color == currentColor) 2.5.dp else 1.dp,
                                color = if (color == currentColor) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                r = color.red
                                g = color.green
                                b = color.blue
                                onColorSelected(color)
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // R, G, B Sliders
            LabeledSlider(
                label = "Red (R)",
                value = r,
                range = 0f..1f,
                onValueChange = {
                    r = it
                    onColorSelected(Color(r, g, b))
                },
                valueFormatter = { "${(it * 255).toInt()}" }
            )

            LabeledSlider(
                label = "Green (G)",
                value = g,
                range = 0f..1f,
                onValueChange = {
                    g = it
                    onColorSelected(Color(r, g, b))
                },
                valueFormatter = { "${(it * 255).toInt()}" }
            )

            LabeledSlider(
                label = "Blue (B)",
                value = b,
                range = 0f..1f,
                onValueChange = {
                    b = it
                    onColorSelected(Color(r, g, b))
                },
                valueFormatter = { "${(it * 255).toInt()}" }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
