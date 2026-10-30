package com.ecrisar.flow.edge.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ecrisar.flow.edge.data.LauncherHelper
import com.ecrisar.flow.edge.model.PopupWindowState
import com.ecrisar.flow.edge.ui.theme.OneUIBlue
import kotlin.math.roundToInt

/**
 * Floating Pop-up View window.
 * Replicates Samsung One UI pop-up window:
 * - Draggable header bar to reposition.
 * - Corner drag to resize.
 * - Minimize to floating circle icon.
 * - Maximize and Close buttons.
 * - Action to launch full external app in native Android freeform mode.
 */
@Composable
fun FloatingPopupView(
    state: PopupWindowState,
    onUpdate: (PopupWindowState) -> Unit,
    onClose: () -> Unit,
    onBringToFront: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var currentX by remember(state.xOffsetDp) { mutableFloatStateOf(state.xOffsetDp.value) }
    var currentY by remember(state.yOffsetDp) { mutableFloatStateOf(state.yOffsetDp.value) }
    var currentWidth by remember(state.widthDp) { mutableFloatStateOf(state.widthDp.value) }
    var currentHeight by remember(state.heightDp) { mutableFloatStateOf(state.heightDp.value) }

    if (state.isMinimized) {
        // Floating circular badge when minimized
        Box(
            modifier = modifier
                .offset {
                    IntOffset(
                        with(density) { currentX.dp.toPx().roundToInt() },
                        with(density) { currentY.dp.toPx().roundToInt() }
                    )
                }
                .size(56.dp)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(state.app.accentColor)
                .border(2.dp, Color.White, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        currentX += with(density) { dragAmount.x.toDp().value }
                        currentY += with(density) { dragAmount.y.toDp().value }
                        onUpdate(state.copy(xOffsetDp = currentX.dp, yOffsetDp = currentY.dp))
                    }
                }
                .clickable {
                    onBringToFront()
                    onUpdate(state.copy(isMinimized = false))
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.app.label.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        return
    }

    val windowWidth = if (state.isMaximized) 360.dp else currentWidth.dp
    val windowHeight = if (state.isMaximized) 600.dp else currentHeight.dp

    Box(
        modifier = modifier
            .offset {
                if (state.isMaximized) {
                    IntOffset(0, 0)
                } else {
                    IntOffset(
                        with(density) { currentX.dp.toPx().roundToInt() },
                        with(density) { currentY.dp.toPx().roundToInt() }
                    )
                }
            }
            .width(windowWidth)
            .height(windowHeight)
            .zIndex(state.zIndex)
            .shadow(16.dp, RoundedCornerShape(18.dp), spotColor = Color.Black)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E1F24))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(18.dp))
            .clickable { onBringToFront() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Samsung One UI Pop-up Window Header Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .pointerInput(state.isMaximized) {
                        if (!state.isMaximized) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                currentX += with(density) { dragAmount.x.toDp().value }
                                currentY += with(density) { dragAmount.y.toDp().value }
                                onUpdate(state.copy(xOffsetDp = currentX.dp, yOffsetDp = currentY.dp))
                            }
                        }
                    },
                color = Color(0xFF282A30)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // App icon and title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(state.app.accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.app.label.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = state.app.label,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Window control buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Minimize button
                        IconButton(
                            onClick = {
                                onUpdate(state.copy(isMinimized = true))
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Minimize",
                                tint = Color(0xFFAAAAAA),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Maximize / Restore button
                        IconButton(
                            onClick = {
                                onUpdate(state.copy(isMaximized = !state.isMaximized))
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (state.isMaximized) Icons.Default.Fullscreen else Icons.Default.OpenInFull,
                                contentDescription = "Maximize",
                                tint = Color(0xFFAAAAAA),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Close button
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Window Content Body
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF131417))
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(state.app.accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.app.label.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = state.app.label,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = state.app.packageName,
                            color = Color(0xFF888B94),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x3322242A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
                        ) {
                            Text(
                                text = "Running in One UI Pop-up Mode",
                                color = OneUIBlue,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Launch in Android OS Freeform button
                    Button(
                        onClick = {
                            LauncherHelper.launchAppInPopupView(context, state.app)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OneUIBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Launch,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Open System App",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Bottom-right corner resize handle indicator
            if (!state.isMaximized) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(24.dp)
                        .offset(x = (-2).dp, y = (-2).dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newW = (currentWidth + with(density) { dragAmount.x.toDp().value })
                                    .coerceIn(200f, 600f)
                                val newH = (currentHeight + with(density) { dragAmount.y.toDp().value })
                                    .coerceIn(240f, 900f)
                                currentWidth = newW
                                currentHeight = newH
                                onUpdate(state.copy(widthDp = newW.dp, heightDp = newH.dp))
                            }
                        },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .padding(end = 4.dp, bottom = 4.dp)
                            .background(Color(0x66FFFFFF), CircleShape)
                    )
                }
            }
        }
    }
}
