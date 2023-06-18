package com.fiz.battleinthespace.app

import android.app.Application
import android.util.Log
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class App : Application() {
    private var timeStart: Long = 0

    init {
        timeStart = System.currentTimeMillis()
    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Default).launch {
            coroutineScope {
                launch {
                    DynamicColors.applyToActivitiesIfAvailable(this@App)
                }
                launch {
                    FirebaseApp.initializeApp(this@App)
                }
            }
            val timeFinish = System.currentTimeMillis()
            val timeInit = timeFinish - timeStart
            Log.d("BattleInfo", "time launch = $timeInit")
        }
    }
}