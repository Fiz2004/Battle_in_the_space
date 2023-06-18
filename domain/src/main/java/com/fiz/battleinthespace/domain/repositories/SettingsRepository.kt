package com.fiz.battleinthespace.domain.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    fun getFlowCountPlayers(): MutableStateFlow<Int>
    fun getCountPlayers(): Int

    fun getFlowUuid(): StateFlow<String>
    fun getUuid(): String

    fun getIsFirstLaunch(): Boolean

    fun saveCountPlayers(count: Int): Boolean

    fun saveIsFirstLaunchComplete(uuid: String)

    fun saveAuthUuid(uuid: String)

    fun deleteAuthUuid()
}