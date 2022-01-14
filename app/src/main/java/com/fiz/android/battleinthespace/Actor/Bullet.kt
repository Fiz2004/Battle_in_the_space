package com.fiz.android.battleinthespace.Actor

import kotlin.math.sqrt

private const val SPEED_MAX:Double = 4.0

class Bullet(
    centerX: Double,
    centerY: Double,

    speedX: Double,
    speedY: Double,

    angle: Double,

    size:Double=0.1,

    var roadLength: Double,

    var player: Int,
) : Actor(
    centerX, centerY, speedX, speedY, angle,size, SPEED_MAX
) {
    override fun update(deltaTime: Int, width: Double, height: Double) {
        super.update(deltaTime, width, height)
        val roadX=speedX*deltaTime/1000
        val roadY=speedY*deltaTime/1000
        roadLength += sqrt(roadX*roadX+roadY*roadY)
    }
}