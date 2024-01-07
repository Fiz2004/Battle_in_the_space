package com.fiz.battleinthespace.database.data_source.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SharedPrefPlayerStorageImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : SharedPrefPlayerStorage {

    override suspend fun getCountPlayers(): Int {
        return sharedPreferences.getInt(KEY_COUNT_PLAYER, 4)
    }

    override suspend fun saveCountPlayers(count: Int) {
        sharedPreferences.edit().putInt(KEY_COUNT_PLAYER, count).apply()
    }

    override suspend fun getUuid(): String? {
        return sharedPreferences.getString(KEY_GUUID, null) ?: (sharedPreferences.getString(KEY_UUID, null))
    }

    override suspend fun saveGUuid(uuid: String) {
        sharedPreferences.edit().putString(KEY_GUUID, uuid).apply()
    }

    override suspend fun deleteGUuid() {
        sharedPreferences.edit().putString(KEY_GUUID, null).apply()
    }

    private companion object {
        const val KEY_COUNT_PLAYER = "countPlayer"
        const val KEY_UUID = "uuid"
        const val KEY_GUUID = "guuid"
    }
}