package com.elyndra.app.util

import com.elyndra.app.domain.model.Platform

/**
 * Built-in catalog of supported consoles, used to seed the `platforms` table on
 * first run and to drive [com.elyndra.app.domain.usecase.scan.PlatformDetector].
 *
 * Extensions like iso/bin/cue/chd/zip are shared by several platforms on purpose -
 * PlatformDetector resolves those via folder-name hints rather than extension alone.
 */
object PlatformCatalog {

    val all: List<Platform> = listOf(
        platform("nes", "Nintendo Entertainment System", "NES", listOf("nes"), listOf("nes", "nintendo"), 10),
        platform("snes", "Super Nintendo", "SNES", listOf("sfc", "smc"), listOf("snes", "sfc", "superfamicom"), 20),
        platform("n64", "Nintendo 64", "N64", listOf("n64", "z64", "v64"), listOf("n64", "nintendo64"), 30),
        platform("gb", "Game Boy", "GB", listOf("gb"), listOf("gb", "gameboy"), 40),
        platform("gbc", "Game Boy Color", "GBC", listOf("gbc"), listOf("gbc"), 50),
        platform("gba", "Game Boy Advance", "GBA", listOf("gba"), listOf("gba"), 60),
        platform("nds", "Nintendo DS", "NDS", listOf("nds"), listOf("nds", "ds"), 70),
        platform("n3ds", "Nintendo 3DS", "3DS", listOf("3ds", "cia"), listOf("3ds"), 80),
        platform("gamecube", "GameCube", "GC", listOf("gcm", "rvz"), listOf("gamecube", "gc", "ngc"), 90),
        platform("wii", "Wii", "Wii", listOf("wbfs", "rvz"), listOf("wii"), 100),
        platform("switch", "Nintendo Switch", "Switch", listOf("xci", "nsp"), listOf("switch", "nx"), 110),
        platform("psx", "PlayStation", "PS1", listOf("pbp"), listOf("psx", "ps1", "playstation"), 120),
        platform("ps2", "PlayStation 2", "PS2", emptyList(), listOf("ps2"), 130),
        platform("psp", "PlayStation Portable", "PSP", listOf("cso"), listOf("psp"), 140),
        platform("vita", "PlayStation Vita", "Vita", listOf("vpk"), listOf("vita", "psvita"), 150),
        platform("genesis", "Sega Genesis", "Genesis", listOf("md", "gen", "smd"), listOf("genesis", "megadrive", "mega-drive"), 160),
        platform("mastersystem", "Sega Master System", "SMS", listOf("sms"), listOf("mastersystem", "sms"), 170),
        platform("gamegear", "Sega Game Gear", "GG", listOf("gg"), listOf("gamegear"), 180),
        platform("saturn", "Sega Saturn", "Saturn", emptyList(), listOf("saturn"), 190),
        platform("dreamcast", "Sega Dreamcast", "DC", listOf("gdi", "cdi"), listOf("dreamcast", "dc"), 200),
        platform("arcade", "Arcade", "MAME", emptyList(), listOf("arcade", "mame"), 210),
        // No extensions/aliases on purpose: never matched by folder scanning,
        // only populated through the "Add Android app" picker.
        platform(Constants.ANDROID_PLATFORM_ID, "Android Games", "Android", emptyList(), emptyList(), 900),
    )

    /** extension (no dot, lowercase) -> platform ids that use it. May have >1 entry. */
    val extensionToPlatformIds: Map<String, List<String>> = buildMap<String, MutableList<String>> {
        for (p in all) for (ext in p.extensions) getOrPut(ext) { mutableListOf() }.add(p.id)
        // Ambiguous, multi-platform disc-image extensions - resolved via folder hints only.
        val shared = listOf("iso", "bin", "cue", "chd", "zip", "img")
        for (ext in shared) {
            val owners = when (ext) {
                "iso" -> listOf("gamecube", "wii", "psp", "ps2")
                "bin", "cue", "img" -> listOf("psx", "saturn")
                "chd" -> listOf("psx", "ps2", "saturn", "dreamcast", "arcade")
                "zip" -> listOf("arcade")
                else -> emptyList()
            }
            getOrPut(ext) { mutableListOf() }.addAll(owners)
        }
    }

    /** normalized folder name -> platform id. Unambiguous by construction. */
    val folderAliasToPlatformId: Map<String, String> = buildMap {
        for (p in all) for (alias in p.folderAliases) put(alias.lowercase(), p.id)
    }

    fun byId(id: String): Platform? = all.find { it.id == id }

    private fun platform(
        id: String,
        name: String,
        short: String,
        extensions: List<String>,
        aliases: List<String>,
        order: Int,
    ) = Platform(
        id = id,
        displayName = name,
        shortName = short,
        extensions = extensions,
        folderAliases = aliases,
        sortOrder = order,
    )
}
