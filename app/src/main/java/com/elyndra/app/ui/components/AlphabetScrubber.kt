package com.elyndra.app.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp

private val LETTERS = ('A'..'Z').toList()

/** Vertical fast-scroll rail - drag or tap a letter to jump the list there. */
@Composable
fun AlphabetScrubber(
    availableLetters: Set<Char>,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    var heightPx by remember { mutableIntStateOf(1) }

    fun letterForY(y: Float): Char {
        val index = ((y / heightPx) * LETTERS.size).toInt().coerceIn(0, LETTERS.lastIndex)
        return LETTERS[index]
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(28.dp)
            .fillMaxHeight()
            .onSizeChanged { heightPx = it.height.coerceAtLeast(1) }
            .pointerInput(Unit) {
                detectTapGestures { offset -> onLetterSelected(letterForY(offset.y)) }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ -> onLetterSelected(letterForY(change.position.y)) }
            },
    ) {
        LETTERS.forEach { letter ->
            val available = letter in availableLetters
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (available) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                },
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}
