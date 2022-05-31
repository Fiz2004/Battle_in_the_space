package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.Rect
import android.graphics.RectF
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.models.DrawableAnimationDestroy
import com.fiz.battleinthespace.feature_gamescreen.game.models.Meteorite
import com.fiz.battleinthespace.feature_gamescreen.game.models.MoveableActor
import com.fiz.battleinthespace.feature_gamescreen.game.models.SpaceShip
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon
import kotlin.math.ceil
import kotlin.math.floor

data class BackgroundUi(
    val value: Int,
    val src: Rect,
    val dst: RectF
)

data class GameState(
    val width: Int,
    val height: Int,
    val round: Int,
    val backgroundsUi: List<BackgroundUi>,
    val players: MutableList<Player>,
    val countPlayers: Int = players.size,
    val backgrounds: List<List<Int>>,
    val actors: MutableList<MoveableActor>,
    val listAnimationDestroy: MutableList<DrawableAnimationDestroy>,
    var spaceShips: MutableList<SpaceShip>,
    var bullets: MutableList<Weapon>,
    var meteorites: MutableList<Meteorite>
) {
    companion object {

        private lateinit var display: Display

        fun setDisplay(display: Display) {
            this.display = display
        }

        fun getStateFromGame(game: Game): GameState {
            if (game.listActors.listAnimationDestroy.any { it.timeShow <= 0 }) {
                var a = 1
                a += 1
                println(a)
            }
            if (game.listActors.listAnimationDestroy.toMutableList().any { it.timeShow <= 0 }) {
                var a = 1
                a += 1
                println(a)
            }
            return GameState(
                game.width,
                game.height,
                game.round,
                backgroundsUi = getBackgroundsUI(game),
                game.players,
                game.countPlayers,
                game.backgrounds.toList(),
                game.listActors.actors.toMutableList(),
                game.listActors.listAnimationDestroy.toMutableList(),
                game.listActors.spaceShips.toMutableList(),
                game.listActors.bullets.toMutableList(),
                game.listActors.meteorites.toMutableList(),
            )
        }

        private fun getBackgroundsUI(game: Game): List<BackgroundUi> {
            val result = mutableListOf<BackgroundUi>()

            if (!this::display.isInitialized) return result

            val xStart = floor(display.viewport.left).toInt()
            val xEnd = ceil(display.viewport.left + display.viewport.width).toInt()
            val yStart = floor(display.viewport.top).toInt()
            val yEnd = ceil(display.viewport.top + display.viewport.height).toInt()

            val rectSrc = Rect(
                0,
                0,
                display.bitmapRepository.bmpBackground[0].width,
                display.bitmapRepository.bmpBackground[0].height
            )
            for (n in xStart until xEnd)
                for (k in yStart until yEnd) {
                    val x = Physics.changeCoordinateIfBorderTop(n.toDouble(), Physics.width)
                    val y = Physics.changeCoordinateIfBorderTop(k.toDouble(), Physics.height)
                    val background = game.backgrounds[x.toInt()][y.toInt()]

                    val xStartDst = (n - display.viewport.left).toFloat() * display.sizeUnit
                    val yStartDst = (k - display.viewport.top).toFloat() * display.sizeUnit
                    val rectDst = RectF(
                        xStartDst,
                        yStartDst,
                        xStartDst + display.sizeUnit,
                        yStartDst + display.sizeUnit
                    )
                    result.add(
                        BackgroundUi(
                            value = background,
                            src = rectSrc,
                            dst = rectDst
                        )
                    )
                }
            return result
        }
    }
}