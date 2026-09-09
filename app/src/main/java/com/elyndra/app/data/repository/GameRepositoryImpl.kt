package com.elyndra.app.data.repository

import com.elyndra.app.data.local.dao.GameDao
import com.elyndra.app.data.mapper.toDomain
import com.elyndra.app.data.mapper.toEntity
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
) : GameRepository {

    override fun observeGames(includeHidden: Boolean): Flow<List<Game>> =
        gameDao.observeGames(includeHidden).map { entities -> entities.map { it.toDomain() } }

    override fun observeGame(gameId: Long): Flow<Game?> =
        gameDao.observeGame(gameId).map { it?.toDomain() }

    override suspend fun getGame(gameId: Long): Game? = gameDao.getGame(gameId)?.toDomain()

    override suspend fun findByRomUri(romUri: String): Game? = gameDao.findByRomUri(romUri)?.toDomain()

    override suspend fun upsertGame(game: Game): Long = gameDao.upsert(game.toEntity())

    override suspend fun upsertGames(games: List<Game>): List<Long> =
        gameDao.upsertAll(games.map { it.toEntity() })

    override suspend fun deleteGame(gameId: Long) = gameDao.deleteById(gameId)

    override suspend fun setFavorite(gameId: Long, isFavorite: Boolean) =
        gameDao.setFavorite(gameId, isFavorite)

    override suspend fun setHidden(gameId: Long, isHidden: Boolean) =
        gameDao.setHidden(gameId, isHidden)

    override suspend fun applyPlaySession(gameId: Long, additionalSeconds: Long, playedAt: Long) =
        gameDao.applyPlaySession(gameId, additionalSeconds, playedAt)
}
