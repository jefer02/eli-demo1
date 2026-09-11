package com.elyndra.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * A click with no ripple.
 *
 * The shell answers a press by lifting and re-lighting the element itself -
 * a Material ripple spreading under a translucent glass panel reads as a
 * smudge on the glass rather than as feedback, so it is suppressed everywhere
 * in the new chrome.
 */
fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClick = onClick,
    )
}
