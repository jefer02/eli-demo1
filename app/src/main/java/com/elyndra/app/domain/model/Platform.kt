package com.elyndra.app.domain.model

/**
 * A console/system (e.g. SNES, PS1). Doubles as the per-platform emulator
 * configuration since Elyndra only ever needs one emulator per platform.
 */
data class Platform(
    val id: String,
    val displayName: String,
    val shortName: String,
    val extensions: List<String>,
    val folderAliases: List<String>,
    val emulatorPackageName: String? = null,
    val emulatorActivityName: String? = null,
    val emulatorAction: String = "android.intent.action.VIEW",
    val sortOrder: Int = 0,
) {
    val isEmulatorConfigured: Boolean get() = !emulatorPackageName.isNullOrBlank()
}
