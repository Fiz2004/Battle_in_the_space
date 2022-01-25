package com.fiz.android.battleinthespace.actor

import android.graphics.Bitmap
import com.fiz.android.battleinthespace.Display
import com.fiz.android.battleinthespace.engine.Vec
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val SPEED_MAX: Double = 4.0

class Bullet(
    center: Vec,

    speed: Vec,

    angle: Double,

    size: Double = 0.1,

    var roadLength: Double,

    var player: Int,
) : MoveableActor(
    center, speed, angle, size, SPEED_MAX
) {
    companion object {
        val roadLengthMax = 6

        fun create(spaceShips: MutableList<SpaceShip>, player: Int): Bullet {
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
            return Bullet(center, speed, angle, 0.1, roadLength, player)
        }
    }

    override fun update(deltaTime: Double, width: Double, height: Double) {
        super.update(deltaTime, width, height)
        roadLength += sqrt(speed.sumPow2()) * deltaTime
    }

    override fun getBitmap(display: Display): Bitmap {
        return display.bmpBullet
    }
}