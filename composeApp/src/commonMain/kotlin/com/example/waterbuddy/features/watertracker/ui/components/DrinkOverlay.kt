package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.PI

@Composable
fun DrinkOverlay(
    amountMl: Int,
    modifier: Modifier = Modifier,
    maxAmountMl: Int = 1000
) {
    val fillPercentage = (amountMl.toFloat() / maxAmountMl.toFloat()).coerceIn(0f, 1f)
    val color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)

    val transition = rememberInfiniteTransition()
    val waveOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val waterHeight = height * fillPercentage
            val waveAmplitude = 20.dp.toPx()

            val path = Path().apply {
                moveTo(0f, height)
                lineTo(0f, height - waterHeight)

                // Draw wave
                val steps = 50
                for (i in 0..steps) {
                    val x = width * (i.toFloat() / steps)
                    val progress = i.toFloat() / steps
                    val waveY =
                        kotlin.math.sin((progress * 2 * PI) + (waveOffset * 2 * PI)) * waveAmplitude
                    lineTo(x, height - waterHeight + waveY.toFloat())
                }

                lineTo(width, height)
                close()
            }

            drawPath(path, color)
        }

        // Center Text
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$amountMl ml",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
