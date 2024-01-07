package com.fiz.battleinthespace.app

import android.app.Application
import android.util.Log
import com.fiz.battleinthespace.common.AppDispatchers
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
internal class App : Application() {

    private var timeStart = System.currentTimeMillis()

    @Inject
    lateinit var dispatchers: AppDispatchers

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(dispatchers.default).launch {
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
