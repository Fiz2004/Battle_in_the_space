package com.fiz.android.battleinthespace.app

import android.app.Application
import com.fiz.android.battleinthespace.base.data.PlayerRepository

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerRepository.initialize(this)
    }
}