package com.bearzwayne.musicplayer.domain.repository

import com.bearzwayne.musicplayer.data.localdatabase.DatabaseHelper
import com.bearzwayne.musicplayer.data.retrofitinstance.RetrofitInstance
import com.bearzwayne.musicplayer.domain.model.RadioStation

interface RadioRepository {

    suspend fun getTopStations(count: Int): List<RadioStation>

    suspend fun getTopRatedStations(count: Int): List<RadioStation>

    suspend fun getRecentlyChangedStations(count: Int): List<RadioStation>

    suspend fun getFavoriteStations(): List<RadioStation>

    suspend fun updateRadioList(newRadioList: List<RadioStation>)

    suspend fun searchStations(name: String?, country: String?, language: String?): List<RadioStation>
}