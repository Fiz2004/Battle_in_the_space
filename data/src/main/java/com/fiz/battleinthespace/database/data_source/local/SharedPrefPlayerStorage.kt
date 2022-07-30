package com.fiz.battleinthespace.database.data_source.local

import android.content.SharedPreferences
import javax.inject.Inject

private const val KEY_COUNT_PLAYER = "countPlayer"
private const val IS_FIRST_LAUNCH = "isFirstLaunch"
private const val UUID = "uuid"
private const val GUUID = "guuid"

class SharedPrefPlayerStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveCountPlayers(count: Int): Boolean {
        sharedPreferences.edit().putInt(KEY_COUNT_PLAYER, count).apply()
        return true
    }

    fun getCountPlayers(): Int {
        return sharedPreferences.getInt(KEY_COUNT_PLAYER, 4)
    }

    fun getUuid(): String {
        val guuid = sharedPreferences.getString(GUUID, "") ?: ""
        return if (guuid == "")
            sharedPreferences.getString(UUID, "") ?: ""
        else
            guuid
    }

    fun loadIsFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
    }

    fun saveIsFirstLaunchComplete(uuid: String) {
        sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, false).apply()
        sharedPreferences.edit().putString(UUID, uuid).apply()
    }

    fun saveAuthUuid(uuid: String) {
        sharedPreferences.edit().putString(GUUID, uuid).apply()
    }

    fun deleteAuthUuid() {
        sharedPreferences.edit().putString(GUUID, "").apply()
    }

}