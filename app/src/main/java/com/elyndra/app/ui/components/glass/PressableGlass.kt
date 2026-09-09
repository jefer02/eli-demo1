package com.elyndra.app.ui.components.glass

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.elyndra.app.ui.theme.GLASS_PRESS_SCALE

/** Spring-driven scale + glow while [interactionSource] reports a press. */
class GlassPressState internal constructor(
    val scale: State<Float>,
    val glow: State<Float>,
)

@Composable
fun rememberGlassPressState(interactionSource: InteractionSource): GlassPressState {
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = animateFloatAsState(
        targetValue = if (isPressed) GLASS_PRESS_SCALE else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "glassPressScale",
    )
    val glow = animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "glassPressGlow",
    )

    return remember(scale, glow) { GlassPressState(scale, glow) }
}

/**
 * Applies [state]'s scale/glow via the lambda overload of [graphicsLayer] and
 * a plain [drawWithContent] - both read the underlying `State<Float>` at draw
 * time, so a press animates without recomposing whatever this is attached to.
 */
fun Modifier.glassPress(state: GlassPressState): Modifier = this
    .graphicsLayer {
        scaleX = state.scale.value
        scaleY = state.scale.value
    }
    .drawWithContent {
        drawContent()
        val glow = state.glow.value
        if (glow > 0.01f) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.16f * glow), Color.Transparent),
                    radius = size.maxDimension * 0.75f,
                ),
            )
        }
    }
