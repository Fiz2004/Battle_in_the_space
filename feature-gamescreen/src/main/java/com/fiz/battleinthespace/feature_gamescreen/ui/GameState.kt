package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.Rect
import android.graphics.RectF
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.BitmapRepository
import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.models.*
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon
import kotlin.math.ceil
import kotlin.math.floor

private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 7

data class BackgroundUi(
    val value: Int,
    val src: Rect,
    val dst: RectF
)

data class AnimationDestroyUi(
    val value: Int,
    val centerX: Double,
    val centerY: Double,
    val size: Double,
    val angle: Double
)

data class GameState(
    val width: Int,
    val height: Int,
    val round: Int,
    val backgroundsUi: List<BackgroundUi>,
    val bulletsAnimationDestroyUi: List<AnimationDestroyUi>,
    val spaceShipsAnimationDestroyUi: List<AnimationDestroyUi>,
    val players: MutableList<Player>,
    val countPlayers: Int = players.size,
    val backgrounds: List<List<Int>>,
    val bulletsAnimationDestroy: List<BulletAnimationDestroy>,
    val spaceshipsAnimationDestroy: List<SpaceShipAnimationDestroy>,
    var spaceShips: List<SpaceShip>,
    var bullets: List<Weapon>,
    var meteorites: List<Meteorite>
)

class GetGameStateFromGame(
    var surfaceWidth: Int, var surfaceHeight: Int,
    var gameWidth: Int, var gameHeight: Int,
    var sizeUnit: Float,
    val bitmapRepository: BitmapRepository
) {
    private var viewport: Viewport = Viewport(
        0,
        0,
        0,
        0,
        0f
    )

    fun setViewport(
        surfaceWidth: Int,
        surfaceHeight: Int,
        gameWidth: Int,
        gameHeight: Int,
        sizeUnit: Float
    ) {
        this.surfaceWidth = surfaceWidth
        this.surfaceHeight = surfaceHeight
        this.gameWidth = widthWorld
        this.gameHeight = gameHeight
        this.sizeUnit = sizeUnit
        viewport = Viewport(
            surfaceWidth,
            surfaceHeight,
            gameWidth,
            gameHeight,
            sizeUnit
        )
    }

    operator fun invoke(game: Game): GameState {
        viewport.update(game)

        return GameState(
            game.width,
            game.height,
            game.round,
            backgroundsUi = getBackgroundsUI(game),
            bulletsAnimationDestroyUi = getBulletsAnimationDestroyUi(game),
            spaceShipsAnimationDestroyUi = getSpaceshipsAnimationDestroyUi(game),
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

        val xStart = floor(viewport.left).toInt()
        val xEnd = ceil(viewport.left + viewport.width).toInt()
        val yStart = floor(viewport.top).toInt()
        val yEnd = ceil(viewport.top + viewport.height).toInt()

        val rectSrc = Rect(
            0,
            0,
            bitmapRepository.bmpBackground[0].width,
            bitmapRepository.bmpBackground[0].height
        )
        for (n in xStart until xEnd)
            for (k in yStart until yEnd) {
                val x = Physics.changeCoordinateIfBorderTop(n.toDouble(), Physics.width)
                val y = Physics.changeCoordinateIfBorderTop(k.toDouble(), Physics.height)
                val background = game.backgrounds[x.toInt()][y.toInt()]

                val xStartDst = (n - viewport.left).toFloat() * sizeUnit
                val yStartDst = (k - viewport.top).toFloat() * sizeUnit
                val rectDst = RectF(
                    xStartDst,
                    yStartDst,
                    xStartDst + sizeUnit,
                    yStartDst + sizeUnit
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

    private fun getBulletsAnimationDestroyUi(game: Game): List<AnimationDestroyUi> {
        val result = mutableListOf<AnimationDestroyUi>()
        for (animationDestroy in game.listActors.bulletsAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_BULLET_DESTROY
            animationDestroy.frame =
                NUMBER_BITMAP_BULLET_DESTROY - ceil(animationDestroy.timeShow / step).toInt()
            val actor = animationDestroy as Actor
            for (point in viewport.getAllPoints(actor))
                result.add(
                    AnimationDestroyUi(
                        value = animationDestroy.frame,
                        centerX = point.x,
                        centerY = point.y,
                        size = actor.size,
                        angle = actor.angle,
                    )
                )
        }
        return result
    }

    private fun getSpaceshipsAnimationDestroyUi(game: Game): List<AnimationDestroyUi> {
        val result = mutableListOf<AnimationDestroyUi>()
        for (animationDestroy in game.listActors.bulletsAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_SPACESHIP_DESTROY
            animationDestroy.frame =
                NUMBER_BITMAP_SPACESHIP_DESTROY - ceil(animationDestroy.timeShow / step).toInt()
            val actor = animationDestroy as Actor
            for (point in viewport.getAllPoints(actor))
                result.add(
                    AnimationDestroyUi(
                        value = animationDestroy.frame,
                        centerX = point.x,
                        centerY = point.y,
                        size = actor.size,
                        angle = actor.angle,
                    )
                )
        }
        return result
    }
}