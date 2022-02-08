package com.fiz.android.battleinthespace

import android.app.Application
import com.fiz.android.battleinthespace.base.data.PlayerRepository

class BattleInTheSpaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerRepository.initialize(this)
    }
}