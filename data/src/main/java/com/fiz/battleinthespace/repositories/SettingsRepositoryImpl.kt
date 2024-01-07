package com.fiz.battleinthespace.repositories

import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorageImpl
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SettingsRepositoryImpl @Inject constructor(
    private val sharedPrefPlayerStorage: SharedPrefPlayerStorageImpl
) : SettingsRepository {

    private val uuid: MutableStateFlow<String?> = MutableStateFlow(null)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            uuid.value = sharedPrefPlayerStorage.getUuid()
            countPlayersFlow.value = sharedPrefPlayerStorage.getCountPlayers()
        }
    }

    private val countPlayersFlow: MutableStateFlow<Int?> = MutableStateFlow(null)

    override fun getFlowUuid(): Flow<String?> {
        return uuid
    }

    override suspend fun getUuid(): String? {
        return sharedPrefPlayerStorage.getUuid()
    }

    override fun getFlowCountPlayers(): Flow<Result<Int>> {
        return countPlayersFlow.map {
            if (it == null) {
                Result.success(0)
            } else {
                Result.success(it)
            }
        }
    }

    override suspend fun getCountPlayers(): Int {
        return sharedPrefPlayerStorage.getCountPlayers()
    }

    override suspend fun saveCountPlayers(count: Int) {
        countPlayersFlow.value = count
        sharedPrefPlayerStorage.saveCountPlayers(count)
    }

    override suspend fun saveAuthUuid(uuid: String) {
        this.uuid.value = uuid
        sharedPrefPlayerStorage.saveGUuid(uuid)
    }

    override suspend fun deleteAuthUuid() {
        sharedPrefPlayerStorage.deleteGUuid()
        this@SettingsRepositoryImpl.uuid.value = sharedPrefPlayerStorage.getUuid()
    }

}