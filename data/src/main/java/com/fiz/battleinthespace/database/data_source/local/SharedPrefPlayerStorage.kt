package com.fiz.battleinthespace.database.data_source.local

import android.content.SharedPreferences
import javax.inject.Inject

private const val KEY_COUNT_PLAYER = "countPlayer"
private const val IS_FIRST_LAUNCH = "isFirstLaunch"

class SharedPrefPlayerStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveCountPlayers(count: Int): Boolean {
        sharedPreferences.edit().putInt(KEY_COUNT_PLAYER, count).apply()
        return true
    }

    fun getCountPlayers(): Int {
        return sharedPreferences.getInt(KEY_COUNT_PLAYER, 4)
    }

    fun loadIsFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
    }

    fun saveIsFirstLaunchComplete() {
        sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, false).apply()
    }

}