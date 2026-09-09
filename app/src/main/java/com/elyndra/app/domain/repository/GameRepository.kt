package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun observeGames(includeHidden: Boolean = false): Flow<List<Game>>
    fun observeGame(gameId: Long): Flow<Game?>
    suspend fun getGame(gameId: Long): Game?
    suspend fun findByRomUri(romUri: String): Game?
    suspend fun upsertGame(game: Game): Long
    suspend fun upsertGames(games: List<Game>): List<Long>
    suspend fun deleteGame(gameId: Long)
    suspend fun setFavorite(gameId: Long, isFavorite: Boolean)
    suspend fun setHidden(gameId: Long, isHidden: Boolean)
    suspend fun applyPlaySession(gameId: Long, additionalSeconds: Long, playedAt: Long)
}
