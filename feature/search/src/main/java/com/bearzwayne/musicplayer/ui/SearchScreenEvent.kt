package com.bearzwayne.musicplayer.ui

import com.bearzwayne.musicplayer.domain.model.Song

sealed class SearchScreenEvent {
    data class PlaySong(val song: Song): SearchScreenEvent()//
    data class OnSongLikeClick(val song: Song) : SearchScreenEvent()//
}