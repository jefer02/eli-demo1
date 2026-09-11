package com.elyndra.app.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * The shell's fixed palette. Only these four tones are accent-independent:
 * everything else in the UI is painted from the chosen AccentPalette, so
 * swapping an accent never has to touch a surface color.
 */

/** The canvas the whole app sits on - a barely-warm off-white. */
val ElyndraPaper = Color(0xFFF6F8F9)

/** Primary type and icons. Deliberately not pure black; the shell has no true black. */
val ElyndraInk = Color(0xFF333333)

/** Secondary type: labels, metadata, anything that must recede from [ElyndraInk]. */
val ElyndraInkMuted = Color(0xFF555555)

/** The one non-accent status color: "connected", "done", healthy. */
val ElyndraGreen = Color(0xFF9BD494)

val ElyndraErrorRed = Color(0xFFBA1A1A)
val ElyndraOnErrorRed = Color(0xFFFFFFFF)
val ElyndraErrorContainer = Color(0xFFFFDAD6)
val ElyndraOnErrorContainer = Color(0xFF410002)

/**
 * Surfaces derived from [ElyndraPaper]. The shell layers glass over artwork
 * rather than stacking opaque Material surfaces, so these stay close together -
 * they are the fallback for stock M3 components, not the main visual language.
 */
val ElyndraSurface = Color(0xFFFFFFFF)
val ElyndraSurfaceVariant = Color(0xFFE7EAEC)
val ElyndraOutline = Color(0x24333333)

/**
 * The cover-art gradient pairs. A game with no scraped artwork still needs a
 * tile, and a flat gray one makes a full rail look broken - so each title is
 * hashed onto one of these pairs and keeps it for good.
 */
val ArtworkPairs: List<Pair<Color, Color>> = listOf(
    Color(0xFFF59659) to Color(0xFFE26D19),
    Color(0xFFEE7E28) to Color(0xFF333333),
    Color(0xFF9BD494) to Color(0xFFEE7E28),
    Color(0xFF84B6F7) to Color(0xFF2C63C8),
    Color(0xFFC2A6F2) to Color(0xFF7343CE),
    Color(0xFFF79BA8) to Color(0xFFD33F5B),
    Color(0xFF8CD9D3) to Color(0xFF1E9A93),
    Color(0xFFF3CE7A) to Color(0xFFC08A12),
    Color(0xFF9BD494) to Color(0xFF333333),
    Color(0xFFF5A3D6) to Color(0xFFC02E9B),
    Color(0xFF555555) to Color(0xFF333333),
    Color(0xFFF59659) to Color(0xFF9BD494),
)

/**
 * Picks a stable artwork pair for [key]. `hashCode` would work but is not
 * specified to be stable across JVM versions; this is, so a game's tile color
 * survives an app update.
 */
fun artworkPairFor(key: String): Pair<Color, Color> {
    var h = 0
    for (ch in key) h = (h * 31 + ch.code) and 0x7FFFFFFF
    return ArtworkPairs[h % ArtworkPairs.size]
}
