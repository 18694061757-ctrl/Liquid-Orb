package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrbConfig

@Composable
fun PresetSelectorBar(
    presets: List<OrbConfig>,
    selectedPresetId: String,
    isZh: Boolean,
    onSelectPreset: (OrbConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("preset_selector_row"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(presets, key = { it.id }) { preset ->
            val isSelected = preset.id == selectedPresetId

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) Color(0xFF222736) else Color(0xFF131722)
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) preset.primaryColor else Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectPreset(preset) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("preset_item_${preset.id}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mini Orb preview sphere
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    ) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        preset.accentColor,
                                        preset.primaryColor,
                                        preset.secondaryColor
                                    ),
                                    center = center,
                                    radius = size.width / 2f
                                ),
                                center = center,
                                radius = size.width / 2f
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isZh) preset.nameZh else preset.nameEn,
                                color = if (isSelected) Color.White else Color(0xFFCFD5E2),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (preset.isAudioResponsive) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Audio Responsive",
                                    tint = preset.primaryColor,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
