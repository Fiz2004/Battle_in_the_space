package com.fiz.battleinthespace.feature_mainscreen.di

import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.repositories.PlayerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindPlayerRepository(playerRepository: PlayerRepositoryImpl): PlayerRepository
}