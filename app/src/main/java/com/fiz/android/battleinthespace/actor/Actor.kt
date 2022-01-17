package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Vec

open class Actor(_center: Vec,
                 _angle: Double,
                 var size: Double){
    var center: Vec = _center.copy()

    var angle: Double = _angle
    set(value) {
        field = value
        if (value > 360)
            field = value - 360
        if (value < 0)
            field = value + 360
    }

    val angleToRadians: Double
    get() {
        return angle / 180.0 * Math.PI
    }
}