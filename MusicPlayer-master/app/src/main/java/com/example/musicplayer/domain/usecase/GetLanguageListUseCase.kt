package com.example.musicplayer.domain.usecase

import com.example.musicplayer.data.repository.RadioRepository
import com.example.musicplayer.domain.model.RadioCountry
import com.example.musicplayer.domain.model.RadioLanguage
import com.example.musicplayer.domain.model.RadioStation
import javax.inject.Inject

class GetLanguageListUseCase @Inject constructor(private val radioRepository: RadioRepository) {

    suspend operator fun invoke(): List<RadioLanguage> {
        return radioRepository.getLanguageList()
    }
}