package com.bearzwayne.musicplayer.data.localdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bearzwayne.musicplayer.data.localdatabase.dao.AlbumDao
import com.bearzwayne.musicplayer.data.localdatabase.dao.ArtistDao
import com.bearzwayne.musicplayer.data.localdatabase.dao.GenreDao
import com.bearzwayne.musicplayer.data.localdatabase.dao.PlaylistDao
import com.bearzwayne.musicplayer.data.localdatabase.dao.RadioStationDao
import com.bearzwayne.musicplayer.data.localdatabase.dao.SongDao

import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Genre
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.model.Song

@Database(entities = [Song::class, Album::class, Artist::class, Genre::class, Playlist::class, RadioStation::class], version = 1)
@TypeConverters(Converters::class)
abstract class MusicPlayerDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun genreDao(): GenreDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun radioStationDao(): RadioStationDao


    companion object {
        @Volatile
        private var INSTANCE: MusicPlayerDatabase? = null

        fun getDatabase(context: Context): MusicPlayerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicPlayerDatabase::class.java,
                    "Music_player"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(): MusicPlayerDatabase? {
            return INSTANCE
        }
    }


}