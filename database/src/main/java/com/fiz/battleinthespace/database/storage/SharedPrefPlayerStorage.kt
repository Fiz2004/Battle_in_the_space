package com.fiz.battleinthespace.database.storage

import android.content.Context

private const val SHARED_PREFS_NAME = "shared_prefs_player"
private const val KEY_COUNT_PLAYER = "countPlayer"
private const val IS_FIRST_LAUNCH = "isFirstLaunch"

class SharedPrefPlayerStorage(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

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