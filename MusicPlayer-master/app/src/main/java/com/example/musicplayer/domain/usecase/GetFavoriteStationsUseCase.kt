package com.example.musicplayer.domain.usecase

import com.example.musicplayer.data.repository.RadioRepository
import com.example.musicplayer.domain.model.RadioStation
import javax.inject.Inject

class GetFavoriteStationsUseCase @Inject constructor(private val radioRepository: RadioRepository) {

    suspend operator fun invoke(): List<RadioStation> {
        return radioRepository.getFavoriteStations()
    }
}