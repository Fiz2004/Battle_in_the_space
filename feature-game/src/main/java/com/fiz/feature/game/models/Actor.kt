package com.fiz.feature.game.models

import android.graphics.Bitmap
import com.fiz.battleinthespace.repositories.BitmapRepository

internal interface Drawable : java.io.Serializable {
    var center: com.fiz.battleinthespace.common.Vec
    var angle: Double
    var size: Double

    fun getBitmap(bitmapRepository: BitmapRepository): Bitmap
}

abstract class Actor(
    center: com.fiz.battleinthespace.common.Vec,
    angle: Double,
    var size: Double
) : java.io.Serializable {

    val halfSize
        get() = size / 2

    var center: com.fiz.battleinthespace.common.Vec = center.copy()

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