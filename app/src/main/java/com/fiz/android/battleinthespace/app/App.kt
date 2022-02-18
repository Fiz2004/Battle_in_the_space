package com.fiz.android.battleinthespace.app

import android.app.Application
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import io.realm.Realm

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        PlayerRepository.initialize(this)
    }
}