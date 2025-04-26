package com.mohamedbenrejeb.menuinteraction

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun WarGame() {
    val scope = rememberCoroutineScope()
    val screenWidthPx = with(LocalDensity.current) { 1080.dp.toPx() } // Approximate width
    val screenHeightPx = with(LocalDensity.current) { 1920.dp.toPx() } // Approximate height

    // Player positions
    var player1Pos by remember { mutableStateOf(Offset(300f, 1500f)) }
    var player2Pos by remember { mutableStateOf(Offset(700f, 1500f)) }

    val offsetX1 = remember { Animatable(0f) }
    val offsetY1 = remember { Animatable(0f) }
    val offsetX2 = remember { Animatable(0f) }
    val offsetY2 = remember { Animatable(0f) }

    // Bullets fired by players
    var bullets by remember { mutableStateOf(listOf<Bullet>()) }

    // Enemies falling
    var enemies by remember { mutableStateOf(listOf<Enemy>()) }

    // Scores
    var player1Score by remember { mutableStateOf(0) }
    var player2Score by remember { mutableStateOf(0) }

    // Player 1 movement updater
    LaunchedEffect(offsetX1.value, offsetY1.value) {
        while (true) {
            delay(16)
            val newPos = player1Pos + Offset(offsetX1.value * 10f, offsetY1.value * 10f)
            // Clamp inside screen horizontally and vertically
            player1Pos = Offset(
                newPos.x.coerceIn(40f, screenWidthPx - 40f),
                newPos.y.coerceIn(1000f, screenHeightPx - 40f)
            )
        }
    }

    // Player 2 movement updater
    LaunchedEffect(offsetX2.value, offsetY2.value) {
        while (true) {
            delay(16)
            val newPos = player2Pos + Offset(offsetX2.value * 10f, offsetY2.value * 10f)
            player2Pos = Offset(
                newPos.x.coerceIn(40f, screenWidthPx - 40f),
                newPos.y.coerceIn(1000f, screenHeightPx - 40f)
            )
        }
    }

    // Enemy spawning and movement
    LaunchedEffect(Unit) {
        while (true) {
            delay(800)
            // Spawn enemy at random X top position
            val enemyX = Random.nextFloat() * (screenWidthPx - 80f) + 40f
            enemies = enemies + Enemy(Offset(enemyX, 0f))
        }
    }

    // Game loop: update bullets, enemies and check collisions
    LaunchedEffect(bullets, enemies) {
        while (true) {
            delay(16)
            // Move bullets upwards
            bullets = bullets.mapNotNull {
                val newPos = it.position - Offset(0f, 15f)
                if (newPos.y < 0) null else it.copy(position = newPos)
            }

            // Move enemies downwards
            enemies = enemies.mapNotNull {
                val newPos = it.position + Offset(0f, 6f)
                if (newPos.y > screenHeightPx) null else it.copy(position = newPos)
            }

            // Collision detection: bullets vs enemies
            val remainingEnemies = mutableListOf<Enemy>()
            val remainingBullets = mutableListOf<Bullet>()

            for (enemy in enemies) {
                var hit = false
                for (bullet in bullets) {
                    if (enemy.position.getDistanceTo(bullet.position) < 40f) {
                        // Bullet hits enemy
                        hit = true
                        // Add score for player who fired bullet
                        if (bullet.owner == PlayerId.Player1) player1Score++ else player2Score++
                        break
                    }
                }
                if (!hit) remainingEnemies.add(enemy)
            }

            for (bullet in bullets) {
                val hitsEnemy = enemies.any { enemy -> enemy.position.getDistanceTo(bullet.position) < 40f }
                if (!hitsEnemy) remainingBullets.add(bullet)
            }

            enemies = remainingEnemies
            bullets = remainingBullets
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw players as triangles (fighter shapes)
            drawTriangle(player1Pos, Color.Green)
            drawTriangle(player2Pos, Color.Red)

            // Draw bullets
            bullets.forEach {
                drawCircle(Color.Yellow, 10f, it.position)
            }

            // Draw enemies as circles
            enemies.forEach {
                drawCircle(Color.Magenta, 30f, it.position)
            }
        }

        // Joystick for Player 1
        Joystick(
            modifier = Modifier.align(Alignment.BottomStart).padding(32.dp),
            offsetX = offsetX1,
            offsetY = offsetY1,
            buttonSize = 60.dp,
            directionFactor = 1
        )

        // Joystick for Player 2
        Joystick(
            modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp),
            offsetX = offsetX2,
            offsetY = offsetY2,
            buttonSize = 60.dp,
            directionFactor = 1
        )

        // Fire buttons for each player
        FireButton(
            modifier = Modifier.align(Alignment.BottomCenter).padding(start = 100.dp, bottom = 100.dp),
            onFire = {
                bullets = bullets + Bullet(player1Pos - Offset(0f, 40f), PlayerId.Player1)
            },
            color = Color.Green
        )

        FireButton(
            modifier = Modifier.align(Alignment.BottomCenter).padding(end = 100.dp, bottom = 100.dp),
            onFire = {
                bullets = bullets + Bullet(player2Pos - Offset(0f, 40f), PlayerId.Player2)
            },
            color = Color.Red
        )

        // Display scores
        Text(
            text = "Player 1 Score: $player1Score",
            color = Color.Green,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )

        Text(
            text = "Player 2 Score: $player2Score",
            color = Color.Red,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        )
    }
}

fun DrawScope.drawTriangle(center: Offset, color: Color) {
    val size = 40f
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x - size, center.y + size)
        lineTo(center.x + size, center.y + size)
        close()
    }
    drawPath(path, color)
}

@Composable
fun FireButton(
    modifier: Modifier,
    onFire: () -> Unit,
    color: Color
) {
    androidx.compose.material3.Button(
        onClick = onFire,
        modifier = modifier.size(80.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text("FIRE", color = Color.White)
    }
}

@Composable
fun Joystick(
    modifier: Modifier,
    offsetX: Animatable<Float, AnimationVector1D>,
    offsetY: Animatable<Float, AnimationVector1D>,
    buttonSize: Dp,
    directionFactor: Int
) {
    val scope = rememberCoroutineScope()
    val buttonSizePx = with(LocalDensity.current) { buttonSize.toPx() }
    val dragSizePx = buttonSizePx * 1.5f

    Box(
        modifier = modifier.size(buttonSize * 3),
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
                                if (sqrt(newOffsetX * newOffsetX + newOffsetY * newOffsetY) < dragSizePx) {
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
fun Offset.getDistanceTo(other: Offset): Float {
    return sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2))
}
data class Bullet(val position: Offset, val owner: PlayerId)
data class Enemy(val position: Offset)
enum class PlayerId { Player1, Player2 }
