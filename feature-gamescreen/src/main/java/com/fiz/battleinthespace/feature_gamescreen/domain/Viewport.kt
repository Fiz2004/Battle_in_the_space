package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.common.Vec
import kotlin.math.ceil

class Viewport(
    surfaceWidth: Int, surfaceHeight: Int,
    stateGameWidth: Int, stateGameHeight: Int, sizeUnit: Float
) {
    var left: Double = 0.0
    var top: Double = 0.0

    val width: Double = (surfaceWidth / sizeUnit).toDouble()
    val height: Double = (surfaceHeight / sizeUnit).toDouble()

    private val levelWidth: Int = stateGameWidth
    private val levelHeight: Int = stateGameHeight

    private val marginX: Float = surfaceWidth / sizeUnit / 2
    private val marginY: Float = surfaceHeight / sizeUnit / 2

    private var widthAllViewportsOnScreen = run {
        val countViewportXOnScreen = ceil(width / levelWidth)
        levelWidth * countViewportXOnScreen
    }

    private var heightAllViewportsOnScreen = run {
        val countViewportYOnScreen = ceil(height / levelHeight)
        levelHeight * countViewportYOnScreen
    }

    fun update(game: com.fiz.feature.game.Game) {
        val spaceship =
            game.listActors.spaceShips[game.players.indexOf(game.players.find { it.main })]
        val center = spaceship.center
        left = center.x - marginX

        top = center.y - marginY
    }

    fun getAllPoints(actor: com.fiz.feature.game.models.Actor): MutableList<Vec> {
        val result = mutableListOf<Vec>()
        var relativeX =
            actor.center.x - widthAllViewportsOnScreen

        while (relativeX <= actor.center.x + widthAllViewportsOnScreen) {
            if (relativeX + actor.halfSize < left) {
                relativeX += levelWidth
                continue
            }
            if (relativeX - actor.halfSize > left + width) {
                break
            }
            var relativeY =
                actor.center.y - heightAllViewportsOnScreen
            while (relativeY <= actor.center.y + heightAllViewportsOnScreen) {
                if (relativeY + actor.halfSize < top) {
                    relativeY += levelHeight
                    continue
                }
                if (relativeY - actor.halfSize > top + height) {
                    break
                }

                result.add(Vec(relativeX - left, relativeY - top))

                relativeY += levelHeight
            }

            relativeX += levelWidth
        }
        return result
    }
}