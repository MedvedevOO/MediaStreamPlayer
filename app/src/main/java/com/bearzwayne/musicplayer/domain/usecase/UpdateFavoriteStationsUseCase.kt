package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.data.repository.RadioRepository
import com.bearzwayne.musicplayer.domain.model.RadioStation
import javax.inject.Inject

class UpdateFavoriteStationsUseCase @Inject constructor(private val radioRepository: RadioRepository) {

    suspend operator fun invoke(newRadioList: List<RadioStation>) {
        return radioRepository.updateRadioList(newRadioList)
    }
}