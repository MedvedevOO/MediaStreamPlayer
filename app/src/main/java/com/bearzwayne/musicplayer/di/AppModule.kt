package com.bearzwayne.musicplayer.di

import android.content.Context
import com.bearzwayne.musicplayer.data.di.ApplicationScope
import com.bearzwayne.musicplayer.data.localdatabase.DatabaseHelper
import com.bearzwayne.musicplayer.data.localdatabase.MusicPlayerDatabase
import com.bearzwayne.musicplayer.data.remotedatabase.MusicRemoteDatabase
import com.bearzwayne.musicplayer.data.repository.MusicRepositoryImpl
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Singleton
    @Provides
    fun provideMusicRepository(
        @ApplicationContext context: Context,
        musicRemoteDatabase: MusicRemoteDatabase,
        databaseHelper: DatabaseHelper,
        @ApplicationScope applicationScope: CoroutineScope
    ): MusicRepository =
        MusicRepositoryImpl(context, musicRemoteDatabase, databaseHelper, applicationScope)

    @Singleton
    @Provides
    fun provideMusicController(
        @ApplicationContext context: Context,
        @ApplicationScope applicationScope: CoroutineScope
    ): MusicController =
        MusicControllerImpl(context, applicationScope)
}

