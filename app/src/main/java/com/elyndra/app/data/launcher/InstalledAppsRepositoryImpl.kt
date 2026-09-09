package com.elyndra.app.data.launcher

import android.content.Context
import android.content.Intent
import com.elyndra.app.domain.model.InstalledAndroidApp
import com.elyndra.app.domain.repository.InstalledAppsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Enumerates launchable apps via `ACTION_MAIN`/`CATEGORY_LAUNCHER` resolution.
 * Requires the matching `<intent>` filter in the manifest's `<queries>` block
 * for visibility on Android 11+ - see AndroidManifest.xml.
 */
class InstalledAppsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : InstalledAppsRepository {

    override fun getLaunchableApps(): List<InstalledAndroidApp> {
        val packageManager = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        @Suppress("DEPRECATION")
        val resolved = packageManager.queryIntentActivities(launcherIntent, 0)

        return resolved.asSequence()
            .map { it.activityInfo.packageName }
            .distinct()
            .filter { it != context.packageName }
            .mapNotNull { packageName ->
                val appInfo = runCatching { packageManager.getApplicationInfo(packageName, 0) }.getOrNull()
                    ?: return@mapNotNull null
                InstalledAndroidApp(
                    packageName = packageName,
                    label = packageManager.getApplicationLabel(appInfo).toString(),
                )
            }
            .sortedBy { it.label.lowercase() }
            .toList()
    }
}
