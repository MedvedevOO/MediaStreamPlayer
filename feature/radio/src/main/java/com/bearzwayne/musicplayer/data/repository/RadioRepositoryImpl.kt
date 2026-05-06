package com.bearzwayne.musicplayer.data.repository

import com.bearzwayne.musicplayer.data.localdatabase.DatabaseHelper
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.radio.RadioBrowserApi
import com.bearzwayne.musicplayer.domain.repository.RadioRepository

class RadioRepositoryImpl(
    private val api: RadioBrowserApi,
    private val databaseHelper: DatabaseHelper
) : RadioRepository {

    override suspend fun getTopStations(count: Int): List<RadioStation> {
        return try {
            api.getTopStations(count).distinct()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getTopRatedStations(count: Int): List<RadioStation> {
        return try {
            api.getTopRatedStations(count).distinct()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getRecentlyChangedStations(count: Int): List<RadioStation> {
        return try {
            api.getRecentlyChangedStations(count).distinct()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getFavoriteStations(): List<RadioStation> {
        return databaseHelper.getFavoriteRadioStations().distinct()
    }

    override suspend fun updateRadioList(newRadioList: List<RadioStation>) {
        databaseHelper.updateFavoriteRadioStations(newRadioList)
    }

    override suspend fun searchStations(name: String?, country: String?, language: String?): List<RadioStation> {
        return try {
            api.searchStations(name, country, language)
        } catch (_: Exception) {
            emptyList()
        }
    }
}