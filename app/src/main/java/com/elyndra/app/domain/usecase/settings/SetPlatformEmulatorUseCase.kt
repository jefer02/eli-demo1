package com.elyndra.app.domain.usecase.settings

import com.elyndra.app.domain.repository.PlatformRepository
import javax.inject.Inject

class SetPlatformEmulatorUseCase @Inject constructor(
    private val platformRepository: PlatformRepository,
) {
    suspend operator fun invoke(
        platformId: String,
        packageName: String?,
        activityName: String?,
        action: String = "android.intent.action.VIEW",
    ) {
        platformRepository.setEmulatorConfig(platformId, packageName, activityName, action)
    }
}
