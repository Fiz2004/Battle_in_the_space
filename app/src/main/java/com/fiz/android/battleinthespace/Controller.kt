package com.fiz.android.battleinthespace

import com.fiz.android.battleinthespace.engine.Vec

class Controller(
    var fire: Boolean = false,
    _timeBetweenFireMin: Int =500,
    _angle: Float = 0F,
    var power: Float = 0F
) {
    var press: Vec =Vec(0.0,0.0)
    private var timeBetweenFireMin=_timeBetweenFireMin
    private var timeLastFire: Int = 0
    fun isCanFire(deltaTime: Int): Boolean {
        if (timeLastFire == 0) {
            timeLastFire = timeBetweenFireMin
            return true
        }
        timeLastFire -= deltaTime
        if (timeLastFire < 0)
            timeLastFire = 0
        return false
    }


    var angle: Float = _angle
        set(value) {
            field = value
            if (value > 360)
                field = value - 360
            if (value < 0)
                field = value + 360
        }
}