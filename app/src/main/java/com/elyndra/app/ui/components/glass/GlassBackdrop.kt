package com.elyndra.app.ui.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * The flat ground every glass surface in the app sits on.
 *
 * This used to paint three large blurred color fields (violet/teal/coral) as
 * ambient light. In practice [androidx.compose.ui.draw.blur] clips to the
 * layer bounds, so the blobs read as hard-edged rectangles instead of soft
 * light - so the color is gone and the glass surfaces carry their own
 * highlights (see LiquidGlassSurface.kt).
 *
 * Still mounted once near the root (see ElyndraNavHost) behind a transparent
 * Scaffold, so screens keep a consistent ground across navigation.
 */
@Composable
fun GlassBackdrop(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
}
