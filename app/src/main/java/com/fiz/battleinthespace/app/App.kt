package com.fiz.battleinthespace.app

import android.app.Application
import android.content.Intent
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorage
import com.fiz.battleinthespace.database.repositories.PlayerRepository
import com.fiz.battleinthespace.feature_gamescreen.presentation.ApplicationFeatureGameScreen
import com.fiz.battleinthespace.feature_gamescreen.presentation.GameActivity
import com.fiz.battleinthespace.feature_mainscreen.ui.ApplicationFeatureMainScreen
import com.google.android.material.color.DynamicColors
import io.realm.Realm

class App : Application(), ApplicationFeatureMainScreen, ApplicationFeatureGameScreen {
    private val playersLocalDataSource: PlayersLocalDataSource by lazy {
        PlayersLocalDataSource()
    }

    private val playerRepository: PlayerRepository by lazy {
        PlayerRepository(playersLocalDataSource, SharedPrefPlayerStorage(applicationContext))
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Realm.init(this)
    }

    override fun getRepositoryFeatureMainScreen(): PlayerRepository {
        return playerRepository
    }

    override fun getRepositoryFeatureGameScreen(): PlayerRepository {
        return playerRepository
    }

    override fun getIntentForNextScreen(): Intent {
        return Intent(this, GameActivity::class.java)
    }
}