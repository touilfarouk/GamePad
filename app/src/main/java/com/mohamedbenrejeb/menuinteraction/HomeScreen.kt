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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

import androidx.compose.foundation.Canvas
@Composable
fun HomeScreen() {
    val layoutDirection = LocalLayoutDirection.current
    val directionFactor = if (layoutDirection == LayoutDirection.Rtl) -1 else 1
    val scope = rememberCoroutineScope()

    val buttonSize = 60.dp
    val buttonSizePx = with(LocalDensity.current) { buttonSize.toPx() }
    val dragSizePx = buttonSizePx * 1.5f

    // Player 1 movement
    var player1Pos by remember { mutableStateOf(Offset(500f, 500f)) }
    val offsetX1 = remember { Animatable(0f) }
    val offsetY1 = remember { Animatable(0f) }

    // Player 2 movement
    var player2Pos by remember { mutableStateOf(Offset(900f, 500f)) }
    val offsetX2 = remember { Animatable(0f) }
    val offsetY2 = remember { Animatable(0f) }

    // Movement control
    LaunchedEffect(offsetX1.value, offsetY1.value) {
        while (true) {
            delay(16) // ~60 FPS
            player1Pos += Offset(offsetX1.value * 0.2f, offsetY1.value * 0.2f)
        }
    }

    LaunchedEffect(offsetX2.value, offsetY2.value) {
        while (true) {
            delay(16)
            player2Pos += Offset(offsetX2.value * 0.2f, offsetY2.value * 0.2f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Player 1 Shape
        DrawMovingShape(
            center = player1Pos,
            color = Color.Green
        )

        // Player 2 Shape
        DrawMovingShape(
            center = player2Pos,
            color = Color.Red
        )

        // Joystick Player 1 (Bottom Start)
        Joystick(
            modifier = Modifier.align(Alignment.BottomStart),
            offsetX = offsetX1,
            offsetY = offsetY1,
            buttonSize = buttonSize,
            buttonSizePx = buttonSizePx,
            dragSizePx = dragSizePx,
            directionFactor = directionFactor
        )

        // Joystick Player 2 (Bottom End)
        Joystick(
            modifier = Modifier.align(Alignment.BottomEnd),
            offsetX = offsetX2,
            offsetY = offsetY2,
            buttonSize = buttonSize,
            buttonSizePx = buttonSizePx,
            dragSizePx = dragSizePx,
            directionFactor = directionFactor
        )
    }
}

@Composable
fun Joystick(
    modifier: Modifier,
    offsetX: Animatable<Float, AnimationVector1D>,
    offsetY: Animatable<Float, AnimationVector1D>,
    buttonSize: Dp,
    buttonSizePx: Float,
    dragSizePx: Float,
    directionFactor: Int
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .size(buttonSize * 3),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = offsetX.value.roundToInt(),
                        y = offsetY.value.roundToInt()
                    )
                }
                .size(buttonSize)
                .background(Color.DarkGray, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {},
                        onDragEnd = {
                            scope.launch {
                                offsetX.animateTo(0f)
                                offsetY.animateTo(0f)
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f)
                                offsetY.animateTo(0f)
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newOffsetX = offsetX.value + dragAmount.x * directionFactor
                                val newOffsetY = offsetY.value + dragAmount.y

                                if (sqrt(newOffsetX.pow(2) + newOffsetY.pow(2)) < dragSizePx) {
                                    offsetX.snapTo(newOffsetX)
                                    offsetY.snapTo(newOffsetY)
                                }
                            }
                        }
                    )
                }
        )
    }
}

@Composable
fun DrawMovingShape(
    center: Offset,
    color: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = color,
            radius = 40f,
            center = center
        )
    }
}

