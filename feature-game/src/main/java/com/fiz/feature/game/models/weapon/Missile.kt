package com.fiz.feature.game.models.weapon

import android.graphics.Bitmap
import com.fiz.battleinthespace.repositories.BitmapRepository
import com.fiz.feature.game.models.SpaceShip
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val SPEED_MAX: Double = 8.0

internal class Missile(
    center: com.fiz.battleinthespace.common.Vec,

    speed: com.fiz.battleinthespace.common.Vec,

    angle: Double,

    size: Double = 0.1,
    inGame: Boolean = true,
    roadLength: Double,

    player: Int
) : Weapon(
    center, speed, angle, size, inGame, roadLength, player, SPEED_MAX
), java.io.Serializable {
    override var roadLengthMax = 6.0

    companion object {

        fun create(spaceShips: List<SpaceShip>, player: Int): Weapon {
            val spaceship = spaceShips[player]
            val center = com.fiz.battleinthespace.common.Vec(
                spaceship.center.x + (spaceship.halfSize) * cos(spaceship.angleToRadians),
                spaceship.center.y + (spaceship.halfSize) * sin(spaceship.angleToRadians)
            )
            val speed = com.fiz.battleinthespace.common.Vec(
                SPEED_MAX * cos(spaceship.angleToRadians),
                SPEED_MAX * sin(spaceship.angleToRadians)
            )
            val angle = spaceship.angle
            val roadLength = 0.0
            val size = 0.2
            return Missile(center, speed, angle, size, true, roadLength, player)
        }
    }

    override fun update(deltaTime: Double, width: Int, height: Int) {
        super.update(deltaTime, width, height)
        roadLength += sqrt(speed.sumPow2()) * deltaTime
    }

    override fun getBitmap(bitmapRepository: BitmapRepository): Bitmap {
        return bitmapRepository.bmpWeapon[2]
    }

    override fun getType(): Int {
        return 2
    }
}