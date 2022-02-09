package com.fiz.android.battleinthespace.base.data.storage

import android.content.Context

private const val SHARED_PREFS_NAME = "shared_prefs_player"
private const val KEY_COUNT_PLAYER = "countPlayer"

class SharedPrefPlayerStorage(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun save(count: Int): Boolean {

        sharedPreferences.edit().putInt(KEY_COUNT_PLAYER, count).apply()
        return true
    }

    fun get(): Int {
        val countPlayers = sharedPreferences.getInt(KEY_COUNT_PLAYER, 4)
        return countPlayers
    }

}