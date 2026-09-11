package com.elyndra.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elyndra.app.domain.model.AccentColor
import com.elyndra.app.domain.model.GlassTint
import com.elyndra.app.domain.model.UserPreferences

/** The color each [GlassTint] washes its panels with, before opacity. */
fun GlassTint.color(): Color = when (this) {
    GlassTint.PAPEL -> Color(0xFFFFFFFF)
    GlassTint.ARENA -> Color(0xFFF59659)
    GlassTint.AMBAR -> Color(0xFFEE7E28)
    GlassTint.COBRE -> Color(0xFFE26D19)
    GlassTint.MENTA -> Color(0xFF9BD494)
    GlassTint.CIELO -> Color(0xFF84B6F7)
    GlassTint.LILA -> Color(0xFFC2A6F2)
    GlassTint.HUMO -> Color(0xFF555555)
}

/**
 * Everything the user can tune about the glass, resolved once at the top of the
 * tree so no panel has to reach for preferences itself.
 *
 * Unlike the old fixed ultraThin/thin/regular vocabulary, thickness here is a
 * *user* setting: Settings > Liquid Glass drives [blur] and [opacity] directly,
 * and every panel in the app is the same thickness by design.
 */
@Immutable
data class GlassSettings(
    val tint: Color,
    val opacity: Float,
    val blur: Dp,
    /** How hard the hero scrim darkens artwork under a title, 0f..1f. */
    val heroScrim: Float,
    val accent: AccentPalette,
) {
    /** The tint at the user's chosen opacity - the actual fill of a glass panel. */
    val fill: Color get() = tint.copy(alpha = opacity)

    /** The specular top edge that sells the material. Always white, never tinted. */
    val border: Color get() = Color.White.copy(alpha = 0.72f)

    companion object {
        fun from(prefs: UserPreferences): GlassSettings = GlassSettings(
            tint = prefs.glassTint.color(),
            opacity = prefs.glassOpacity / 100f,
            blur = prefs.glassBlur.dp,
            heroScrim = prefs.heroScrim / 100f,
            accent = prefs.accentColor.palette(),
        )
    }
}

/**
 * Reachable from any composable so a panel deep in a screen can render itself
 * without threading four settings through every call site. Static because the
 * whole tree recomposes anyway when the user drags a slider.
 */
val LocalGlass = staticCompositionLocalOf {
    GlassSettings.from(UserPreferences()).copy(accent = AccentColor.MANDARINA.palette())
}

object GlassMetrics {
    val cardRadius = 16.dp
    val panelRadius = 18.dp
    val tightPanelRadius = 16.dp
    val chipRadius = 12.dp
    val buttonRadius = 15.dp
    val borderWidth = 1.dp
}

/** How far a card lifts and glows once pressed/focused - spring-driven, see PressableGlass.kt. */
internal const val GLASS_PRESS_SCALE = 1.045f
internal val GLASS_PRESS_ELEVATION: Dp = 18.dp

/**
 * The old fixed-thickness vocabulary.
 *
 * Superseded by [GlassSettings], where thickness is a user setting rather than
 * a per-component choice. Kept only so screens that have not been moved to the
 * new shell yet still compile; delete it once nothing references
 * [GlassMaterials].
 */
@Deprecated("Read LocalGlass instead - thickness is a user setting now.")
data class GlassStyle(
    val tintAlpha: Float,
    val borderAlpha: Float,
    val highlightAlpha: Float,
    val shadowAlpha: Float,
)

@Suppress("DEPRECATION")
@Deprecated("Read LocalGlass instead - thickness is a user setting now.")
object GlassMaterials {
    val ultraThin = GlassStyle(tintAlpha = 0.05f, borderAlpha = 0.28f, highlightAlpha = 0.22f, shadowAlpha = 0.10f)
    val thin = GlassStyle(tintAlpha = 0.09f, borderAlpha = 0.34f, highlightAlpha = 0.28f, shadowAlpha = 0.14f)
    val regular = GlassStyle(tintAlpha = 0.14f, borderAlpha = 0.40f, highlightAlpha = 0.34f, shadowAlpha = 0.18f)
    val thick = GlassStyle(tintAlpha = 0.22f, borderAlpha = 0.46f, highlightAlpha = 0.40f, shadowAlpha = 0.24f)
}
