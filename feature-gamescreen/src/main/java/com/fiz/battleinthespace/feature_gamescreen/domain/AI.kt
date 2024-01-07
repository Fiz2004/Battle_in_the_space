package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.domain.models.Controller
import com.fiz.feature.game.Game
import com.fiz.feature.game.models.weapon.Weapon
import kotlin.math.min

internal class AI {

    fun getNewController(index: Int, game: Game): Controller {
        val newController = Controller()

        val spaceship = game.listActors.spaceShips[index]
        val center = spaceship.center

        val (minDistanceMeteorite, minDistance) = game.listActors.meteorites.toMutableList()
            .map {
                Pair(it, com.fiz.feature.game.engine.Physics.findDistance(center, it.center))
            }
            .minByOrNull {
                it.second
            }
            ?: return newController

        val angle =
            com.fiz.feature.game.engine.Physics.findAngle(center, minDistanceMeteorite.center)
        newController.angle = angle

        val indexWeapon = spaceship.weapon

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