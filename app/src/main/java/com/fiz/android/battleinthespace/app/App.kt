package com.fiz.android.battleinthespace.app

import android.app.Application
import com.google.android.material.color.DynamicColors
import io.realm.Realm

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Realm.init(this)
    }
}