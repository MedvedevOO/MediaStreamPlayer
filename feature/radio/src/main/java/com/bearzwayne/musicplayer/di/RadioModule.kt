package com.bearzwayne.musicplayer.di

import com.bearzwayne.musicplayer.data.repository.RadioRepositoryImpl
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
    fun provideRadioRepository(): RadioRepository = RadioRepositoryImpl()
}
