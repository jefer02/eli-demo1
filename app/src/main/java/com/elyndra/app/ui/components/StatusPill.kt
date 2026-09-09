package com.elyndra.app.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.BatteryManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elyndra.app.R
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.theme.GlassMaterials
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

private const val CLOCK_TICK_MS = 20_000L
private val LowBatteryRed = Color(0xFFFF6B6B)

/** Matches the home shell's chrome: the pill floats over artwork, not over a themed surface. */
private val PillTint = Color(0xFF151327)
private val BatteryGreen = Color(0xFF7BE495)

/**
 * The console-style indicator pill: controller, network, battery and clock.
 *
 * Every value here is real - the clock ticks, the battery reflects the device
 * and Wi-Fi reflects the active network - because a shell that fakes its own
 * status bar is worse than one that has none.
 */
@Composable
fun StatusPill(modifier: Modifier = Modifier) {
    val batteryLevel = rememberBatteryLevel()
    val isOnline = rememberWifiConnected()
    val time = rememberFormattedTime()

    LiquidGlassSurface(
        shape = CircleShape,
        style = GlassMaterials.thick,
        tint = PillTint,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.SportsEsports,
                contentDescription = stringResource(R.string.status_controller),
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(18.dp),
            )
            Icon(
                imageVector = if (isOnline) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                contentDescription = stringResource(
                    if (isOnline) R.string.status_wifi_on else R.string.status_wifi_off,
                ),
                tint = Color.White.copy(alpha = if (isOnline) 0.85f else 0.4f),
                modifier = Modifier.size(18.dp),
            )
            BatteryGauge(level = batteryLevel)
            Text(
                text = time,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/**
 * Drawn rather than picked from the icon set: the stock battery icons come in
 * fixed steps, and a gauge that fills continuously reads the actual level.
 */
@Composable
private fun BatteryGauge(level: Int) {
    val fillColor = if (level <= 15) LowBatteryRed else BatteryGreen
    val description = stringResource(R.string.status_battery, level)

    Canvas(
        modifier = Modifier
            .width(26.dp)
            .height(14.dp)
            .semantics { contentDescription = description },
    ) {
        val nubWidth = size.width * 0.09f
        val bodyWidth = size.width - nubWidth - 1.dp.toPx()
        val radius = CornerRadius(size.height * 0.32f, size.height * 0.32f)

        drawRoundRect(
            color = Color.White.copy(alpha = 0.75f),
            size = Size(bodyWidth, size.height),
            cornerRadius = radius,
            style = Stroke(width = 1.4.dp.toPx()),
        )
        // Positive nub on the right, same as a physical cell.
        drawRoundRect(
            color = Color.White.copy(alpha = 0.75f),
            topLeft = Offset(bodyWidth + 1.dp.toPx(), size.height * 0.3f),
            size = Size(nubWidth, size.height * 0.4f),
            cornerRadius = CornerRadius(nubWidth / 2, nubWidth / 2),
        )
        val inset = 3.dp.toPx()
        val fillWidth = ((bodyWidth - inset * 2) * (level / 100f)).coerceAtLeast(0f)
        if (fillWidth > 0f) {
            drawRoundRect(
                color = fillColor,
                topLeft = Offset(inset, inset),
                size = Size(fillWidth, size.height - inset * 2),
                cornerRadius = CornerRadius(size.height * 0.16f, size.height * 0.16f),
            )
        }
    }
}

/** Battery percentage, kept current from the sticky ACTION_BATTERY_CHANGED broadcast. */
@Composable
private fun rememberBatteryLevel(): Int {
    val context = LocalContext.current
    var level by remember { mutableIntStateOf(100) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context?, intent: Intent?) {
                val raw = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if (raw >= 0 && scale > 0) level = (raw * 100 / scale).coerceIn(0, 100)
            }
        }
        // Registering a sticky broadcast returns the last value immediately, so
        // there's no "100%" flash before the first real update arrives.
        val sticky = context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val raw = sticky?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = sticky?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (raw >= 0 && scale > 0) level = (raw * 100 / scale).coerceIn(0, 100)

        onDispose { runCatching { context.unregisterReceiver(receiver) } }
    }
    return level
}

@Composable
private fun rememberWifiConnected(): Boolean {
    val context = LocalContext.current
    var connected by remember { mutableStateOf(true) }

    DisposableEffect(context) {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        fun refresh() {
            val capabilities = manager?.getNetworkCapabilities(manager.activeNetwork)
            connected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = refresh()
            override fun onLost(network: Network) = refresh()
        }
        refresh()
        runCatching { manager?.registerDefaultNetworkCallback(callback) }

        onDispose { runCatching { manager?.unregisterNetworkCallback(callback) } }
    }
    return connected
}

/** Wall clock in the locale's short format, refreshed every 20s. */
@Composable
private fun rememberFormattedTime(): String {
    val locale = LocalConfiguration.current.locales[0]
    val formatter = remember(locale) { DateFormat.getTimeInstance(DateFormat.SHORT, locale) }
    var now by remember { mutableStateOf(formatter.format(Date())) }

    LaunchedEffect(formatter) {
        while (true) {
            now = formatter.format(Date())
            delay(CLOCK_TICK_MS)
        }
    }
    return now
}