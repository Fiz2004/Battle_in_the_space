package com.fiz.feature.game.models

import android.graphics.Bitmap
import com.fiz.battleinthespace.common.Vec
import com.fiz.battleinthespace.domain.models.Controller
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.repositories.BitmapRepository
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign
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

    inGame: Boolean = true,
    var isFly: Boolean = false,
    val player: Player
) : MoveableActor(
    center, speed, angle, size, inGame, SPEED_MAX
), java.io.Serializable {
    constructor (respawn: Respawn, playerGame: Player) : this(
        center = Vec(respawn.center),
        player = playerGame,
        angle = respawn.angle
    )

    private var timeRespawn: Double = TIME_RESPAWN_MIN

    fun respawn(respawn: Respawn) {
        center = respawn.center.copy()
        speed = Vec(0.0, 0.0)
        angle = respawn.angle
        inGame = true
        isFly = false
    }

    fun isCanRespawnFromTime(deltaTime: Double): Boolean {
        timeRespawn -= deltaTime
        if (timeRespawn < 0) {
            timeRespawn = TIME_RESPAWN_MIN
            return true
        }
        return false
    }

    fun moveRotate(deltaTime: Double, needAngle: Double) {
        val step = SPEED_ANGLE_PER_SECOND * deltaTime

        if (abs(angle - needAngle) < step || abs(angle - needAngle) > 360 - step) {
            angle = needAngle
            return
        }

        angle += getSignStepRotate(needAngle) * step
    }

    private fun getSignStepRotate(needAngle: Double): Double {
        return if (abs(angle - needAngle) > 180)
            getSignIfCounterClockwise(needAngle)
        else
            getSignIfClockwise(needAngle)
    }


    private fun getSignIfCounterClockwise(needAngle: Double): Double {
        return sign(angle - needAngle)
    }

    private fun getSignIfClockwise(needAngle: Double): Double {
        return sign(needAngle - angle)
    }

    fun moveForward(deltaTime: Double, controller: Controller) {
        val step = INCREASE_SPEED_PER_SECOND * deltaTime * controller.power
        isFly = controller.power != 0.0

        speed += Vec(step * cos(angleToRadians), step * sin(angleToRadians))
    }

    override fun getBitmap(bitmapRepository: BitmapRepository): Bitmap {
        return if (isFly)
            bitmapRepository.bmpSpaceshipFly[player.number]
        else
            bitmapRepository.bmpSpaceship[player.number]

    }

}