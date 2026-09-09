package com.elyndra.app.data.playtime

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Elyndra can't observe what happens *inside* an external emulator, so
 * playtime is approximated as "how long Elyndra stayed backgrounded after
 * the user pressed Play" - i.e. the time between [onGameLaunched] and the
 * process's next `onStart` (registered against [androidx.lifecycle.ProcessLifecycleOwner]
 * in ElyndraApplication). This is the same trick every Android ROM frontend
 * uses, since there's no cross-app API for "how long was that app open".
 */
@Singleton
class PlaytimeTracker @Inject constructor() : DefaultLifecycleObserver {

    data class CompletedSession(
        val gameId: Long,
        val startedAt: Long,
        val endedAt: Long,
        val durationSeconds: Long,
    )

    private var pendingGameId: Long? = null
    private var launchedAtMillis: Long = 0L

    private val _sessionRecorded = MutableSharedFlow<CompletedSession>(extraBufferCapacity = 4)
    val sessionRecorded: SharedFlow<CompletedSession> = _sessionRecorded

    fun onGameLaunched(gameId: Long) {
        pendingGameId = gameId
        launchedAtMillis = System.currentTimeMillis()
    }

    override fun onStart(owner: LifecycleOwner) {
        val gameId = pendingGameId ?: return
        pendingGameId = null
        val endedAt = System.currentTimeMillis()
        val durationSeconds = (endedAt - launchedAtMillis) / 1000
        if (durationSeconds > 0) {
            _sessionRecorded.tryEmit(CompletedSession(gameId, launchedAtMillis, endedAt, durationSeconds))
        }
    }
}
