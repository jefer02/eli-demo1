package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.PlaySession
import kotlinx.coroutines.flow.Flow

interface PlaySessionRepository {
    suspend fun addSession(session: PlaySession): Long
    fun observeSessionsForGame(gameId: Long): Flow<List<PlaySession>>
}
