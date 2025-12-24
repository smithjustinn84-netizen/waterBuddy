package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FluidBackground(
    progress: Float,
    isGoalReached: Boolean,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "waveTransition")

    val waveOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "waveOffset",
    )

    val fluidColor by animateColorAsState(
        targetValue = if (isGoalReached) {
            Color(0xFF00CED1).copy(alpha = 0.25f) // Brighter teal when goal reached
        } else {
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        },
        animationSpec = tween(1000),
        label = "fluidColor",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val waterHeight = height * progress.coerceIn(0f, 1.1f)
        val waveAmplitude = 12.dp.toPx()

        val path = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height - waterHeight)

            val steps = 40
            for (i in 0..steps) {
                val x = width * (i.toFloat() / steps)
                val waveProgress = i.toFloat() / steps
                val waveY = sin((waveProgress * 2 * PI) + (waveOffset * 2 * PI)) * waveAmplitude
                lineTo(x, height - waterHeight + waveY.toFloat())
            }

            lineTo(width, height)
            close()
        }

        drawPath(path, fluidColor)
    }
}
