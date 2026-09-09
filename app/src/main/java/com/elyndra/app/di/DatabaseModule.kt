package com.elyndra.app.di

import android.content.Context
import androidx.room.Room
import com.elyndra.app.data.local.ElyndraDatabase
import com.elyndra.app.data.local.dao.GameDao
import com.elyndra.app.data.local.dao.PlatformDao
import com.elyndra.app.data.local.dao.PlaySessionDao
import com.elyndra.app.data.local.dao.RomFolderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ElyndraDatabase =
        Room.databaseBuilder(context, ElyndraDatabase::class.java, ElyndraDatabase.DATABASE_NAME)
            // Pre-release: no installed base to preserve, so a schema bump just
            // recreates the (small, easily-rescanned) local library.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGameDao(database: ElyndraDatabase): GameDao = database.gameDao()

    @Provides
    fun providePlatformDao(database: ElyndraDatabase): PlatformDao = database.platformDao()

    @Provides
    fun providePlaySessionDao(database: ElyndraDatabase): PlaySessionDao = database.playSessionDao()

    @Provides
    fun provideRomFolderDao(database: ElyndraDatabase): RomFolderDao = database.romFolderDao()
}
