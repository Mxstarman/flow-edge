package com.ecrisar.flow.edge.ui.panels

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecrisar.flow.edge.data.LauncherHelper
import com.ecrisar.flow.edge.model.AppItem
import com.ecrisar.flow.edge.ui.components.LocalDragDropState
import com.ecrisar.flow.edge.ui.theme.OneUIBlue

/**
 * Apps Edge Panel:
 * - Shows favorite apps or all apps
 * - Tap to launch standard
 * - Long-press & drag onto screen to open in Pop-up view!
 * - Manage favorites with star toggle
 */
@Composable
fun AppsPanel(
    favoriteApps: List<AppItem>,
    allApps: List<AppItem>,
    onToggleFavorite: (AppItem) -> Unit,
    onAppClicked: (AppItem) -> Unit,
    onAppDropped: (AppItem, Offset) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val dragDropState = LocalDragDropState.current

    var showAllApps by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val displayedApps = remember(showAllApps, searchQuery, favoriteApps, allApps) {
        val baseList = if (showAllApps) allApps else favoriteApps
        if (searchQuery.isBlank()) {
            baseList
        } else {
            baseList.filter { it.label.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Top Header with Title and Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (showAllApps) "All Apps" else "Favorite Apps",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Drag icon to screen for Pop-up view",
                    color = Color(0xFF9E9E9E),
                    fontSize = 10.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (showAllApps) OneUIBlue else Color(0xFF2C2F36),
                modifier = Modifier.clickable {
                    showAllApps = !showAllApps
                    searchQuery = ""
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (showAllApps) "Favorites" else "All Apps",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Search bar (visible in All Apps mode)
        AnimatedVisibility(visible = showAllApps) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                placeholder = { Text("Search apps...", fontSize = 12.sp, color = Color(0xFF888B94)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF888B94),
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF888B94),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OneUIBlue,
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedContainerColor = Color(0xFF22242A),
                    unfocusedContainerColor = Color(0xFF1E1F24),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grid of Apps (2 columns matching Samsung Edge Panel)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayedApps, key = { it.packageName }) { app ->
                AppGridItem(
                    app = app,
                    onAppClicked = { onAppClicked(app) },
                    onToggleFavorite = { onToggleFavorite(app) },
                    onStartDrag = { initialOffset ->
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        dragDropState.startDrag(app, initialOffset)
                    },
                    onDrag = { newOffset ->
                        dragDropState.updateDrag(newOffset)
                    },
                    onEndDrag = {
                        dragDropState.endDrag { droppedApp, offset ->
                            onAppDropped(droppedApp, offset)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AppGridItem(
    app: AppItem,
    onAppClicked: () -> Unit,
    onToggleFavorite: () -> Unit,
    onStartDrag: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onEndDrag: () -> Unit
) {
    var itemPositionInRoot by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                itemPositionInRoot = coordinates.positionInRoot()
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        onStartDrag(itemPositionInRoot + offset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(itemPositionInRoot + change.position)
                    },
                    onDragEnd = { onEndDrag() },
                    onDragCancel = { onEndDrag() }
                )
            }
            .clickable { onAppClicked() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            // App Icon Pill / Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(8.dp, CircleShape, spotColor = app.accentColor.copy(alpha = 0.5f))
                    .clip(CircleShape)
                    .background(app.accentColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.label.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            // Star indicator for favorites
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .offset(x = 4.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(Color(0xEE1E1F24))
                    .clickable { onToggleFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (app.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (app.isFavorite) Color(0xFFFFD700) else Color(0xFF6B7280),
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = app.label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(68.dp)
        )
    }
}
