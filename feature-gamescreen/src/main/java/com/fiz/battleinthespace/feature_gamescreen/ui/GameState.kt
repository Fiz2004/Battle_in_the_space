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

data class SpriteUi(
    val value: Int,
    val centerX: Float,
    val centerY: Float,
    val src: Rect,
    val dst: RectF,
    val angle: Float
)

data class MeteoriteSpriteUi(
    val view: Int,
    val viewSize: Int,
    val centerX: Float,
    val centerY: Float,
    val src: Rect,
    val dst: RectF,
    val angle: Float
)

data class GameState(
    val width: Int,
    val height: Int,
    val round: Int,
    val backgroundsUi: List<BackgroundUi>,
    val spaceshipsUi: List<SpriteUi>,
    val spaceshipsFlyUi: List<SpriteUi>,
    val bulletsUi: List<SpriteUi>,
    val meteoritesUi: List<MeteoriteSpriteUi>,
    val bulletsAnimationsDestroyUi: List<SpriteUi>,
    val spaceShipsAnimationsDestroyUi: List<SpriteUi>,
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
            spaceshipsUi = getSpaceshipsUi(game),
            spaceshipsFlyUi = getSpaceshipsFlyUi(game),
            bulletsUi = getBulletsUi(game),
            meteoritesUi = getMeteoritesUi(game),
            bulletsAnimationsDestroyUi = getBulletsAnimationDestroyUi(game),
            spaceShipsAnimationsDestroyUi = getSpaceshipsAnimationDestroyUi(game),
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

    private fun getBulletsAnimationDestroyUi(game: Game): List<SpriteUi> {
        val result = mutableListOf<SpriteUi>()
        for (animationDestroy in game.listActors.bulletsAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_BULLET_DESTROY
            animationDestroy.frame =
                NUMBER_BITMAP_BULLET_DESTROY - ceil(animationDestroy.timeShow / step).toInt()
            val actor = animationDestroy as Actor
            for (point in viewport.getAllPoints(actor)) {

                val rectSrc = Rect(
                    0,
                    0,
                    bitmapRepository.bmpBulletDestroy.first().width,
                    bitmapRepository.bmpBulletDestroy.first().height
                )
                val halfSize = (actor.size * sizeUnit / 2).toFloat()
                val rectDst = RectF(
                    -halfSize, -halfSize,
                    halfSize, halfSize
                )

                result.add(
                    SpriteUi(
                        value = animationDestroy.frame,
                        centerX = (point.x * sizeUnit).toFloat(),
                        centerY = (point.y * sizeUnit).toFloat(),
                        src = rectSrc,
                        dst = rectDst,
                        angle = actor.angle.toFloat(),
                    )
                )
            }
        }
        return result
    }

    private fun getSpaceshipsAnimationDestroyUi(game: Game): List<SpriteUi> {
        val result = mutableListOf<SpriteUi>()
        for (animationDestroy in game.listActors.bulletsAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_SPACESHIP_DESTROY
            animationDestroy.frame =
                NUMBER_BITMAP_SPACESHIP_DESTROY - ceil(animationDestroy.timeShow / step).toInt()
            val actor = animationDestroy as Actor
            for (point in viewport.getAllPoints(actor)) {

                val rectSrc = Rect(
                    0,
                    0,
                    bitmapRepository.bmpSpaceshipDestroy.first().width,
                    bitmapRepository.bmpSpaceshipDestroy.first().height
                )
                val halfSize = (actor.size * sizeUnit / 2).toFloat()
                val rectDst = RectF(
                    -halfSize, -halfSize,
                    halfSize, halfSize
                )

                result.add(
                    SpriteUi(
                        value = animationDestroy.frame,
                        centerX = (point.x * sizeUnit).toFloat(),
                        centerY = (point.y * sizeUnit).toFloat(),
                        src = rectSrc,
                        dst = rectDst,
                        angle = actor.angle.toFloat(),
                    )
                )
            }
        }
        return result
    }

    private fun getSpaceshipsUi(game: Game): List<SpriteUi> {
        val result = mutableListOf<SpriteUi>()

        for (actor in game.listActors.spaceShips) {
            if (!actor.inGame || actor.isFly)
                continue
            for (point in viewport.getAllPoints(actor)) {
                val rectSrc = Rect(
                    0,
                    0,
                    bitmapRepository.bmpSpaceship.first().width,
                    bitmapRepository.bmpSpaceship.first().height
                )
                val halfSize = (actor.size * sizeUnit / 2).toFloat()
                val rectDst = RectF(
                    -halfSize, -halfSize,
                    halfSize, halfSize
                )

                result.add(
                    SpriteUi(
                        value = actor.player.number,
                        centerX = (point.x * sizeUnit).toFloat(),
                        centerY = (point.y * sizeUnit).toFloat(),
                        src = rectSrc,
                        dst = rectDst,
                        angle = actor.angle.toFloat(),
                    )
                )
            }
        }

        return result
    }

    private fun getSpaceshipsFlyUi(game: Game): List<SpriteUi> {
        val result = mutableListOf<SpriteUi>()

        for (actor in game.listActors.spaceShips) {
            if (!actor.inGame || !actor.isFly)
                continue
            for (point in viewport.getAllPoints(actor)) {
                val rectSrc = Rect(
                    0,
                    0,
                    bitmapRepository.bmpSpaceshipFly.first().width,
                    bitmapRepository.bmpSpaceshipFly.first().height
                )
                val halfSize = (actor.size * sizeUnit / 2).toFloat()
                val rectDst = RectF(
                    -halfSize, -halfSize,
                    halfSize, halfSize
                )

                result.add(
                    SpriteUi(
                        value = actor.player.number,
                        centerX = (point.x * sizeUnit).toFloat(),
                        centerY = (point.y * sizeUnit).toFloat(),
                        src = rectSrc,
                        dst = rectDst,
                        angle = actor.angle.toFloat(),
                    )
                )
            }
        }

        return result
    }

    private fun getBulletsUi(game: Game): List<SpriteUi> {
        val result = mutableListOf<SpriteUi>()

        for (actor in game.listActors.bullets) {
            for (point in viewport.getAllPoints(actor)) {
                val rectSrc = Rect(
                    0,
                    0,
                    bitmapRepository.bmpWeapon[actor.getType()].width,
                    bitmapRepository.bmpWeapon[actor.getType()].height
                )
                val halfSize = (actor.size * sizeUnit / 2).toFloat()
                val rectDst = RectF(
                    -halfSize, -halfSize,
                    halfSize, halfSize
                )

                result.add(
                    SpriteUi(
                        value = actor.getType(),
                        centerX = (point.x * sizeUnit).toFloat(),
                        centerY = (point.y * sizeUnit).toFloat(),
                        src = rectSrc,
                        dst = rectDst,
                        angle = actor.angle.toFloat(),
                    )
                )
            }
        }

        return result
    }

    private fun getMeteoritesUi(game: Game): List<MeteoriteSpriteUi> {
        val result = mutableListOf<MeteoriteSpriteUi>()

        for (actor in game.listActors.meteorites) {
            for (point in viewport.getAllPoints(actor)) {
                val rectSrc = Rect(
                    0,
                    0,
                    bitmapRepository.bmpMeteorites[actor.view][actor.viewSize].width,
                    bitmapRepository.bmpMeteorites[actor.view][actor.viewSize].height
                )
                val halfSize = (actor.size * sizeUnit / 2).toFloat()
                val rectDst = RectF(
                    -halfSize, -halfSize,
                    halfSize, halfSize
                )

                result.add(
                    MeteoriteSpriteUi(
                        view = actor.view,
                        viewSize = actor.viewSize,
                        centerX = (point.x * sizeUnit).toFloat(),
                        centerY = (point.y * sizeUnit).toFloat(),
                        src = rectSrc,
                        dst = rectDst,
                        angle = actor.angle.toFloat(),
                    )
                )
            }
        }

        return result
    }
}