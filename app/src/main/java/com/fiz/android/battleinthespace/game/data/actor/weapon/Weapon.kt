package com.fiz.android.battleinthespace.game.data.actor.weapon

import com.fiz.android.battleinthespace.game.data.actor.MoveableActor
import com.fiz.android.battleinthespace.game.data.actor.SpaceShip
import com.fiz.android.battleinthespace.game.data.engine.Vec
import kotlin.math.sqrt

open abstract class Weapon(
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

    override fun update(deltaTime: Double, width: Double, height: Double) {
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

