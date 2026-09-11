package com.elyndra.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.elyndra.app.domain.model.AccentColor

/**
 * One accent's worth of scheme colors.
 *
 * The shell paints accents as *gradients*, not flat fills - every primary
 * button, chip and selection ring is a [light] to [deep] ramp - so both
 * endpoints are first-class here rather than derived. [swatch] is the same
 * ramp at settings-swatch size.
 */
data class AccentPalette(
    val light: Color,
    val deep: Color,
    val onAccent: Color,
    val container: Color,
    val onContainer: Color,
) {
    /** The accent ramp at the design's default 145 degrees. */
    val swatch: Brush get() = gradient()

    /**
     * [degrees] follows CSS `linear-gradient`: 0 points up, 90 points right.
     * Compose wants offsets, so callers that need an exact box size use
     * [accentGradient] instead; this is the size-agnostic form for fills that
     * are close enough to square.
     */
    fun gradient(degrees: Float = 145f): Brush = accentBrush(light, deep, degrees)

    /** Flat tone for text and icons that must stay legible on the paper canvas. */
    val ink: Color get() = deep
}

private val accentPalettes: Map<AccentColor, AccentPalette> = mapOf(
    AccentColor.MANDARINA to AccentPalette(
        light = Color(0xFFF59659), deep = Color(0xFFE26D19),
        onAccent = Color(0xFFFFFFFF), container = Color(0xFFFFE2CE), onContainer = Color(0xFF4A2100),
    ),
    AccentColor.FUEGO to AccentPalette(
        light = Color(0xFFEE7E28), deep = Color(0xFFB24A08),
        onAccent = Color(0xFFFFFFFF), container = Color(0xFFFFDCC2), onContainer = Color(0xFF3A1600),
    ),
    AccentColor.MENTA to AccentPalette(
        light = Color(0xFF9BD494), deep = Color(0xFF3F9A62),
        onAccent = Color(0xFF04301A), container = Color(0xFFD3F0CE), onContainer = Color(0xFF04301A),
    ),
    AccentColor.COBALTO to AccentPalette(
        light = Color(0xFF84B6F7), deep = Color(0xFF2C63C8),
        onAccent = Color(0xFFFFFFFF), container = Color(0xFFD6E5FD), onContainer = Color(0xFF001C46),
    ),
    AccentColor.LILA to AccentPalette(
        light = Color(0xFFC2A6F2), deep = Color(0xFF7343CE),
        onAccent = Color(0xFFFFFFFF), container = Color(0xFFE9DDFB), onContainer = Color(0xFF23005C),
    ),
    AccentColor.CORAL to AccentPalette(
        light = Color(0xFFF79BA8), deep = Color(0xFFD33F5B),
        onAccent = Color(0xFFFFFFFF), container = Color(0xFFFCDCE1), onContainer = Color(0xFF430011),
    ),
    AccentColor.TURQUESA to AccentPalette(
        light = Color(0xFF8CD9D3), deep = Color(0xFF1E9A93),
        onAccent = Color(0xFF00312E), container = Color(0xFFCFEFEC), onContainer = Color(0xFF00312E),
    ),
    AccentColor.ORO to AccentPalette(
        light = Color(0xFFF3CE7A), deep = Color(0xFFC08A12),
        onAccent = Color(0xFF3A2A00), container = Color(0xFFFAECC9), onContainer = Color(0xFF3A2A00),
    ),
    AccentColor.CHICLE to AccentPalette(
        light = Color(0xFFF5A3D6), deep = Color(0xFFC02E9B),
        onAccent = Color(0xFFFFFFFF), container = Color(0xFFFBDDF0), onContainer = Color(0xFF43002F),
    ),
    AccentColor.GRAFITO to AccentPalette(
        light = Color(0xFF8C9196), deep = Color(0xFF333333),
        onAccent = Color(0xFFFFFFFF), container = Color(0xFFE1E3E6), onContainer = Color(0xFF1A1A1A),
    ),
)

fun AccentColor.palette(): AccentPalette = accentPalettes.getValue(this)

/**
 * A CSS-style linear gradient as a Compose brush.
 *
 * CSS measures the angle clockwise from "up" and Compose wants two offsets, so
 * the angle is rotated a quarter turn and projected onto the unit circle. The
 * result is size-agnostic: [Brush.linearGradient] with fractional offsets
 * stretches to whatever it fills, which is what every caller here wants.
 */
internal fun accentBrush(light: Color, deep: Color, degrees: Float): Brush {
    val radians = Math.toRadians((degrees - 90f).toDouble())
    val dx = kotlin.math.cos(radians).toFloat()
    val dy = kotlin.math.sin(radians).toFloat()
    // Span the full box: start and end sit on opposite corners of the sweep.
    return Brush.linearGradient(
        colors = listOf(light, deep),
        start = androidx.compose.ui.geometry.Offset(0.5f - dx * 0.5f, 0.5f - dy * 0.5f) * GradientSpan,
        end = androidx.compose.ui.geometry.Offset(0.5f + dx * 0.5f, 0.5f + dy * 0.5f) * GradientSpan,
    )
}

/**
 * Brush offsets are absolute pixels, but the fills these brushes go into range
 * from a 30dp swatch to a full-bleed hero. A span far larger than any of them
 * would flatten the ramp; one far smaller would repeat it. This is a middle
 * value that reads as a smooth ramp across the sizes the shell actually uses -
 * callers needing an exact fit pass their own brush.
 */
private const val GradientSpan = 600f
