package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon
import kotlin.math.min

class AI {

    fun getNewController(index: Int, game: Game): Controller {
        val newController = Controller()

        val spaceship = game.listActors.spaceShips[index]
        val center = spaceship.center

        val (minDistanceMeteorite, minDistance) = game.listActors.meteorites.toMutableList()
            .map {
                Pair(it, Physics.findDistance(center, it.center))
            }
            .minByOrNull {
                it.second
            }
            ?: return newController

        val angle = Physics.findAngle(center, minDistanceMeteorite.center)
        newController.angle = angle

        val indexWeapon = spaceship.player.weapon

        if (minDistance > Weapon.create(
                game.listActors.spaceShips,
                index,
                indexWeapon
            ).roadLengthMax
        ) {
            newController.power = min(game.width / minDistance, 1.0)
            newController.fire = false
        } else {
            newController.power = 0.0
            newController.fire = true
        }

        return newController
    }
}