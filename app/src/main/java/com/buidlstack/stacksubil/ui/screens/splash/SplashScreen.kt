package com.buidlstack.stacksubil.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.buidlstack.stacksubil.ui.theme.AccentCyan
import com.buidlstack.stacksubil.ui.theme.AccentMint
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.math.cos

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    var startAnim by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnim = true
        delay(2200)
        onFinished()
    }

    val logoOffset by animateFloatAsState(
        targetValue = if (startAnim) 0f else 30f,
        animationSpec = tween(900, easing = EaseOutQuart),
        label = "offset"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(700, delayMillis = 100),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 48f
            val cols = (size.width / gridSize).toInt() + 2
            val rows = (size.height / gridSize).toInt() + 2
            val lineColor = AccentCyan.copy(alpha = 0.04f)

            for (i in 0..cols) {
                drawLine(
                    color = lineColor,
                    start = Offset(i * gridSize, 0f),
                    end = Offset(i * gridSize, size.height),
                    strokeWidth = 1f
                )
            }
            for (j in 0..rows) {
                drawLine(
                    color = lineColor,
                    start = Offset(0f, j * gridSize),
                    end = Offset(size.width, j * gridSize),
                    strokeWidth = 1f
                )
            }

            val dotPositions = listOf(
                Offset(0.15f, 0.2f), Offset(0.8f, 0.15f), Offset(0.25f, 0.75f),
                Offset(0.7f, 0.8f), Offset(0.5f, 0.1f), Offset(0.9f, 0.5f),
                Offset(0.1f, 0.55f), Offset(0.6f, 0.9f)
            )
            dotPositions.forEachIndexed { index, pos ->
                val phase = (pulse + index * 0.15f) % 1f
                val dotAlpha = (sin(phase * Math.PI * 2).toFloat() * 0.5f + 0.5f) * 0.6f
                drawCircle(
                    color = AccentCyan.copy(alpha = dotAlpha),
                    radius = 3f,
                    center = Offset(size.width * pos.x, size.height * pos.y)
                )
                if (phase < 0.5f) {
                    drawCircle(
                        color = AccentCyan.copy(alpha = dotAlpha * 0.3f),
                        radius = 3f + phase * 20f,
                        center = Offset(size.width * pos.x, size.height * pos.y)
                    )
                }
            }

            val cx = size.width * 0.5f
            val cy = size.height * 0.5f
            val radius = 120f
            for (i in 0 until 6) {
                val angle = (i * 60.0) * (Math.PI / 180.0)
                val x = cx + radius * cos(angle).toFloat()
                val y = cy + radius * sin(angle).toFloat()
                drawLine(
                    color = AccentMint.copy(alpha = 0.08f),
                    start = Offset(cx, cy),
                    end = Offset(x, y),
                    strokeWidth = 1f
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = logoOffset.dp)
        ) {
            Canvas(modifier = Modifier.size(80.dp)) {
                val cx = size.width / 2
                val cy = size.height / 2
                val r = size.width / 2 - 4f
                drawCircle(
                    color = AccentCyan.copy(alpha = 0.15f),
                    radius = r,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = AccentCyan.copy(alpha = 0.4f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                )
                drawLine(
                    color = AccentCyan,
                    start = Offset(cx - r * 0.5f, cy),
                    end = Offset(cx + r * 0.5f, cy),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = AccentMint,
                    start = Offset(cx, cy - r * 0.5f),
                    end = Offset(cx, cy + r * 0.5f),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
                for (i in -2..2) {
                    val tx = cx + i * r * 0.25f
                    drawLine(
                        color = AccentCyan.copy(alpha = 0.5f),
                        start = Offset(tx, cy - 6f),
                        end = Offset(tx, cy + 6f),
                        strokeWidth = 1.5f,
                        cap = StrokeCap.Round
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Build Stack",
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = Brush.horizontalGradient(listOf(AccentCyan, AccentMint))
                ),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Smart measurement notes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
