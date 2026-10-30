package com.ecrisar.flow.edge.ui.panels

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecrisar.flow.edge.ui.theme.OneUIBlue

/**
 * Quick Tools Edge Panel:
 * Flashlight toggle, digital ruler, volume controls.
 */
@Composable
fun ToolsPanel(
    modifier: Modifier = Modifier
) {
    var isFlashlightOn by remember { mutableStateOf(false) }
    var volumeLevel by remember { mutableFloatStateOf(0.7f) }
    var rulerUnitCm by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Quick Tools",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        // Flashlight Widget
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isFlashlightOn) Color(0xFF2C3545) else Color(0xFF1E2026),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isFlashlightOn) OneUIBlue else Color(0x22FFFFFF)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isFlashlightOn = !isFlashlightOn }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isFlashlightOn) OneUIBlue else Color(0xFF2A2D35)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = null,
                            tint = if (isFlashlightOn) Color.White else Color(0xFF888B94),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Flashlight",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isFlashlightOn) "Active" else "Off",
                            color = if (isFlashlightOn) OneUIBlue else Color(0xFF888B94),
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isFlashlightOn) OneUIBlue else Color(0xFF444444))
                )
            }
        }

        // Volume Control Widget
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E2026),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Volume",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(volumeLevel * 100).toInt()}%",
                        color = OneUIBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeDown,
                        contentDescription = null,
                        tint = Color(0xFF888B94),
                        modifier = Modifier.size(18.dp)
                    )

                    Slider(
                        value = volumeLevel,
                        onValueChange = { volumeLevel = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = OneUIBlue,
                            activeTrackColor = OneUIBlue,
                            inactiveTrackColor = Color(0xFF333742)
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color(0xFF888B94),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Digital Ruler Widget
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E2026),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = null,
                            tint = OneUIBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Digital Ruler",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = if (rulerUnitCm) "CM" else "INCH",
                        color = OneUIBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x330381FE))
                            .clickable { rulerUnitCm = !rulerUnitCm }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ruler Ticks Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF131418))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val tickSpacingPx = if (rulerUnitCm) 18f else 28f
                        var y = 10f
                        var tickCount = 0

                        while (y < size.height) {
                            val isMajor = tickCount % 5 == 0
                            val tickWidth = if (isMajor) size.width * 0.45f else size.width * 0.2f
                            val stroke = if (isMajor) 2f else 1f
                            val color = if (isMajor) Color.White else Color(0xFF6B7280)

                            drawLine(
                                color = color,
                                start = Offset(0f, y),
                                end = Offset(tickWidth, y),
                                strokeWidth = stroke
                            )

                            y += tickSpacingPx
                            tickCount++
                        }
                    }
                }
            }
        }
    }
}
