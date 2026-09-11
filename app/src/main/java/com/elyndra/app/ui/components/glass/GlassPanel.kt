package com.elyndra.app.ui.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elyndra.app.ui.theme.GlassMetrics
import com.elyndra.app.ui.theme.LocalGlass

/**
 * A panel of the shell's glass, tuned by whatever the user set in
 * Settings > Liquid Glass: [com.elyndra.app.ui.theme.GlassSettings.fill] is the
 * tint at their chosen opacity, with a white specular top edge over it.
 *
 * On the *blur* half of that setting: there is no backdrop-blur API in Compose,
 * and blurring a copy of everything behind a panel every frame is not
 * affordable during a fling. Instead the ground itself
 * ([AuroraBackdrop]) is soft by construction, and the user's blur value drives
 * how far this panel's own light spreads - a thicker blur reads as a more
 * diffuse, milkier panel, which is the cue the setting is actually reaching
 * for. Panels over cover art rely on translucency alone.
 */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(GlassMetrics.panelRadius),
    elevation: Dp = 10.dp,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val glass = LocalGlass.current
    // 0..40dp of blur maps onto how wide the internal highlight spreads.
    val diffusion = (glass.blur.value / 40f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .shadow(elevation, shape, ambientColor = ShadowInk, spotColor = ShadowInk)
            .clip(shape)
            .background(glass.fill)
            .drawWithCache {
                val highlight = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.28f + 0.34f * diffusion), Color.Transparent),
                    center = Offset(size.width * 0.15f, -size.height * 0.1f),
                    radius = size.maxDimension * (0.55f + 0.55f * diffusion),
                )
                val innerShadow = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.10f), Color.Transparent),
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
                    listOf(
                        glass.border,
                        glass.border.copy(alpha = glass.border.alpha * 0.3f),
                        glass.border.copy(alpha = glass.border.alpha * 0.65f),
                    ),
                ),
                shape = shape,
            ),
        content = content,
    )
}

/**
 * The dark counterpart, for chrome that floats over cover art instead of over
 * paper - the hero's top bar buttons, the emulator chip. Fixed rather than
 * user-tunable: it has to stay legible against artwork of any brightness.
 */
@Composable
fun DarkGlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(GlassMetrics.chipRadius),
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.16f))
            .border(GlassMetrics.borderWidth, Color.White.copy(alpha = 0.30f), shape),
        content = content,
    )
}

/** Shadows in this shell are ink, never black - black reads as a hole in the paper. */
private val ShadowInk = Color(0xFF333333)
