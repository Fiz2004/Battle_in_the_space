package com.fiz.battleinthespace.di

import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorage
import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorageImpl
import com.fiz.battleinthespace.database.data_source.network.PlayersRemoteDataSource
import com.fiz.battleinthespace.database.data_source.network.PlayersRemoteDataSourceImpl
import com.fiz.battleinthespace.domain.repositories.AuthRepository
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import com.fiz.battleinthespace.repositories.AuthRepositoryImpl
import com.fiz.battleinthespace.repositories.PlayerRepositoryImpl
import com.fiz.battleinthespace.repositories.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModuleBinds {

    @Binds
    abstract fun bindPlayerRepository(playerRepository: PlayerRepositoryImpl): PlayerRepository

    @Binds
    abstract fun bindAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindSettingsRepository(settingsRepository: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindSharedPrefPlayerStorage(sharedPrefPlayerStorage: SharedPrefPlayerStorageImpl): SharedPrefPlayerStorage

    @Binds
    abstract fun bindPlayersRemoteDataSourcee(playersRemoteDataSource: PlayersRemoteDataSourceImpl): PlayersRemoteDataSource
}