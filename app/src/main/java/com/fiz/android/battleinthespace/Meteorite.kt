package com.fiz.android.battleinthespace

import kotlin.math.cos
import kotlin.math.sin

private const val SPEED_MAX:Double = 1.0

class Meteorite(
    centerX: Double,
    centerY: Double,

    speedX: Double = 0.0,
    speedY: Double = 0.0,

    angle: Double,

    size: Double,

    var viewSize: Int,

    var view: Int
) : Actor(
    centerX, centerY, speedX, speedY, angle, size,SPEED_MAX
) {
    companion object {
        fun create(): Meteorite {
            val angle = 210
//            val angle = (0..360).shuffled().first()
            return Meteorite(
                centerX = 8.0,
                centerY = 6.0,
                speedX = SPEED_MAX * cos(angle / 180.0 * Math.PI),
                speedY = SPEED_MAX * sin(angle / 180.0 * Math.PI),
                angle = angle.toDouble(),
                size = 1.0,
                viewSize = 0,
                view = (0..1).shuffled().first()
            )
        }
    }
}