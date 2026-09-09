package com.elyndra.app.util

object Constants {
    const val SCREENSCRAPER_BASE_URL = "https://api.screenscraper.fr/api2/"
    const val TWITCH_OAUTH_BASE_URL = "https://id.twitch.tv/"
    const val IGDB_BASE_URL = "https://api.igdb.com/v4/"
    const val STEAMGRIDDB_BASE_URL = "https://www.steamgriddb.com/api/v2/"
    const val RETROACHIEVEMENTS_BASE_URL = "https://retroachievements.org/API/"

    /** All ROM/disc-image extensions Elyndra recognizes, lowercase, no leading dot. */
    val KNOWN_ROM_EXTENSIONS: Set<String> = PlatformCatalog.extensionToPlatformIds.keys

    const val DEFAULT_GRID_COLUMNS = 3
    const val MIN_GRID_COLUMNS = 2
    const val MAX_GRID_COLUMNS = 6

    /** Synthetic [com.elyndra.app.domain.model.Platform] id for installed Android apps added via the picker. */
    const val ANDROID_PLATFORM_ID = "android"

    /**
     * Package names Elyndra knows how to offer in the emulator picker. Must be
     * kept in sync with the `<queries>` block in AndroidManifest.xml - that's
     * what makes these packages (and their labels) visible to PackageManager on
     * Android 11+; anything left off both lists still works via manual entry.
     */
    val KNOWN_EMULATOR_PACKAGES: List<String> = listOf(
        "com.retroarch",
        "com.retroarch.aarch64",
        "com.retroarch.ra32",
        "org.dolphinemu.dolphinemu",
        "com.github.stenzek.duckstation",
        "org.ppsspp.ppsspp",
        "org.ppsspp.ppssppgold",
        "org.citra.citra_emu",
        "io.github.lime3ds.android",
        "me.magnum.melonds",
        "com.aeonlucid.drastic",
        "org.yabause.android",
        "jp.gamba.yabasanshiro",
        "org.mupen64plusae.v3.fzurita",
        "com.aethersx2.android",
        "skyline.emu",
    )
}
