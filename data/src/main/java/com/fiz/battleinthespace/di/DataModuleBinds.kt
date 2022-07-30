package com.fiz.battleinthespace.di

import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import com.fiz.battleinthespace.repositories.PlayerRepositoryImpl
import com.fiz.battleinthespace.repositories.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModuleBinds {

    @Binds
    abstract fun bindPlayerRepository(playerRepository: PlayerRepositoryImpl): PlayerRepository

    @Binds
    abstract fun bindSettingsRepository(settingsRepository: SettingsRepositoryImpl): SettingsRepository
}