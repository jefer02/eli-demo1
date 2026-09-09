package com.elyndra.app.data.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.elyndra.app.data.playtime.PlaytimeTracker
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.InstalledEmulatorApp
import com.elyndra.app.domain.model.LaunchOutcome
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.repository.GameLauncherRepository
import com.elyndra.app.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Builds and fires the [Intent] that hands a ROM off to whatever emulator is
 * configured for its platform, and kicks off playtime tracking once it does.
 * See the emulator-launching write-up in the project README for the exact
 * contract this relies on (why content:// Uris, why explicit component, etc).
 */
class EmulatorLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playtimeTracker: PlaytimeTracker,
) : GameLauncherRepository {

    override fun isEmulatorInstalled(packageName: String): Boolean =
        runCatching { context.packageManager.getApplicationInfo(packageName, 0) }.isSuccess

    override fun launch(game: Game, platform: Platform): LaunchOutcome {
        if (game.isNativeApp) return launchNativeApp(game)

        val packageName = platform.emulatorPackageName?.takeIf { it.isNotBlank() }
            ?: return LaunchOutcome.EmulatorNotConfigured
        if (!isEmulatorInstalled(packageName)) return LaunchOutcome.EmulatorNotInstalled(packageName)

        return try {
            val romUri = Uri.parse(game.romUri)
            val intent = Intent(platform.emulatorAction).apply {
                setDataAndType(romUri, MIME_TYPE_ROM)
                if (!platform.emulatorActivityName.isNullOrBlank()) {
                    setClassName(packageName, platform.emulatorActivityName)
                } else {
                    setPackage(packageName)
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            playtimeTracker.onGameLaunched(game.id)
            LaunchOutcome.Launched
        } catch (e: ActivityNotFoundException) {
            LaunchOutcome.Failed(e.message ?: "No matching activity for this emulator")
        } catch (e: SecurityException) {
            LaunchOutcome.Failed(e.message ?: "Permission denied while launching the emulator")
        }
    }

    /** For native apps [Game.romUri] holds a bare package name, not a ROM Uri. */
    private fun launchNativeApp(game: Game): LaunchOutcome {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(game.romUri)
            ?: return LaunchOutcome.Failed("${game.romUri} is not installed or has no launcher activity")

        return try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            playtimeTracker.onGameLaunched(game.id)
            LaunchOutcome.Launched
        } catch (e: ActivityNotFoundException) {
            LaunchOutcome.Failed(e.message ?: "No launcher activity for ${game.romUri}")
        } catch (e: SecurityException) {
            LaunchOutcome.Failed(e.message ?: "Permission denied while launching ${game.romUri}")
        }
    }

    override fun getInstalledCandidateEmulators(): List<InstalledEmulatorApp> {
        val packageManager = context.packageManager
        return Constants.KNOWN_EMULATOR_PACKAGES.mapNotNull { packageName ->
            val appInfo = runCatching { packageManager.getApplicationInfo(packageName, 0) }.getOrNull()
                ?: return@mapNotNull null
            InstalledEmulatorApp(
                packageName = packageName,
                label = packageManager.getApplicationLabel(appInfo).toString(),
            )
        }
    }

    private companion object {
        // Deliberately generic: we target the emulator by explicit package/component,
        // not by mime-based resolution, so this only needs to be a plausible value.
        const val MIME_TYPE_ROM = "application/octet-stream"
    }
}
