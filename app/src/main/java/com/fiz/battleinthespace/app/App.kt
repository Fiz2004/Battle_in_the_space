package com.fiz.battleinthespace.app

import android.app.Application
import android.content.Intent
import com.fiz.battleinthespace.database.PlayerRepository
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.storage.SharedPrefPlayerStorage
import com.fiz.battleinthespace.feature_gamescreen.presentation.GameActivity
import com.fiz.battleinthespace.feature_mainscreen.ui.ApplicationFeatureMainScreen
import com.google.android.material.color.DynamicColors

class App : Application(), ApplicationFeatureMainScreen {
    private val playersLocalDataSource: PlayersLocalDataSource by lazy {
        PlayersLocalDataSource()
    }

    val playerRepository: PlayerRepository by lazy {
        PlayerRepository(playersLocalDataSource, SharedPrefPlayerStorage(applicationContext))
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun getRepository(): PlayerRepository {
        return playerRepository
    }

    override fun getIntentForNextScreen(): Intent {
        return Intent(this, GameActivity::class.java)
    }
}