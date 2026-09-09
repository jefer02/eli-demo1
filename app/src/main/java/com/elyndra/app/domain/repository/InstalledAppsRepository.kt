package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.InstalledAndroidApp

interface InstalledAppsRepository {
    /** Every installed app with a launcher entry point, excluding Elyndra itself. */
    fun getLaunchableApps(): List<InstalledAndroidApp>
}
