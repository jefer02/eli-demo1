package com.elyndra.app.domain.usecase.scraper

import com.elyndra.app.di.IoDispatcher
import com.elyndra.app.domain.model.ScrapeBatchResult
import com.elyndra.app.domain.model.ScrapeOutcome
import com.elyndra.app.domain.repository.GameRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/** Rescrapes many games at once, emitting a running tally after each one. */
class ScrapeLibraryUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val scrapeGameUseCase: ScrapeGameUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    /** Rescrapes [gameIds], or the entire library (including hidden games) when null. */
    operator fun invoke(gameIds: List<Long>? = null): Flow<ScrapeBatchResult> = flow {
        val targets = gameIds ?: gameRepository.observeGames(includeHidden = true).first().map { it.id }
        var succeeded = 0
        var notFound = 0
        var failed = 0

        for (id in targets) {
            when (scrapeGameUseCase(id)) {
                is ScrapeOutcome.Success -> succeeded++
                is ScrapeOutcome.NotFound -> notFound++
                is ScrapeOutcome.MissingCredentials, is ScrapeOutcome.Failed -> failed++
            }
            emit(ScrapeBatchResult(targets.size, succeeded, notFound, failed))
        }
    }.flowOn(ioDispatcher)
}
