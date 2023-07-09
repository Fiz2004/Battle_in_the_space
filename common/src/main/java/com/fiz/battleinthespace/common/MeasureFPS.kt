package com.fiz.battleinthespace.common

import android.util.Log

class MeasureFPS {
    var lastTime = System.currentTimeMillis()
    var fps = 60

    operator fun invoke(body: () -> Unit) {
        val now = System.currentTimeMillis()
        val deltaTime = now - lastTime
        fps = (1000.0/deltaTime).toInt()

        Log.d("FPS", fps.toString())

        body()

        lastTime = now
    }
}