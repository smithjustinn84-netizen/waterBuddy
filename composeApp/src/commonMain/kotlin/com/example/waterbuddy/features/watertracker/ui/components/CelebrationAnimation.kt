package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CelebrationAnimation(
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {},
) {
    val particles = remember {
        List(100) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f, // Start above the screen
                color = confettiColors.random(),
                size = Random.nextFloat() * 20f + 10f,
                speed = Random.nextFloat() * 1000f + 500f,
                angle = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 360f - 180f,
            )
        }
    }

    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing),
        )
        onAnimationEnd()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val progress = animatable.value
        particles.forEach { particle ->
            val yPos = (particle.y * size.height) + (progress * (size.height + particle.speed))
            val xPos = particle.x * size.width + (sin(progress * 5 + particle.angle) * 50).toFloat()

            if (yPos in -100f..size.height + 100f) {
                rotate(degrees = particle.angle + progress * particle.rotationSpeed, pivot = Offset(xPos, yPos)) {
                    drawRect(
                        color = particle.color,
                        topLeft = Offset(xPos, yPos),
                        size = Size(particle.size, particle.size / 2),
                        alpha = 1f - progress,
                    )
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val speed: Float,
    val angle: Float,
    val rotationSpeed: Float,
)

private val confettiColors = listOf(
    Color(0xFFF44336),
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF673AB7),
    Color(0xFF3F51B5),
    Color(0xFF2196F3),
    Color(0xFF03A9F4),
    Color(0xFF00BCD4),
    Color(0xFF009688),
    Color(0xFF4CAF50),
    Color(0xFF8BC34A),
    Color(0xFFCDDC39),
    Color(0xFFFFEB3B),
    Color(0xFFFFC107),
    Color(0xFFFF9800),
    Color(0xFFFF5722),
)
