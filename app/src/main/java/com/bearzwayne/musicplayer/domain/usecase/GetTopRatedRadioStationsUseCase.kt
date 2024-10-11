package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.data.repository.RadioRepository
import com.bearzwayne.musicplayer.domain.model.RadioStation
import javax.inject.Inject

class GetTopRatedRadioStationsUseCase @Inject constructor(private val radioRepository: RadioRepository) {

    suspend operator fun invoke(count: Int): List<RadioStation> {
        return radioRepository.getTopRatedStations(count)
    }
}