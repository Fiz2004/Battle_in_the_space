package com.fiz.android.battleinthespace.game

import com.fiz.android.battleinthespace.actor.Bullet
import com.fiz.android.battleinthespace.actor.Meteorite
import com.fiz.android.battleinthespace.engine.Physics
import kotlin.math.max
import kotlin.math.min

class AI(private var state: State) {
    fun update(controller: Controller) {
        controller.fire = true
        val spaceship = state.level.listActors.spaceShips[controller.player.number]
        val center = spaceship.center
        if (state.level.listActors.meteorites.isNotEmpty()) {
            var minDistanceMeteorite: Meteorite = state.level.listActors.meteorites.first()
            var minDistance: Double = max(state.level.width, state.level.height)
            for (meteorite in state.level.listActors.meteorites) {
                val distance = Physics.findDistance(center, meteorite.center)
                if (distance < minDistance) {
                    minDistance = distance
                    minDistanceMeteorite = meteorite
                }
            }
            val angle = Physics.findAngle(spaceship.center, minDistanceMeteorite.center)
            controller.angle = angle.toFloat()

            if (minDistance > Bullet.roadLengthMax)
                controller.power = min((state.level.width / minDistance).toFloat(), 1F)
            else
                controller.power = 0F
        }
    }


}