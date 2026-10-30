package com.ecrisar.flow.edge.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecrisar.flow.edge.model.EdgePanelSettings
import kotlin.math.roundToInt

/**
 * Samsung One UI style Edge Handle.
 * - Sits along the right edge of the screen.
 * - Supports vertical dragging to reposition anywhere along the edge.
 * - Supports swiping inward or tapping to expand the edge drawer.
 */
@Composable
fun EdgeHandleView(
    settings: EdgePanelSettings,
    isPanelOpen: Boolean,
    onOpenPanel: () -> Unit,
    onPositionChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var isDraggingPosition by remember { mutableStateOf(false) }
    var currentYPercent by remember(settings.handlePositionPercent) {
        mutableFloatStateOf(settings.handlePositionPercent)
    }

    val animatedWidth by animateDpAsState(
        targetValue = if (isDraggingPosition) (settings.handleWidthDp + 8).dp else settings.handleWidthDp.dp,
        animationSpec = spring(),
        label = "handleWidth"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isDraggingPosition) 1.0f else settings.handleAlpha,
        animationSpec = spring(),
        label = "handleAlpha"
    )

    val handleColor = Color(settings.handleColorHex)
    val shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)

    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.TopEnd
    ) {
        // Vertical positioning container
        Box(
            modifier = Modifier
                .offset {
                    // Calculate pixel offset from top based on screen height
                    // We can use parent size or approximate with screen fraction
                    IntOffset(0, 0)
                }
                .fillMaxHeight()
        ) {
            // Pill container placed at currentYPercent
            Box(
                modifier = Modifier
                    .fillMaxHeight(fraction = currentYPercent)
            )

            // Actual handle pill placed below the spacer
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        // Offset dynamically
                        IntOffset(0, 0)
                    }
                    .padding(
                        top = (currentYPercent * 800).dp // Scaled for normal screen bounds
                    )
                    .pointerInput(settings.isVibrationEnabled) {
                        detectDragGestures(
                            onDragStart = {
                                isDraggingPosition = true
                                if (settings.isVibrationEnabled) {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                }
                            },
                            onDragEnd = {
                                isDraggingPosition = false
                                onPositionChanged(currentYPercent)
                                if (settings.isVibrationEnabled) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                }
                            },
                            onDragCancel = {
                                isDraggingPosition = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // If horizontal swipe to the left, open panel
                                if (dragAmount.x < -15f && !isDraggingPosition) {
                                    onOpenPanel()
                                } else {
                                    // Vertical drag to reposition
                                    val screenHeightPx = view.height.toFloat().coerceAtLeast(1000f)
                                    val deltaPercent = dragAmount.y / screenHeightPx
                                    currentYPercent = (currentYPercent + deltaPercent).coerceIn(0.08f, 0.90f)
                                }
                            }
                        )
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (settings.isVibrationEnabled) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                        onOpenPanel()
                    }
            ) {
                // If dragging position, show a helper tooltip to the left of the handle
                if (isDraggingPosition) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = (-95).dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xEE22242A),
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Position: ${(currentYPercent * 100).roundToInt()}%",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // The sleek One UI handle pill
                Box(
                    modifier = Modifier
                        .width(animatedWidth)
                        .height(settings.handleHeightDp.dp)
                        .shadow(
                            elevation = if (isDraggingPosition) 12.dp else 4.dp,
                            shape = shape,
                            spotColor = handleColor.copy(alpha = 0.6f)
                        )
                        .clip(shape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    handleColor.copy(alpha = animatedAlpha),
                                    handleColor.copy(alpha = (animatedAlpha * 0.75f))
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDraggingPosition) {
                        Icon(
                            imageVector = Icons.Default.UnfoldMore,
                            contentDescription = "Adjust Position",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Open Edge Panel",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
