package com.fiz.battleinthespace.repositories

import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorage
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val sharedPrefPlayerStorage: SharedPrefPlayerStorage
) : SettingsRepository {

    private val uuid: MutableStateFlow<String> = MutableStateFlow(sharedPrefPlayerStorage.getUuid())

    private val countPlayersFlow: MutableStateFlow<Int> =
        MutableStateFlow(sharedPrefPlayerStorage.getCountPlayers())

    override fun getFlowUuid(): MutableStateFlow<String> {
        return uuid
    }

    override fun getUuid(): String {
        return sharedPrefPlayerStorage.getUuid()
    }

    override fun getFlowCountPlayers(): MutableStateFlow<Int> {
        return countPlayersFlow
    }

    override fun getCountPlayers(): Int {
        return sharedPrefPlayerStorage.getCountPlayers()
    }


    override fun getIsFirstLaunch(): Boolean {
        return sharedPrefPlayerStorage.loadIsFirstLaunch()
    }

    override fun saveCountPlayers(count: Int): Boolean {
        countPlayersFlow.value = count
        return sharedPrefPlayerStorage.saveCountPlayers(count)
    }

    override fun saveAuthUuid(uuid: String) {
        this@SettingsRepositoryImpl.uuid.value = uuid
        return sharedPrefPlayerStorage.saveAuthUuid(uuid)
    }

    override fun saveIsFirstLaunchComplete(uuid: String) {
        this@SettingsRepositoryImpl.uuid.value = uuid
        sharedPrefPlayerStorage.saveIsFirstLaunchComplete(uuid)
    }

    override fun deleteAuthUuid() {
        sharedPrefPlayerStorage.deleteAuthUuid()
        this@SettingsRepositoryImpl.uuid.value = sharedPrefPlayerStorage.getUuid()
    }

}