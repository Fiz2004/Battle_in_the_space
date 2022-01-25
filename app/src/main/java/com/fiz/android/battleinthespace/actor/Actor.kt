package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Vec

interface Drawable {
    var center: Vec
    var angle: Double
    var size: Double
}

abstract class Actor(
    _center: Vec,
    _angle: Double,
    override var size: Double) : Drawable {

    val halfSize
        get() = size / 2

    override var center: Vec = _center.copy()

    override var angle: Double = _angle
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