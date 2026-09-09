package com.elyndra.app.data.repository

import com.elyndra.app.data.local.dao.PlaySessionDao
import com.elyndra.app.data.mapper.toDomain
import com.elyndra.app.data.mapper.toEntity
import com.elyndra.app.domain.model.PlaySession
import com.elyndra.app.domain.repository.PlaySessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaySessionRepositoryImpl @Inject constructor(
    private val playSessionDao: PlaySessionDao,
) : PlaySessionRepository {

    override suspend fun addSession(session: PlaySession): Long =
        playSessionDao.insert(session.toEntity())

    override fun observeSessionsForGame(gameId: Long): Flow<List<PlaySession>> =
        playSessionDao.observeSessionsForGame(gameId).map { entities -> entities.map { it.toDomain() } }
}
