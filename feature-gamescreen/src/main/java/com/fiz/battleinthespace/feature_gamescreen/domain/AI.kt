package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.feature_gamescreen.data.actor.Meteorite
import com.fiz.battleinthespace.feature_gamescreen.data.actor.weapon.Weapon
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Physics
import java.io.Serializable
import kotlin.math.max
import kotlin.math.min

class AI : Serializable {

    fun update(index: Int, controller: Controller, level: Level) {
        controller.fire = true
        val spaceship = level.listActors.spaceShips[index]
        val center = spaceship.center
        if (level.listActors.meteorites.isNotEmpty()) {
            var minDistanceMeteorite: Meteorite = level.listActors.meteorites.first()
            var minDistance: Double = max(level.width.toDouble(), level.height.toDouble())
            for (meteorite in level.listActors.meteorites) {
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
                    level.listActors.spaceShips,
                    index,
                    indexWeapon
                ).roadLengthMax
            )
                controller.power = min((level.width / minDistance).toFloat(), 1F)
            else
                controller.power = 0F
        }
    }

}