package com.elyndra.app.domain.usecase.playtime

import com.elyndra.app.domain.model.PlaySession
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.PlaySessionRepository
import javax.inject.Inject

/** Persists a finished session (see [com.elyndra.app.data.playtime.PlaytimeTracker])
 *  both as its own row and as an increment on the game's running total. */
class RecordPlaySessionUseCase @Inject constructor(
    private val playSessionRepository: PlaySessionRepository,
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(gameId: Long, startedAt: Long, endedAt: Long, durationSeconds: Long) {
        playSessionRepository.addSession(
            PlaySession(gameId = gameId, startedAt = startedAt, endedAt = endedAt, durationSeconds = durationSeconds),
        )
        gameRepository.applyPlaySession(gameId, durationSeconds, endedAt)
    }
}
