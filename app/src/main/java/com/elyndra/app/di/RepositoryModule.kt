package com.elyndra.app.di

import com.elyndra.app.data.launcher.EmulatorLauncher
import com.elyndra.app.data.launcher.InstalledAppsRepositoryImpl
import com.elyndra.app.data.repository.GameRepositoryImpl
import com.elyndra.app.data.repository.IntegrationCredentialsRepositoryImpl
import com.elyndra.app.data.repository.PlatformRepositoryImpl
import com.elyndra.app.data.repository.PlaySessionRepositoryImpl
import com.elyndra.app.data.repository.RomFolderRepositoryImpl
import com.elyndra.app.data.repository.ScraperRepositoryImpl
import com.elyndra.app.data.repository.UserPreferencesRepositoryImpl
import com.elyndra.app.domain.repository.GameLauncherRepository
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.InstalledAppsRepository
import com.elyndra.app.domain.repository.IntegrationCredentialsRepository
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.domain.repository.PlaySessionRepository
import com.elyndra.app.domain.repository.RomFolderRepository
import com.elyndra.app.domain.repository.ScraperRepository
import com.elyndra.app.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    abstract fun bindGameLauncherRepository(impl: EmulatorLauncher): GameLauncherRepository

    @Binds
    @Singleton
    abstract fun bindPlatformRepository(impl: PlatformRepositoryImpl): PlatformRepository

    @Binds
    @Singleton
    abstract fun bindPlaySessionRepository(impl: PlaySessionRepositoryImpl): PlaySessionRepository

    @Binds
    @Singleton
    abstract fun bindRomFolderRepository(impl: RomFolderRepositoryImpl): RomFolderRepository

    @Binds
    @Singleton
    abstract fun bindScraperRepository(impl: ScraperRepositoryImpl): ScraperRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindIntegrationCredentialsRepository(
        impl: IntegrationCredentialsRepositoryImpl,
    ): IntegrationCredentialsRepository

    @Binds
    @Singleton
    abstract fun bindInstalledAppsRepository(impl: InstalledAppsRepositoryImpl): InstalledAppsRepository
}
