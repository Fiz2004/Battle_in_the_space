package com.fiz.battleinthespace.core

import android.app.Application
import com.fiz.battleinthespace.database.PlayerRepository
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.storage.SharedPrefPlayerStorage
import com.google.android.material.color.DynamicColors
import io.realm.Realm

class App : Application() {
    private val playersLocalDataSource: PlayersLocalDataSource by lazy {
        PlayersLocalDataSource()
    }

    val playerRepository: PlayerRepository by lazy {
        PlayerRepository(playersLocalDataSource, SharedPrefPlayerStorage(applicationContext))
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Realm.init(this)
    }
}