package com.elyndra.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.components.glass.glassPress
import com.elyndra.app.ui.components.glass.rememberGlassPressState
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics

/** Floating liquid-glass dock standing in for Material3's NavigationBar. */
@Composable
fun GlassBottomNav(
    items: List<BottomNavItem>,
    isSelected: (Any) -> Boolean,
    onItemClick: (Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiquidGlassSurface(
        shape = RoundedCornerShape(GlassMetrics.panelRadius),
        style = GlassMaterials.regular,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEach { item ->
                GlassNavItem(
                    item = item,
                    selected = isSelected(item.route),
                    onClick = { onItemClick(item.route) },
                )
            }
        }
    }
}

@Composable
private fun GlassNavItem(item: BottomNavItem, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressState = rememberGlassPressState(interactionSource)
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "navItemColor",
    )
    val pillColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.primary.copy(alpha = 0f),
        label = "navItemPill",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .glassPress(pressState)
            .clip(RoundedCornerShape(20.dp))
            .background(pillColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        val label = stringResource(item.label)
        Icon(item.icon, contentDescription = label, tint = contentColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = contentColor)
    }
}
