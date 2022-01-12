package com.fiz.android.battleinthespace

import android.util.Log
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val SPEED_ANGLE_PER_SECOND: Double = 200.0
private const val INCREASE_SPEED_PER_SECOND: Double = 0.8
private const val SPEED_MAX: Double = 2.0

class SpaceShip(
    centerX: Double,
    centerY: Double,

    speedX: Double = 0.0,
    speedY: Double = 0.0,

    angle: Double,

    size: Double = 1.0,

    var inGame: Boolean = true,
) : Actor(
    centerX, centerY, speedX, speedY, angle, size, SPEED_MAX
) {
    fun moveRight(deltaTime: Int) {
        val step = (SPEED_ANGLE_PER_SECOND * deltaTime / 1000)
        angle += step
    }

    fun moveRotate(deltaTime: Int, controller: Controller) {
        val step = (SPEED_ANGLE_PER_SECOND * deltaTime / 1000)
        Log.d("moveRotate", "Step=$step")
        Log.d("moveRotate", "angle=$angle")
        Log.d("moveRotate", "controller.angle=${controller.angle}")
        if (abs(angle - controller.angle) > 180)
            if (angle > controller.angle)
                if (abs(angle - controller.angle) < step)
                    angle = controller.angle.toDouble()
                else
                    angle += step
            else
                if (abs(angle - controller.angle) < step)
                    angle = controller.angle.toDouble()
                else
                    angle -= step
        else
            if (angle < controller.angle)
                if (abs(angle - controller.angle) < step)
                    angle = controller.angle.toDouble()
                else
                    angle += step
            else
                if (abs(angle - controller.angle) < step)
                    angle = controller.angle.toDouble()
                else
                    angle -= step
    }

    fun moveForward(deltaTime: Int, controller: Controller) {
        val step = (INCREASE_SPEED_PER_SECOND * deltaTime / 1000) * controller.power

        speedX += step * cos(angle / 180.0 * Math.PI)

        speedY += step * sin(angle / 180.0 * Math.PI)

    }

    fun moveLeft(deltaTime: Int) {
        val step = (SPEED_ANGLE_PER_SECOND * deltaTime / 1000)
        angle -= step
    }

    fun moveUp(deltaTime: Int) {
        val step = (INCREASE_SPEED_PER_SECOND * deltaTime / 1000)

        speedX += step * cos(angle / 180 * Math.PI)

        speedY += step * sin(angle / 180 * Math.PI)

    }

    fun moveDown(deltaTime: Int) {
//        val step = (INCREASE_SPEED_PER_SECOND * deltaTime / 1000)
//
//        speedY -= step * sin(angle / 180 * Math.PI)
//        if (speedY < 0.0)
//            speedY = 0.0
//
//        speedX -= step * cos(angle / 180 * Math.PI)
//        if (speedX < 0.0)
//            speedX = 0.0
    }

}