package com.fiz.battleinthespace.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getFlowCountPlayers(): Flow<Result<Int>>

    suspend fun getCountPlayers(): Int

    fun getFlowUuid(): Flow<String?>

    suspend fun getUuid(): String?

    suspend fun saveCountPlayers(count: Int)

    suspend fun saveAuthUuid(uuid: String)

    suspend fun deleteAuthUuid()
}