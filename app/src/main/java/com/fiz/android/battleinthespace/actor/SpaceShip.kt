package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.Controller
import com.fiz.android.battleinthespace.engine.Vec
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val SPEED_ANGLE_PER_SECOND: Double = 200.0
private const val INCREASE_SPEED_PER_SECOND: Double = 1.2
private const val SPEED_MAX: Double = 2.0
private const val TIME_RESPAWN_MIN: Int = 1000

class SpaceShip(
    center: Vec,

    speed: Vec = Vec(0.0, 0.0),

    angle: Double,

    size: Double = 1.0,

    var inGame: Boolean = true,
    var isFly: Boolean = false
) : MoveableActor(
    center, speed, angle, size, SPEED_MAX
) {
    constructor (respawn: Respawn) : this(Vec(respawn.center), angle = respawn.angle)

    private var timeRespawnMin=TIME_RESPAWN_MIN
    private var timeRespawn: Int = TIME_RESPAWN_MIN

    fun isCanRespawn(deltaTime: Int): Boolean {
        timeRespawn -= deltaTime
        if (timeRespawn < 0) {
            timeRespawn = TIME_RESPAWN_MIN
            return true
        }
        return false
    }



    fun moveRotate(deltaTime: Int, controller: Controller) {
        val step = (SPEED_ANGLE_PER_SECOND * deltaTime / 1000)

        if (abs(angle - controller.angle) < step) {
            angle = controller.angle.toDouble()
            return
        }

        angle += getStepRotateIfABSAngleMinusControllerAngleCompareTo180(controller,step)
    }

    private fun getStepRotateIfABSAngleMinusControllerAngleCompareTo180(controller: Controller,step:Double):Double{
        return if (abs(angle - controller.angle)  > 180)
            getStepRotateIfAngleCompareToControllerAngle(controller,step)
        else
            getStepRotateIfControllerAngleCompareToAngle(controller,step)
    }


    private fun getStepRotateIfAngleCompareToControllerAngle(controller: Controller,step:Double):Double{
        return if (angle > controller.angle)
            step
        else
            -step
    }

    private fun getStepRotateIfControllerAngleCompareToAngle(controller: Controller,step:Double):Double{
        return if (angle < controller.angle)
            step
        else
            -step
    }

    fun moveForward(deltaTime: Int, controller: Controller) {
        val step = (INCREASE_SPEED_PER_SECOND * deltaTime / 1000) * controller.power

        speed += Vec(step * cos(angleToRadians), step * sin(angleToRadians))
    }

}