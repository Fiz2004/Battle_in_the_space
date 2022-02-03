package com.fiz.android.battleinthespace.options

import android.app.Application

class BattleInTheSpaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerRepository.initialize(this)
    }
}