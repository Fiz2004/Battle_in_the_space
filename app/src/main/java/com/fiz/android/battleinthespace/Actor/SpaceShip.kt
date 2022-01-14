package com.fiz.android.battleinthespace.Actor

import com.fiz.android.battleinthespace.Controller
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val SPEED_ANGLE_PER_SECOND: Double = 200.0
private const val INCREASE_SPEED_PER_SECOND: Double = 1.2
private const val SPEED_MAX: Double = 2.0

class SpaceShip(
    centerX: Double,
    centerY: Double,

    speedX: Double = 0.0,
    speedY: Double = 0.0,

    angle: Double,

    size: Double = 1.0,

    var inGame: Boolean = true,
    var isFly:Boolean=false
) : Actor(
    centerX, centerY, speedX, speedY, angle, size, SPEED_MAX
) {
    fun moveRotate(deltaTime: Int, controller: Controller) {
        val step = (SPEED_ANGLE_PER_SECOND * deltaTime / 1000)
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

}