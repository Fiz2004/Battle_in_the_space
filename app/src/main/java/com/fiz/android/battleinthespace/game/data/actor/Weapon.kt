package com.fiz.android.battleinthespace.game.data.actor

import android.graphics.Bitmap
import com.fiz.android.battleinthespace.game.data.engine.Vec
import com.fiz.android.battleinthespace.game.domain.Display
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val SPEED_MAX: Double = 4.0

class Weapon(
    center: Vec,

    speed: Vec,

    angle: Double,

    size: Double = 0.1,
    inGame: Boolean = true,
    var roadLength: Double,

    var player: Int,
    var type: Int = 0
) : MoveableActor(
    center, speed, angle, size, inGame, SPEED_MAX
) {
    companion object {
        const val roadLengthMax = 6

        fun create(spaceShips: MutableList<SpaceShip>, player: Int, type: Int): Weapon {
            val spaceship = spaceShips[player]
            val center = Vec(
                spaceship.center.x + (spaceship.halfSize) * cos(spaceship.angleToRadians),
                spaceship.center.y + (spaceship.halfSize) * sin(spaceship.angleToRadians)
            )
            val speed = Vec(
                SPEED_MAX * cos(spaceship.angleToRadians),
                SPEED_MAX * sin(spaceship.angleToRadians)
            )
            val angle = spaceship.angle
            val roadLength = 0.0
            val size = 0.1 * type
            return Weapon(center, speed, angle, size, true, roadLength, player, type)
        }
    }

    override fun update(deltaTime: Double, width: Double, height: Double) {
        super.update(deltaTime, width, height)
        roadLength += sqrt(speed.sumPow2()) * deltaTime
    }

    override fun getBitmap(display: Display): Bitmap {
        return display.bmpWeapon[type]
    }
}