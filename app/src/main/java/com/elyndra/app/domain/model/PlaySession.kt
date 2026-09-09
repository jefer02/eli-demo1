package com.elyndra.app.domain.model

data class PlaySession(
    val id: Long = 0,
    val gameId: Long,
    val startedAt: Long,
    val endedAt: Long,
    val durationSeconds: Long,
)
