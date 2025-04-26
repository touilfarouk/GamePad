package com.mohamedbenrejeb.menuinteraction

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mohamedbenrejeb.menuinteraction.shapes.DrawShape
import com.mohamedbenrejeb.menuinteraction.ui.theme.MenuInteractionTheme
import kotlinx.coroutines.CoroutineScope
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

    // Right joystick states
    val offsetXRight = remember { Animatable(0f) }
    val offsetYRight = remember { Animatable(0f) }
    var isDraggingRight by remember { mutableStateOf(false) }
    var currentPositionRight by remember { mutableStateOf<Position?>(null) }

    // Left joystick states
    val offsetXLeft = remember { Animatable(0f) }
    val offsetYLeft = remember { Animatable(0f) }
    var isDraggingLeft by remember { mutableStateOf(false) }
    var currentPositionLeft by remember { mutableStateOf<Position?>(null) }

    LaunchedEffect(offsetXRight.value, offsetYRight.value) {
        currentPositionRight = getPosition(
            offset = Offset(offsetXRight.value, offsetYRight.value),
            buttonSizePx = buttonSizePx
        )
    }

    LaunchedEffect(offsetXLeft.value, offsetYLeft.value) {
        currentPositionLeft = getPosition(
            offset = Offset(offsetXLeft.value, offsetYLeft.value),
            buttonSizePx = buttonSizePx
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Draw the shape for right joystick
        currentPositionRight?.let { position ->
            DrawShape(
                position = position,
                isSelected = true
            )
        }

        // Draw the shape for left joystick
        currentPositionLeft?.let { position ->
            DrawShape(
                position = position,
                isSelected = true
            )
        }

        // --- Right Joystick ---
        Joystick(
            modifier = Modifier.align(Alignment.BottomEnd),
            buttonSize = buttonSize,
            buttonSizePx = buttonSizePx,
            dragSizePx = dragSizePx,
            offsetX = offsetXRight,
            offsetY = offsetYRight,
            isDragging = isDraggingRight,
            onDraggingChanged = { isDraggingRight = it },
            currentPosition = currentPositionRight,
            onPositionChanged = { currentPositionRight = it },
            directionFactor = directionFactor,
            scope = scope
        )

        // --- Left Joystick ---
        Joystick(
            modifier = Modifier.align(Alignment.BottomStart),
            buttonSize = buttonSize,
            buttonSizePx = buttonSizePx,
            dragSizePx = dragSizePx,
            offsetX = offsetXLeft,
            offsetY = offsetYLeft,
            isDragging = isDraggingLeft,
            onDraggingChanged = { isDraggingLeft = it },
            currentPosition = currentPositionLeft,
            onPositionChanged = { currentPositionLeft = it },
            directionFactor = directionFactor,
            scope = scope
        )
    }
}

@Composable
fun Joystick(
    modifier: Modifier,
    buttonSize: Dp,
    buttonSizePx: Float,
    dragSizePx: Float,
    offsetX: Animatable<Float, AnimationVector1D>,
    offsetY: Animatable<Float, AnimationVector1D>,
    isDragging: Boolean,
    onDraggingChanged: (Boolean) -> Unit,
    currentPosition: Position?,
    onPositionChanged: (Position?) -> Unit,
    directionFactor: Int,
    scope: CoroutineScope
) {
    val buttonAlpha = remember { Animatable(0f) }

    LaunchedEffect(isDragging) {
        if (isDragging) {
            buttonAlpha.animateTo(1f)
        } else {
            buttonAlpha.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
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
                        onDragStart = { onDraggingChanged(true) },
                        onDragEnd = {
                            scope.launch { offsetX.animateTo(0f) }
                            scope.launch { offsetY.animateTo(0f) }
                            onDraggingChanged(false)
                            onPositionChanged(null)
                        },
                        onDragCancel = {
                            scope.launch { offsetX.animateTo(0f) }
                            scope.launch { offsetY.animateTo(0f) }
                            onDraggingChanged(false)
                            onPositionChanged(null)
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
            .background(Color.DarkGray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        DrawShape(
            position = position,
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
