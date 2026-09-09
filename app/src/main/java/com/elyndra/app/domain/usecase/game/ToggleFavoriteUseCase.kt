package com.elyndra.app.domain.usecase.game

import com.elyndra.app.domain.repository.GameRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(gameId: Long, isFavorite: Boolean) {
        gameRepository.setFavorite(gameId, isFavorite)
    }
}
