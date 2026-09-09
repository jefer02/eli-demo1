package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.InstalledEmulatorApp
import com.elyndra.app.domain.model.LaunchOutcome
import com.elyndra.app.domain.model.Platform

interface GameLauncherRepository {
    fun isEmulatorInstalled(packageName: String): Boolean
    fun launch(game: Game, platform: Platform): LaunchOutcome

    /** Installed apps from the curated known-emulator list (see manifest `<queries>`). */
    fun getInstalledCandidateEmulators(): List<InstalledEmulatorApp>
}
