package com.fiz.battleinthespace.domain.repositories

import kotlinx.coroutines.flow.MutableStateFlow

interface SettingsRepository {

    fun getFlowCountPlayers(): MutableStateFlow<Int>

    fun getFlowUuid(): MutableStateFlow<String>

    fun getIsFirstLaunch(): Boolean

    fun saveCountPlayers(count: Int): Boolean

    fun saveIsFirstLaunchComplete(uuid: String)

    fun saveAuthUuid(uuid: String)

    fun deleteAuthUuid()
}