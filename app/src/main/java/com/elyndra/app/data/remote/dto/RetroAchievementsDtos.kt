package com.elyndra.app.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Best-effort field mapping for API_GetGame.php - RetroAchievements' API is
 * achievement-first, not scraper-first, so this is intentionally minimal.
 * Verify field names against the live response if achievement tracking is
 * built out further; this is currently only used for a manual game lookup.
 */
@Serializable
data class RetroAchievementsGameDto(
    val Title: String? = null,
    val ImageIcon: String? = null,
    val ImageTitle: String? = null,
    val ImageBoxArt: String? = null,
    val Developer: String? = null,
    val Publisher: String? = null,
    val Genre: String? = null,
    val Released: String? = null,
)
