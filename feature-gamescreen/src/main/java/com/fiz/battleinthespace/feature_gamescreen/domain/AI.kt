package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.models.Meteorite
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon
import java.io.Serializable
import kotlin.math.max
import kotlin.math.min

class AI : Serializable {

    fun update(index: Int, controller: Controller, game: Game) {
        controller.fire = true
        val spaceship = game.listActors.spaceShips[index]
        val center = spaceship.center
        if (game.listActors.meteorites.isNotEmpty()) {
            var minDistanceMeteorite: Meteorite = game.listActors.meteorites.first()
            var minDistance: Double = max(game.width.toDouble(), game.height.toDouble())
            for (meteorite in game.listActors.meteorites) {
                val distance = Physics.findDistance(center, meteorite.center)
                if (distance < minDistance) {
                    minDistance = distance
                    minDistanceMeteorite = meteorite
                }
            }
            val angle = Physics.findAngle(spaceship.center, minDistanceMeteorite.center)
            controller.angle = angle.toFloat()

            val indexWeapon = spaceship.player.weapon
            if (minDistance > Weapon.create(
                    game.listActors.spaceShips,
                    index,
                    indexWeapon
                ).roadLengthMax
            )
                controller.power = min((game.width / minDistance).toFloat(), 1F)
            else
                controller.power = 0F
        }
    }

}