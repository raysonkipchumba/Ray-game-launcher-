package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CatalogGameModel
import com.example.model.GameType
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GreenAccent
import kotlinx.coroutines.delay

@Composable
fun MiniGameScreen(
    game: CatalogGameModel,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = game.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF0D131A), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "⚡ GAME TURBO ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
            }
        }

        // Active Game Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF070B10), RoundedCornerShape(12.dp))
        ) {
            when (game.type) {
                GameType.SPACE_RUNNER -> SpaceRunnerCanvas()
                GameType.BRICK_BREAKER -> BrickBreakerCanvas()
                else -> SpaceRunnerCanvas()
            }
        }
    }
}

@Composable
fun SpaceRunnerCanvas() {
    var playerY by remember { mutableFloatStateOf(200f) }
    var score by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    val obstacleX = remember { mutableStateListOf(800f, 1200f, 1600f) }
    val obstacleY = remember { mutableStateListOf(150f, 300f, 100f) }

    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            delay(16)
            score += 1
            for (i in obstacleX.indices) {
                obstacleX[i] = obstacleX[i] - 12f
                if (obstacleX[i] < -50f) {
                    obstacleX[i] = 1000f + (0..300).random()
                    obstacleY[i] = (50..350).random().toFloat()
                }

                // Simple collision detection with player at x = 100f
                if (obstacleX[i] in 70f..130f && Math.abs(obstacleY[i] - playerY) < 40f) {
                    isGameOver = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    playerY = (playerY + dragAmount.y).coerceIn(50f, 400f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Player Jet
            drawCircle(
                color = CyanAccent,
                radius = 20f,
                center = Offset(100f, playerY)
            )

            // Draw Obstacles
            for (i in obstacleX.indices) {
                drawRect(
                    color = Color(0xFFFF0055),
                    topLeft = Offset(obstacleX[i], obstacleY[i]),
                    size = Size(30f, 30f)
                )
            }
        }

        // Overlay Score & Restart
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text(
                text = "SCORE: $score",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GAME OVER",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF0055)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Final Score: $score", fontSize = 16.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            score = 0
                            playerY = 200f
                            obstacleX[0] = 800f
                            obstacleX[1] = 1200f
                            obstacleX[2] = 1600f
                            isGameOver = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Play Again", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BrickBreakerCanvas() {
    var paddleX by remember { mutableFloatStateOf(400f) }
    var ballX by remember { mutableFloatStateOf(400f) }
    var ballY by remember { mutableFloatStateOf(300f) }
    var ballVx by remember { mutableFloatStateOf(8f) }
    var ballVy by remember { mutableFloatStateOf(-8f) }
    var score by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            ballX += ballVx
            ballY += ballVy

            // Wall bounces
            if (ballX < 20f || ballX > 1200f) ballVx = -ballVx
            if (ballY < 20f) ballVy = -ballVy

            // Paddle collision at bottom y ~ 380f
            if (ballY > 360f && ballY < 390f && ballX in (paddleX - 80f)..(paddleX + 80f)) {
                ballVy = -Math.abs(ballVy)
                score += 10
            }

            if (ballY > 450f) {
                // Reset ball
                ballX = 400f
                ballY = 200f
                ballVy = -8f
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    paddleX = (paddleX + dragAmount.x).coerceIn(100f, 1100f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Ball
            drawCircle(
                color = GreenAccent,
                radius = 12f,
                center = Offset(ballX, ballY)
            )

            // Draw Paddle
            drawRect(
                color = CyanAccent,
                topLeft = Offset(paddleX - 60f, 380f),
                size = Size(120f, 16f)
            )
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text(
                text = "SCORE: $score",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
