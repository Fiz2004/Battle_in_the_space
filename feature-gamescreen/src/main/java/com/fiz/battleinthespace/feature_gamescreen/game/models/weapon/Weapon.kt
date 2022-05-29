package com.fiz.battleinthespace.feature_gamescreen.game.models.weapon

import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.game.models.MoveableActor
import com.fiz.battleinthespace.feature_gamescreen.game.models.SpaceShip
import kotlin.math.sqrt

abstract class Weapon(
    center: Vec,

    speed: Vec,

    angle: Double,

    size: Double = 0.1,
    inGame: Boolean = true,
    var roadLength: Double,

    var player: Int,

    speedMax: Double
) : MoveableActor(
    center, speed, angle, size, inGame, speedMax
) {

    open var roadLengthMax: Double = 6.0

    override fun update(deltaTime: Double, width: Int, height: Int) {
        super.update(deltaTime, width, height)
        roadLength += sqrt(speed.sumPow2()) * deltaTime
    }

    companion object {
        fun create(spaceShips: MutableList<SpaceShip>, player: Int, type: Int): Weapon {
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

