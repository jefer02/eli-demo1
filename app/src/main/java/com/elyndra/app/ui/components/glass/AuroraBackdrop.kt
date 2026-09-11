package com.elyndra.app.ui.components.glass

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.elyndra.app.ui.theme.ElyndraPaper
import com.elyndra.app.ui.theme.LocalGlass

/**
 * The ground every glass surface in the app sits on: warm paper, with two slow
 * accent auroras drifting across it.
 *
 * The auroras are drawn as radial gradients rather than as solid shapes behind
 * a [androidx.compose.ui.draw.blur]. Blur clips to its layer bounds, so a
 * blurred circle near an edge reads as a hard-cut rectangle - which is exactly
 * what sank the previous version of this backdrop. A radial gradient is soft by
 * construction, costs one draw call, and never clips.
 */
@Composable
fun AuroraBackdrop(modifier: Modifier = Modifier, animated: Boolean = true) {
    val glass = LocalGlass.current
    val accent = glass.accent

    val transition = rememberInfiniteTransition(label = "aurora")
    val driftA by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(17_000), RepeatMode.Reverse),
        label = "auroraA",
    )
    val driftB by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(24_000), RepeatMode.Reverse),
        label = "auroraB",
    )

    Box(modifier = modifier.fillMaxSize().background(ElyndraPaper)) {
        Canvas(Modifier.fillMaxSize()) {
            // A warm wash falling from the top edge, so the canvas isn't a flat gray.
            drawRect(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF3EA), ElyndraPaper),
                    center = Offset(size.width * 0.5f, -size.height * 0.1f),
                    radius = size.maxDimension * 0.9f,
                ),
            )
            aurora(
                color = accent.light,
                alpha = 0.40f,
                center = Offset(size.width * (0.28f + 0.06f * driftA), size.height * (0.20f - 0.04f * driftA)),
                radius = size.maxDimension * (0.46f + 0.07f * driftA),
            )
            aurora(
                color = accent.deep,
                alpha = 0.22f,
                center = Offset(size.width * (0.78f - 0.05f * driftB), size.height * (0.82f + 0.04f * driftB)),
                radius = size.maxDimension * (0.44f + 0.06f * driftB),
            )
        }
    }
}

/** One soft field of accent light. Fades to fully transparent, so blobs stack cleanly. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.aurora(
    color: Color,
    alpha: Float,
    center: Offset,
    radius: Float,
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), color.copy(alpha = 0f)),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}

/** The ground with no motion - for screens captured in tests and previews. */
@Composable
fun StaticAuroraBackdrop(modifier: Modifier = Modifier) = AuroraBackdrop(modifier, animated = false)
