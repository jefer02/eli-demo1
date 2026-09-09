package com.elyndra.app.domain.model

/** An installed app with a launcher entry point, offered in the "Add Android app" picker. */
data class InstalledAndroidApp(
    val packageName: String,
    val label: String,
)
