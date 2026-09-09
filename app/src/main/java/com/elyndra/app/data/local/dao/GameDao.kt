package com.elyndra.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.elyndra.app.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE isHidden = 0 OR :includeHidden ORDER BY title COLLATE NOCASE ASC")
    fun observeGames(includeHidden: Boolean): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    fun observeGame(gameId: Long): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGame(gameId: Long): GameEntity?

    @Query("SELECT * FROM games WHERE romUri = :romUri LIMIT 1")
    suspend fun findByRomUri(romUri: String): GameEntity?

    @Upsert
    suspend fun upsert(game: GameEntity): Long

    @Upsert
    suspend fun upsertAll(games: List<GameEntity>): List<Long>

    @Delete
    suspend fun delete(game: GameEntity)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteById(gameId: Long)

    @Query("UPDATE games SET isFavorite = :isFavorite WHERE id = :gameId")
    suspend fun setFavorite(gameId: Long, isFavorite: Boolean)

    @Query("UPDATE games SET isHidden = :isHidden WHERE id = :gameId")
    suspend fun setHidden(gameId: Long, isHidden: Boolean)

    @Query(
        "UPDATE games SET totalPlaytimeSeconds = totalPlaytimeSeconds + :additionalSeconds, " +
            "lastPlayedAt = :playedAt WHERE id = :gameId",
    )
    suspend fun applyPlaySession(gameId: Long, additionalSeconds: Long, playedAt: Long)
}
