package com.fiz.battleinthespace.app

import android.app.Application
import android.content.Intent
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorage
import com.fiz.battleinthespace.feature_gamescreen.presentation.ApplicationFeatureGameScreen
import com.fiz.battleinthespace.feature_gamescreen.presentation.GameActivity
import com.fiz.battleinthespace.feature_mainscreen.ui.ApplicationFeatureMainScreen
import com.google.android.material.color.DynamicColors
import io.realm.Realm

class App : Application(), ApplicationFeatureMainScreen, ApplicationFeatureGameScreen {
    private val playersLocalDataSource: PlayersLocalDataSource by lazy {
        PlayersLocalDataSource()
    }

    private val sharedPrefPlayerStorage: SharedPrefPlayerStorage by lazy {
        SharedPrefPlayerStorage(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Realm.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        playersLocalDataSource.close()
    }

    override fun getPlayersLocalDataSourceFeatureMainScreen(): PlayersLocalDataSource {
        return playersLocalDataSource
    }

    override fun getPlayersLocalDataSourceFeatureGameScreen(): PlayersLocalDataSource {
        return playersLocalDataSource
    }

    override fun getSharedPrefPlayerStorageFeatureMainScreen(): SharedPrefPlayerStorage {
        return sharedPrefPlayerStorage
    }

    override fun getSharedPrefPlayerStorageFeatureGameScreen(): SharedPrefPlayerStorage {
        return sharedPrefPlayerStorage
    }

    override fun getIntentForNextScreen(): Intent {
        return Intent(this, GameActivity::class.java)
    }
}