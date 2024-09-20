package com.example.musicplayer.ui.search

import com.example.musicplayer.domain.model.Song

sealed class SearchScreenEvent {
    data class PlaySong(val song: Song): SearchScreenEvent()//
    data class OnSongLikeClick(val song: Song) : SearchScreenEvent()//
}