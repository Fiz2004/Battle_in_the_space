package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.Rect
import android.graphics.RectF
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.models.BulletAnimationDestroy
import com.fiz.battleinthespace.feature_gamescreen.game.models.Meteorite
import com.fiz.battleinthespace.feature_gamescreen.game.models.SpaceShip
import com.fiz.battleinthespace.feature_gamescreen.game.models.SpaceShipAnimationDestroy
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
        val bulletsAnimationDestroy: List<BulletAnimationDestroy>,
        val spaceshipsAnimationDestroy: List<SpaceShipAnimationDestroy>,
        var spaceShips: List<SpaceShip>,
        var bullets: List<Weapon>,
        var meteorites: List<Meteorite>
) {
    companion object {

        private lateinit var display: Display

        fun setDisplay(display: Display) {
            this.display = display
        }

        fun getStateFromGame(game: Game): GameState {
            return GameState(
                    game.width,
                    game.height,
                    game.round,
                    backgroundsUi = getBackgroundsUI(game),
                    game.players,
                    game.countPlayers,
                    game.backgrounds.toList(),
                    game.listActors.bulletsAnimationDestroy.toList(),
                    game.listActors.spaceShipsAnimationDestroy.toList(),
                    game.listActors.spaceShips.toList(),
                    game.listActors.bullets.toList(),
                    game.listActors.meteorites.toList(),
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