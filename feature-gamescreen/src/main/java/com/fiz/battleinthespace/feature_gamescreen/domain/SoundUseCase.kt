package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.feature_gamescreen.data.repositories.SoundRepository
import javax.inject.Inject

class SoundUseCase @Inject constructor(private val soundRepository: SoundRepository) {
    fun play(numberSound: Int) {
        soundRepository.soundPool.play(soundRepository.soundMap.get(numberSound), 1F, 1F, 1, 0, 1F)
    }
}