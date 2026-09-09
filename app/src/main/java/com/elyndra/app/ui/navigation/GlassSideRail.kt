package com.elyndra.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elyndra.app.R
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.components.glass.glassPress
import com.elyndra.app.ui.components.glass.rememberGlassPressState
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics

/**
 * Persistent floating glass rail standing in for a bottom nav bar - Elyndra is
 * landscape-only (handheld/console-style, same as the reference this mirrors),
 * so the primary nav lives down the left edge instead of across the bottom.
 */
@Composable
fun GlassSideRail(
    items: List<BottomNavItem>,
    isSelected: (Any) -> Boolean,
    onItemClick: (Any) -> Unit,
    onSettingsClick: () -> Unit,
    settingsSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    LiquidGlassSurface(
        shape = RoundedCornerShape(GlassMetrics.panelRadius),
        style = GlassMaterials.regular,
        modifier = modifier
            .fillMaxHeight()
            .width(216.dp)
            .padding(vertical = 20.dp, horizontal = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 20.dp, horizontal = 8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp, bottom = 28.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.VideogameAsset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp),
                )
                Text(
                    text = "Elyndra",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }

            items.forEach { item ->
                GlassRailItem(
                    item = item,
                    selected = isSelected(item.route),
                    onClick = { onItemClick(item.route) },
                )
                Spacer(Modifier.padding(top = 6.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // The gear sits apart from the browse destinations, pinned to the
            // bottom - it's the way into Settings from anywhere in the shell.
            SettingsGearButton(selected = settingsSelected, onClick = onSettingsClick)
        }
    }
}

/** Gear pill: the shell-wide entry point into Settings. */
@Composable
private fun SettingsGearButton(selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressState = rememberGlassPressState(interactionSource)
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "gearColor",
    )
    val pillColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary.copy(alpha = if (selected) 0.18f else 0f),
        label = "gearPill",
    )
    val borderColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary.copy(alpha = if (selected) 0.5f else 0.18f),
        label = "gearBorder",
    )
    val label = stringResource(R.string.nav_settings)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .glassPress(pressState)
            .clip(RoundedCornerShape(18.dp))
            .background(pillColor)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Icon(Icons.Filled.Settings, contentDescription = label, tint = contentColor)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun GlassRailItem(item: BottomNavItem, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressState = rememberGlassPressState(interactionSource)
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "railItemColor",
    )
    val pillColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.primary.copy(alpha = 0f),
        label = "railItemPill",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .glassPress(pressState)
            .clip(RoundedCornerShape(18.dp))
            .background(pillColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Icon(item.icon, contentDescription = null, tint = contentColor)
        Text(
            text = stringResource(item.label),
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}
