package com.elyndra.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * One "thickness" of liquid glass material - how much of what's behind shows
 * through, how bright the specular edge is, how deep the inner shadow reads.
 * Mirrors the ultraThin/thin/regular/thick/ultraThick vocabulary of native
 * glass materials (visionOS, iOS) so the mapping from reference screenshots
 * to code stays obvious once we have them.
 */
data class GlassStyle(
    val tintAlpha: Float,
    val borderAlpha: Float,
    val highlightAlpha: Float,
    val shadowAlpha: Float,
)

object GlassMaterials {
    val ultraThin = GlassStyle(tintAlpha = 0.05f, borderAlpha = 0.28f, highlightAlpha = 0.22f, shadowAlpha = 0.10f)
    val thin = GlassStyle(tintAlpha = 0.09f, borderAlpha = 0.34f, highlightAlpha = 0.28f, shadowAlpha = 0.14f)
    val regular = GlassStyle(tintAlpha = 0.14f, borderAlpha = 0.40f, highlightAlpha = 0.34f, shadowAlpha = 0.18f)
    val thick = GlassStyle(tintAlpha = 0.22f, borderAlpha = 0.46f, highlightAlpha = 0.40f, shadowAlpha = 0.24f)
}

object GlassMetrics {
    val cardRadius = 28.dp
    val panelRadius = 32.dp
    val chipRadius = 100.dp // effectively a pill at any reasonable height
    val borderWidth = 1.2.dp
}

/** How far a card lifts and glows once pressed/focused - spring-driven, see PressableGlass.kt. */
internal const val GLASS_PRESS_SCALE = 1.045f
internal val GLASS_PRESS_ELEVATION: Dp = 18.dp
