package com.fiz.android.battleinthespace.options

import android.app.Application
import com.fiz.android.battleinthespace.database.PlayerRepository

class BattleInTheSpaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerRepository.initialize(this)
    }
}