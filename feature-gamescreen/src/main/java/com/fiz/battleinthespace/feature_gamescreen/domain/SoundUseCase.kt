package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.feature_gamescreen.data.repositories.SoundRepository
import com.fiz.feature.game.SoundEvent
import javax.inject.Inject

internal class SoundUseCase @Inject constructor(private val soundRepository: SoundRepository) {
    fun play(soundEvent: SoundEvent) {
        soundRepository.soundPool.play(soundRepository.soundMap.get(soundEvent.type.ordinal), 1F, 1F, 1, 0, 1F)
    }
}