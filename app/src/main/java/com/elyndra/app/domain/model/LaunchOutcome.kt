package com.elyndra.app.domain.model

sealed interface LaunchOutcome {
    data object Launched : LaunchOutcome
    data object EmulatorNotConfigured : LaunchOutcome
    data class EmulatorNotInstalled(val packageName: String) : LaunchOutcome
    data class Failed(val message: String) : LaunchOutcome
}
