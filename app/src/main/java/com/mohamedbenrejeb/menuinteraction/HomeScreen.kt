package com.mohamedbenrejeb.menuinteraction

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mohamedbenrejeb.menuinteraction.shapes.DrawShape
import com.mohamedbenrejeb.menuinteraction.ui.theme.MenuInteractionTheme
import kotlinx.coroutines.launch
import kotlin.math.*

@Composable
fun HomeScreen() {
    val layoutDirection = LocalLayoutDirection.current
    val directionFactor = if (layoutDirection == LayoutDirection.Rtl) -1 else 1

    val scope = rememberCoroutineScope()
    val buttonSize = 60.dp

    val buttonSizePx = with(LocalDensity.current) { buttonSize.toPx() }
    val dragSizePx = buttonSizePx * 1.5f

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    var isDragging by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf<Position?>(null) }

    LaunchedEffect(offsetX.value, offsetY.value) {
        val newPosition = getPosition(
            offset = Offset(offsetX.value, offsetY.value),
            buttonSizePx = buttonSizePx
        )
        currentPosition = newPosition
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Draw the shape at the center of the screen
        currentPosition?.let { position ->
            DrawShape(
                position = position,
                isSelected = true // Always true while dragging
            )
        }

        // Joystick at bottom right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd) // <<< Important move joystick to bottom right
                .size(buttonSize * 3),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White, CircleShape)
            )

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (offsetX.value).roundToInt(),
                            y = (offsetY.value).roundToInt()
                        )
                    }
                    .size(buttonSize)
                    .background(Color.DarkGray, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                scope.launch { offsetX.animateTo(0f) }
                                scope.launch { offsetY.animateTo(0f) }
                                isDragging = false
                                currentPosition = null // Reset shape when released
                            },
                            onDragCancel = {
                                scope.launch { offsetX.animateTo(0f) }
                                scope.launch { offsetY.animateTo(0f) }
                                isDragging = false
                                currentPosition = null // Reset shape when cancelled
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()

                                scope.launch {
                                    val newOffsetX = offsetX.value + dragAmount.x * directionFactor
                                    val newOffsetY = offsetY.value + dragAmount.y

                                    if (sqrt(newOffsetX.pow(2) + newOffsetY.pow(2)) < dragSizePx) {
                                        offsetX.snapTo(newOffsetX)
                                        offsetY.snapTo(newOffsetY)
                                    } else if (sqrt(offsetX.value.pow(2) + newOffsetY.pow(2)) < dragSizePx) {
                                        offsetY.snapTo(newOffsetY)
                                    } else if (sqrt(newOffsetX.pow(2) + offsetY.value.pow(2)) < dragSizePx) {
                                        offsetX.snapTo(newOffsetX)
                                    }
                                }
                            }
                        )
                    }
            )

            val buttonAlpha = remember { Animatable(0f) }

            LaunchedEffect(isDragging) {
                if (isDragging) {
                    buttonAlpha.animateTo(1f)
                } else {
                    buttonAlpha.animateTo(0f)
                }
            }

            Position.values().forEach { position ->
                val offset = position.getOffset(buttonSizePx)
                MyButton(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = offset.x.roundToInt(),
                                y = offset.y.roundToInt()
                            )
                        }
                        .graphicsLayer {
                            alpha = buttonAlpha.value
                            scaleX = buttonAlpha.value
                            scaleY = buttonAlpha.value
                        }
                        .size(buttonSize)
                        .padding(8.dp),
                    isSelected = position == currentPosition,
                    position = position
                )
            }
        }
    }
}


@Composable
fun MyButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    position: Position,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .fillMaxSize()
            .background(Color.DarkGray.copy(alpha = 0.5f))
        ,
        contentAlignment = Alignment.Center
    ) {
        DrawShape(
            position,
            isSelected = isSelected
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MenuInteractionTheme {
        HomeScreen()
    }
}