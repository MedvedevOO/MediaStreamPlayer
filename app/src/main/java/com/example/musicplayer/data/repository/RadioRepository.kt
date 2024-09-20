package com.example.musicplayer.data.repository

import com.example.musicplayer.data.localdatabase.DatabaseHelper
import com.example.musicplayer.data.retrofitinstance.RetrofitInstance
import com.example.musicplayer.domain.model.RadioStation

class RadioRepository {

    suspend fun getTopStations(count: Int): List<RadioStation> {
        return try {
            RetrofitInstance.api.getTopStations(count)
        } catch (e: Exception) {
            emptyList() // Handle the error gracefully
        }
    }

    suspend fun getTopRatedStations(count: Int): List<RadioStation> {
        return try {
            RetrofitInstance.api.getTopRatedStations(count)
        } catch (e: Exception) {
            emptyList() // Handle the error gracefully
        }
    }

    suspend fun getRecentlyChangedStations(count: Int): List<RadioStation> {
        return try {
            RetrofitInstance.api.getRecentlyChangedStations(count)
        } catch (e: Exception) {
            emptyList() // Handle the error gracefully
        }
    }

    suspend fun getFavoriteStations(): List<RadioStation> {
        return DatabaseHelper().getFavoriteRadioStations()
    }

    suspend fun updateRadioList(newRadioList: List<RadioStation>) {
        DatabaseHelper().updateFavoriteRadioStations(newRadioList)
    }
    suspend fun searchStations(name: String?, country: String?, language: String?): List<RadioStation> {
        return try {
            RetrofitInstance.api.searchStations(name, country, language)
        } catch (e: Exception) {
            emptyList()
        }
    }
}