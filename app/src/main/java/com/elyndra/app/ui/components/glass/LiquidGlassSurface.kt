package com.elyndra.app.ui.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics
import com.elyndra.app.ui.theme.GlassStyle

/**
 * The core liquid-glass material: a translucent fill tinted by [tint], a
 * specular border that catches light top-left and fades toward the
 * bottom-right, and soft inner highlight/shadow gradients that read as
 * volume and refraction rather than a flat tinted rectangle.
 *
 * Deliberately does not attempt live backdrop blur of whatever scrolls behind
 * it - see GlassBackdrop.kt for the reasoning. The "glass" reads through
 * translucency, light and depth cues, which is also far cheaper to redraw
 * every frame during a fling than a real per-pixel backdrop capture.
 *
 * Gradients are computed in [Modifier.drawWithCache], so they're rebuilt only
 * when this surface's size changes, not on every recomposition or frame.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(GlassMetrics.cardRadius),
    style: GlassStyle = GlassMaterials.regular,
    tint: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(tint.copy(alpha = style.tintAlpha))
            .drawWithCache {
                val highlight = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = style.highlightAlpha), Color.Transparent),
                    center = Offset(size.width * 0.15f, -size.height * 0.1f),
                    radius = size.maxDimension * 0.75f,
                )
                val innerShadow = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = style.shadowAlpha), Color.Transparent),
                    center = Offset(size.width * 1.05f, size.height * 1.1f),
                    radius = size.maxDimension * 0.85f,
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(brush = highlight)
                    drawRect(brush = innerShadow)
                }
            }
            .border(
                width = GlassMetrics.borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = style.borderAlpha),
                        Color.White.copy(alpha = style.borderAlpha * 0.25f),
                        Color.White.copy(alpha = style.borderAlpha * 0.6f),
                    ),
                ),
                shape = shape,
            ),
        content = content,
    )
}
