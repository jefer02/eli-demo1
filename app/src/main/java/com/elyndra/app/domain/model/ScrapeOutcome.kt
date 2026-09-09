package com.elyndra.app.domain.model

sealed interface ScrapeOutcome {
    data class Success(val game: Game) : ScrapeOutcome
    data object NotFound : ScrapeOutcome
    data object MissingCredentials : ScrapeOutcome
    data class Failed(val message: String) : ScrapeOutcome
}

/** Aggregate result of running a scrape over many games at once. */
data class ScrapeBatchResult(
    val totalRequested: Int,
    val succeeded: Int,
    val notFound: Int,
    val failed: Int,
)
