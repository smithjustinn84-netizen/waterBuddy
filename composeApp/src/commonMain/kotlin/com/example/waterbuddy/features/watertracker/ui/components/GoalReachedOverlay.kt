package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterbuddy.core.theme.GrokkingTeal
import com.example.waterbuddy.core.theme.LifeWaterBlue
import com.example.waterbuddy.core.theme.MartianRed
import kotlinx.coroutines.delay

@Composable
fun GoalReachedOverlay(
    isVisible: Boolean,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(5000)
            onDismiss()
        }
    }

    val infiniteTransition = rememberInfiniteTransition()

    // High impact pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Box(
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(500, easing = OvershootInterpolator(2f).asEasing()),
            ),
            exit = fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 0.5f, animationSpec = tween(500)),
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                // Background Glow
                Box(
                    modifier = Modifier
                        .size(300.dp, 150.dp)
                        .graphicsLayer(scaleX = pulseScale * 1.2f, scaleY = pulseScale * 1.2f)
                        .blur(32.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(LifeWaterBlue.copy(alpha = glowAlpha), Color.Transparent),
                            ),
                            RoundedCornerShape(32.dp),
                        ),
                )

                // Main Card
                Box(
                    modifier = Modifier
                        .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    GrokkingTeal,
                                    LifeWaterBlue,
                                    MartianRed.copy(alpha = 0.8f),
                                ),
                            ),
                        )
                        .padding(horizontal = 32.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ZENITH ACHIEVED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                letterSpacing = 4.sp,
                                fontWeight = FontWeight.Black,
                            ),
                            color = Color.White.copy(alpha = 0.7f),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 36.sp,
                            ),
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

// Helper to use OvershootInterpolator in Compose
private class OvershootInterpolator(private val tension: Float = 2f) {
    fun asEasing() = Easing { x ->
        val t = x - 1f
        t * t * ((tension + 1f) * t + tension) + 1f
    }
}
