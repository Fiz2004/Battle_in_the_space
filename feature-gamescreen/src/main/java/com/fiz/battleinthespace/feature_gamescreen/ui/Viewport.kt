package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.feature_gamescreen.data.actor.Actor
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Vec
import kotlin.math.ceil

class Viewport(
    surfaceWidth: Int, surfaceHeight: Int,
    stateGameWidth: Double, stateGameHeight: Double, sizeUnit: Float
) {
    var left: Double = 0.0
    var top: Double = 0.0

    val width: Double = (surfaceWidth / sizeUnit).toDouble()
    val height: Double = (surfaceHeight / sizeUnit).toDouble()

    private val levelWidth: Double = stateGameWidth
    private val levelHeight: Double = stateGameHeight

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

    fun update(stateGame: GameState) {
        val spaceship =
            stateGame.level.listActors.spaceShips[stateGame.players.indexOf(stateGame.players.find { it.main })]
        val center = spaceship.center

        left = center.x - marginX

        top = center.y - marginY
    }

    fun getAllPoints(actor: Actor): MutableList<Vec> {
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