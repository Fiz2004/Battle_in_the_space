package com.fiz.android.battleinthespace

import kotlin.math.cos
import kotlin.math.sin

data class SpaceShip(
    override var centerX: Double,
    override var centerY: Double,

    override var speedX: Double = 0.0,
    override var speedY: Double = 0.0,

    override var angle: Double,
    var inGame: Boolean = true,

    private val SPEED_ANGLE_PER_SECOND: Int = 200,
    private val INCREASE_SPEED_PER_SECOND: Double = 0.8,
    private val SPEED_MAX: Double = 2.0
) : Actor(
    centerX,centerY,speedX,speedY,angle
)  {
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

        speedY += step * sin(angle / 180 * Math.PI)
        if (speedY >= SPEED_MAX)
            speedY = SPEED_MAX
        if (speedY <= -SPEED_MAX)
            speedY = -SPEED_MAX


        speedX += step * cos(angle / 180 * Math.PI)
        if (speedX >= SPEED_MAX)
            speedX = SPEED_MAX
        if (speedX <= -SPEED_MAX)
            speedX = -SPEED_MAX
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