package com.fiz.android.battleinthespace

import kotlin.math.cos
import kotlin.math.sin

private const val SPEED_ANGLE_PER_SECOND: Int = 200
private const val INCREASE_SPEED_PER_SECOND: Double = 0.8
private const val SPEED_MAX: Double = 2.0

class SpaceShip(
    centerX: Double,
    centerY: Double,

    speedX: Double = 0.0,
    speedY: Double = 0.0,

    angle: Double,

    size:Double=1.0,

    var inGame: Boolean = true,
) : Actor(
    centerX, centerY, speedX, speedY, angle,size
) {
    fun moveRight(deltaTime: Int) {
        val step = (SPEED_ANGLE_PER_SECOND * deltaTime / 1000)
        angle += step
        if (angle > 360)
            angle -= 360
    }

    fun moveLeft(deltaTime: Int) {
        val step = (SPEED_ANGLE_PER_SECOND * deltaTime / 1000)
        angle -= step
        if (angle < 0)
            angle += 360
    }

    fun moveUp(deltaTime: Int) {
        println("up")
        val step = (INCREASE_SPEED_PER_SECOND * deltaTime / 1000)

        speedX += step * cos(angle / 180 * Math.PI)
        if (speedX >= SPEED_MAX)
            speedX = SPEED_MAX
        if (speedX <= -SPEED_MAX)
            speedX = -SPEED_MAX

        speedY += step * sin(angle / 180 * Math.PI)
        if (speedY >= SPEED_MAX)
            speedY = SPEED_MAX
        if (speedY <= -SPEED_MAX)
            speedY = -SPEED_MAX
    }

    fun moveDown(deltaTime: Int) {
        val step = (INCREASE_SPEED_PER_SECOND * deltaTime / 1000)

        speedY -= step * sin(angle / 180 * Math.PI)
        if (speedY <= 0.0)
            speedY = 0.0

        speedX -= step * cos(angle / 180 * Math.PI)
        if (speedX <= 0.0)
            speedX = 0.0
    }

}