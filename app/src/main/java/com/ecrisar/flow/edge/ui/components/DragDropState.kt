package com.ecrisar.flow.edge.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecrisar.flow.edge.model.AppItem
import com.ecrisar.flow.edge.ui.theme.OneUIBlue
import kotlin.math.roundToInt

class DragDropState {
    var isDragging by mutableStateOf(false)
    var draggedApp by mutableStateOf<AppItem?>(null)
    var dragPosition by mutableStateOf(Offset.Zero)

    fun startDrag(app: AppItem, position: Offset) {
        draggedApp = app
        dragPosition = position
        isDragging = true
    }

    fun updateDrag(position: Offset) {
        dragPosition = position
    }

    fun endDrag(onDrop: (AppItem, Offset) -> Unit) {
        val app = draggedApp
        if (isDragging && app != null) {
            onDrop(app, dragPosition)
        }
        isDragging = false
        draggedApp = null
    }

    fun cancelDrag() {
        isDragging = false
        draggedApp = null
    }
}

val LocalDragDropState = compositionLocalOf { DragDropState() }

@Composable
fun DragDropContainer(
    state: DragDropState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    CompositionLocalProvider(LocalDragDropState provides state) {
        Box(modifier = modifier.fillMaxSize()) {
            content()

            // Floating drag preview item following the user's touch
            if (state.isDragging && state.draggedApp != null) {
                val app = state.draggedApp!!
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (state.dragPosition.x - 70).roundToInt(),
                                (state.dragPosition.y - 70).roundToInt()
                            )
                        }
                        .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = OneUIBlue)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xE61E1F24))
                        .border(1.5.dp, OneUIBlue, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(app.accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = app.label.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }

                        Text(
                            text = "Pop-up View",
                            color = Color.White,
                            fontSize = 13.sp
                        )

                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = OneUIBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Visual highlight indicator on the screen to show where the user can drop apps.
 */
@Composable
fun DropZoneIndicator(
    isDragging: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isDragging,
        enter = fadeIn() + scaleIn(initialScale = 0.95f),
        exit = fadeOut() + scaleOut(targetScale = 0.95f),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .border(2.dp, OneUIBlue.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
                .background(OneUIBlue.copy(alpha = 0.08f), RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xCC1A1B20),
                border = androidx.compose.foundation.BorderStroke(1.dp, OneUIBlue.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = OneUIBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Drop here to open in Pop-up view",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
