package com.elyndra.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.elyndra.app.data.playtime.PlaytimeTracker
import com.elyndra.app.di.ApplicationScope
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.domain.usecase.playtime.RecordPlaySessionUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ElyndraApplication : Application() {

    @Inject lateinit var platformRepository: PlatformRepository

    @Inject lateinit var playtimeTracker: PlaytimeTracker

    @Inject lateinit var recordPlaySessionUseCase: RecordPlaySessionUseCase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()

        // PlaytimeTracker only knows "the app came back to the foreground"; wiring
        // it to ProcessLifecycleOwner here is what turns that into real sessions.
        ProcessLifecycleOwner.get().lifecycle.addObserver(playtimeTracker)

        applicationScope.launch {
            platformRepository.ensureSeeded()
        }

        applicationScope.launch {
            playtimeTracker.sessionRecorded.collect { session ->
                recordPlaySessionUseCase(
                    gameId = session.gameId,
                    startedAt = session.startedAt,
                    endedAt = session.endedAt,
                    durationSeconds = session.durationSeconds,
                )
            }
        }
    }
}
