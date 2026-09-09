package com.elyndra.app.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.elyndra.app.R
import java.text.DateFormat
import java.util.Date
import kotlin.math.roundToInt

/**
 * These read strings and the active locale, so they're composables rather than
 * plain helpers - that way they follow the language picked in Settings instead
 * of the device's, same as every other piece of text in the UI.
 */
@Composable
@ReadOnlyComposable
fun formatPlaytime(totalSeconds: Long): String {
    if (totalSeconds <= 0) return stringResource(R.string.playtime_never)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return when {
        hours > 0 -> stringResource(R.string.playtime_hours_minutes, hours, minutes)
        minutes > 0 -> stringResource(R.string.playtime_minutes, minutes)
        else -> stringResource(R.string.playtime_under_minute)
    }
}

@Composable
@ReadOnlyComposable
fun formatLastPlayed(lastPlayedAtMillis: Long?): String {
    if (lastPlayedAtMillis == null) return stringResource(R.string.playtime_never)
    val locale = LocalConfiguration.current.locales[0]
    return DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(Date(lastPlayedAtMillis))
}

/** Byte units are the same in every language, so this one stays a plain function. */
fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = listOf("B", "KB", "MB", "GB")
    var value = bytes.toDouble()
    var unitIndex = 0
    while (value >= 1024 && unitIndex < units.lastIndex) {
        value /= 1024
        unitIndex++
    }
    val rounded = (value * 10).roundToInt() / 10.0
    return "$rounded ${units[unitIndex]}"
}
