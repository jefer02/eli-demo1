package com.elyndra.app.domain.usecase.game

import com.elyndra.app.domain.repository.GameRepository
import javax.inject.Inject

/** Removes the game from the library only. The ROM file on disk is untouched. */
class DeleteGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(gameId: Long) {
        gameRepository.deleteGame(gameId)
    }
}
