package com.fiz.android.battleinthespace.game.domain

import android.view.SurfaceView
import com.fiz.android.battleinthespace.game.data.actor.Actor
import com.fiz.android.battleinthespace.game.data.engine.Vec
import kotlin.math.ceil

class Viewport(surface: SurfaceView, stateGame: StateGame, sizeUnit: Float) {
    var left: Double = 0.0
    var top: Double = 0.0
    var width: Double = 0.0
    var height: Double = 0.0
    private var levelWidth: Double = 0.0
    private var levelHeight: Double = 0.0
    private var widthAllViewportsOnScreen = 0.0
    private var heightAllViewportsOnScreen = 0.0

    val marginX: Float
    val marginY: Float

    init {
        width = (surface.width / sizeUnit).toDouble()
        height = (surface.height / sizeUnit).toDouble()

        levelWidth = stateGame.level.width
        levelHeight = stateGame.level.height

        marginX = surface.width / sizeUnit / 2
        marginY = surface.height / sizeUnit / 2

        val countViewportXOnScreen = ceil(width / levelWidth)
        val countViewportYOnScreen = ceil(height / levelHeight)

        widthAllViewportsOnScreen = levelWidth * countViewportXOnScreen
        heightAllViewportsOnScreen = levelHeight * countViewportYOnScreen
    }

    fun update(stateGame: StateGame) {
        val spaceship =
            stateGame.level.listActors.spaceShips[stateGame.playerGames.indexOf(stateGame.playerGames.find { it.main })]
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
