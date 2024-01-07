package com.fiz.feature.game.models.weapon

import com.fiz.feature.game.models.MoveableActor
import com.fiz.feature.game.models.SpaceShip
import kotlin.math.sqrt

abstract class Weapon(
    center: com.fiz.battleinthespace.common.Vec,

    speed: com.fiz.battleinthespace.common.Vec,

    angle: Double,

    size: Double = 0.1,
    inGame: Boolean = true,
    var roadLength: Double,

    var player: Int,

    speedMax: Double
) : MoveableActor(
    center, speed, angle, size, inGame, speedMax
), java.io.Serializable {

    open var roadLengthMax: Double = 6.0

    override fun update(deltaTime: Double, width: Int, height: Int) {
        super.update(deltaTime, width, height)
        roadLength += sqrt(speed.sumPow2()) * deltaTime
    }

    open fun getType(): Int {
        return 0
    }

    companion object {
        fun create(spaceShips: List<SpaceShip>, player: Int, type: Int): Weapon {
            return when (type) {
                0 -> OneBullet.create(spaceShips, player)
                1 -> TwoBullet.create(spaceShips, player)
                2 -> Missile.create(spaceShips, player)
                3 -> Ball.create(spaceShips, player)
                else -> throw Error("Нет такого типа оружия")
            }
        }
    }
}

