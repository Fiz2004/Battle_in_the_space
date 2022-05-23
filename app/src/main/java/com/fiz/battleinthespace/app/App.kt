package com.fiz.battleinthespace.app

import android.app.Application
import android.content.Intent
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.feature_gamescreen.ui.GameActivity
import com.fiz.battleinthespace.feature_mainscreen.ui.ApplicationFeatureMainScreen
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), ApplicationFeatureMainScreen {

    @Inject
    lateinit var playersLocalDataSource: PlayersLocalDataSource

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        playersLocalDataSource.close()
    }

    override fun getIntentForNextScreen(): Intent {
        return Intent(this, GameActivity::class.java)
    }
}