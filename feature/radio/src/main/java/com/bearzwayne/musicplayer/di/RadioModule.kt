package com.bearzwayne.musicplayer.di

import com.bearzwayne.musicplayer.data.localdatabase.DatabaseHelper
import com.bearzwayne.musicplayer.data.repository.RadioRepositoryImpl
import com.bearzwayne.musicplayer.domain.radio.RadioBrowserApi
import com.bearzwayne.musicplayer.domain.repository.RadioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RadioModule {

    @Singleton
    @Provides
    fun provideRadioRepository(api: RadioBrowserApi, databaseHelper: DatabaseHelper): RadioRepository =
        RadioRepositoryImpl(api, databaseHelper)
}
