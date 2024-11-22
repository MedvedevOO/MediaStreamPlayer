package com.bearzwayne.musicplayer.di

import android.content.Context
import com.bearzwayne.musicplayer.data.localdatabase.MusicPlayerDatabase
import com.bearzwayne.musicplayer.data.remotedatabase.MusicRemoteDatabase
import com.bearzwayne.musicplayer.data.repository.MusicRepositoryImpl
import com.bearzwayne.musicplayer.data.repository.RadioRepositoryImpl
import com.bearzwayne.musicplayer.domain.repository.RadioRepository
import com.bearzwayne.musicplayer.data.service.MusicControllerImpl
import com.bearzwayne.musicplayer.domain.repository.MusicRepository
import com.bearzwayne.musicplayer.domain.service.MusicController
import com.bearzwayne.musicplayer.other.Constants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCollection() = FirebaseFirestore.getInstance().collection(Constants.SONG_COLLECTION)

    @Singleton
    @Provides
    fun provideMusicDatabase(songCollection: CollectionReference) =
        MusicRemoteDatabase(songCollection)

    @Singleton
    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context) = MusicPlayerDatabase.getDatabase(context)



    @Singleton
    @Provides
    fun provideMusicRepository(
        @ApplicationContext context: Context,
        musicRemoteDatabase: MusicRemoteDatabase
    ): MusicRepository =
        MusicRepositoryImpl(context,musicRemoteDatabase)

    @Singleton
    @Provides
    fun provideRadioRepository() : RadioRepository = RadioRepositoryImpl()

    @Singleton
    @Provides
    fun provideMusicController(@ApplicationContext context: Context): MusicController =
        MusicControllerImpl(context)

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context
}

