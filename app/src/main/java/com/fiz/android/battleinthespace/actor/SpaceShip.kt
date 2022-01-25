package com.fiz.android.battleinthespace.actor

import android.graphics.Bitmap
import com.fiz.android.battleinthespace.Controller
import com.fiz.android.battleinthespace.Display
import com.fiz.android.battleinthespace.engine.Vec
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val SPEED_ANGLE_PER_SECOND: Double = 200.0
private const val INCREASE_SPEED_PER_SECOND: Double = 1.2
private const val SPEED_MAX: Double = 2.0
private const val TIME_RESPAWN_MIN: Double = 1.0

class SpaceShip(
    center: Vec,

    speed: Vec = Vec(0.0, 0.0),

    angle: Double,

    size: Double = 1.0,

    var inGame: Boolean = true,
    var isFly: Boolean = false,
    val player: Int = 0
) : MoveableActor(
    center, speed, angle, size, SPEED_MAX
) {
    constructor (respawn: Respawn, player: Int) : this(Vec(respawn.center), player = player, angle = respawn.angle)

    private var timeRespawn: Double = TIME_RESPAWN_MIN

    fun isCanRespawnFromTime(deltaTime: Double): Boolean {
        timeRespawn -= deltaTime
        if (timeRespawn < 0) {
            timeRespawn = TIME_RESPAWN_MIN
            return true
        }
        return false
    }

    fun moveRotate(deltaTime: Double, controller: Controller) {
        val step = SPEED_ANGLE_PER_SECOND * deltaTime

        if (abs(angle - controller.angle) < step) {
            angle = controller.angle.toDouble()
            return
        }

        angle += getSignStepRotate(controller, step)
    }

    private fun getSignStepRotate(controller: Controller, step: Double): Double {
        return if (abs(angle - controller.angle) > 180)
            getStepRotateIfAbsAngleMinusControllerAngleCompareTo180(controller, step)
        else
            getStepRotateIf180CompareToAbsAngleMinusControllerAngle(controller, step)
    }


    private fun getStepRotateIfAbsAngleMinusControllerAngleCompareTo180(controller: Controller, step: Double): Double {
        return if (angle > controller.angle)
            step
        else
            -step
    }

    private fun getStepRotateIf180CompareToAbsAngleMinusControllerAngle(controller: Controller, step: Double): Double {
        return if (angle < controller.angle)
            step
        else
            -step
    }

    fun moveForward(deltaTime: Double, controller: Controller) {
        val step = INCREASE_SPEED_PER_SECOND * deltaTime * controller.power

        speed += Vec(step * cos(angleToRadians), step * sin(angleToRadians))
    }

    override fun getBitmap(display: Display): Bitmap {
        return if (isFly)
            display.bmpSpaceshipFly[player]
        else
            display.bmpSpaceship[player]

    }

}