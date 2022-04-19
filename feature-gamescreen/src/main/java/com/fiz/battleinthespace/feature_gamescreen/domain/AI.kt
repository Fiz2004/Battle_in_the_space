package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.feature_gamescreen.data.actor.Meteorite
import com.fiz.battleinthespace.feature_gamescreen.data.actor.weapon.Weapon
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Physics
import kotlin.math.max
import kotlin.math.min

class AI(private var stateGame: StateGame) {
    fun update(controller: Controller) {
        controller.fire = true
        val spaceship = stateGame.level.listActors.spaceShips[controller.playerGame.number]
        val center = spaceship.center
        if (stateGame.level.listActors.meteorites.isNotEmpty()) {
            var minDistanceMeteorite: Meteorite = stateGame.level.listActors.meteorites.first()
            var minDistance: Double = max(stateGame.level.width, stateGame.level.height)
            for (meteorite in stateGame.level.listActors.meteorites) {
                val distance = Physics.findDistance(center, meteorite.center)
                if (distance < minDistance) {
                    minDistance = distance
                    minDistanceMeteorite = meteorite
                }
            }
            val angle = Physics.findAngle(spaceship.center, minDistanceMeteorite.center)
            controller.angle = angle.toFloat()

            val indexWeapon = spaceship.playerGame.weapon
            if (minDistance > Weapon.create(
                        stateGame.level.listActors.spaceShips,
                        controller.playerGame.number,
                        indexWeapon).roadLengthMax)
                controller.power = min((stateGame.level.width / minDistance).toFloat(), 1F)
            else
                controller.power = 0F
        }
    }


}