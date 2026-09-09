package com.elyndra.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.elyndra.app.data.local.entity.PlaySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaySessionDao {

    @Insert
    suspend fun insert(session: PlaySessionEntity): Long

    @Query("SELECT * FROM play_sessions WHERE gameId = :gameId ORDER BY startedAt DESC")
    fun observeSessionsForGame(gameId: Long): Flow<List<PlaySessionEntity>>
}
