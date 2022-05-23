package com.fiz.battleinthespace.feature_gamescreen.data.actor.weapon

import android.graphics.Bitmap
import com.fiz.battleinthespace.feature_gamescreen.data.actor.SpaceShip
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.ui.Display
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val SPEED_MAX: Double = 8.0

class Missile(
    center: Vec,

    speed: Vec,

    angle: Double,

    size: Double = 0.1,
    inGame: Boolean = true,
    roadLength: Double,

    player: Int
) : Weapon(
    center, speed, angle, size, inGame, roadLength, player, SPEED_MAX
) {
    override var roadLengthMax = 6.0

    companion object {

        fun create(spaceShips: MutableList<SpaceShip>, player: Int): Weapon {
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
            val size = 0.2
            return Missile(center, speed, angle, size, true, roadLength, player)
        }
    }

    override fun update(deltaTime: Double, width: Double, height: Double) {
        super.update(deltaTime, width, height)
        roadLength += sqrt(speed.sumPow2()) * deltaTime
    }

    override fun getBitmap(display: Display): Bitmap {
        return display.bitmapRepository.bmpWeapon[2]
    }
}