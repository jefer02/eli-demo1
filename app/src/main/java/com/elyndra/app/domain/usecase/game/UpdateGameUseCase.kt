package com.elyndra.app.domain.usecase.game

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.repository.GameRepository
import javax.inject.Inject

class UpdateGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(game: Game) {
        gameRepository.upsertGame(game)
    }
}
