package com.example.musicplayer.domain.usecase

import com.example.musicplayer.data.repository.RadioRepository
import com.example.musicplayer.domain.model.RadioCountry
import com.example.musicplayer.domain.model.RadioStation
import javax.inject.Inject

class GetCountryListUseCase @Inject constructor(private val radioRepository: RadioRepository) {

    suspend operator fun invoke(): List<RadioCountry> {
        return radioRepository.getCountryList()
    }
}