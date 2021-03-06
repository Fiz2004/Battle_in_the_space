package com.fiz.battleinthespace.feature_gamescreen.game.models

import android.graphics.Bitmap
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.ui.Display

interface Drawable {
    var center: Vec
    var angle: Double
    var size: Double

    fun getBitmap(display: Display): Bitmap
}

abstract class Actor(
    center: Vec,
    angle: Double,
    var size: Double) {

    val halfSize
        get() = size / 2

    var center: Vec = center.copy()

    var angle: Double = angle
        set(value) {
            field = value
            if (value >= 360)
                field = value - 360
            if (value < 0)
                field = value + 360
        }

    val angleToRadians: Double
        get() {
            return angle / 180.0 * Math.PI
        }
}