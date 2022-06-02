package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.BitmapRepository
import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.game.models.*
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon
import kotlin.math.*

private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 7

data class HelperPlayerUi(
    val value: Int,
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val color: Int
)

data class HelperMeteoritesUi(
    val centerX: Float,
    val centerY: Float,
    val radius: Float
)

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

data class TextInfoUi(
    val value: String,
    val x: Float,
    val y: Float,
    val textSize: Float,
    val color: Int
)

data class InfoUi(
    val value: Int,
    val src: Rect,
    val dst: RectF
)

data class GameState(
    val round: Int,
    val backgroundsUi: List<BackgroundUi>,
    val spaceshipsUi: List<SpriteUi>,
    val spaceshipsFlyUi: List<SpriteUi>,
    val bulletsUi: List<SpriteUi>,
    val meteoritesUi: List<MeteoriteSpriteUi>,
    val bulletsAnimationsDestroyUi: List<SpriteUi>,
    val spaceShipsAnimationsDestroyUi: List<SpriteUi>,
    val helpersPlayerUi: List<HelperPlayerUi>,
    val helpersMeteoriteUi: List<HelperMeteoritesUi>,
    val textsInfoUi: List<TextInfoUi>,
    val infoUi: List<InfoUi>,
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
    var InfoWidth: Int, var InfoHeight: Int,
    var gameWidth: Int, var gameHeight: Int,
    var sizeUnit: Float,
    val bitmapRepository: BitmapRepository
) {

    val paint = Paint()

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
            game.round,
            backgroundsUi = getBackgroundsUI(game),
            spaceshipsUi = getSpaceshipsUi(game),
            spaceshipsFlyUi = getSpaceshipsFlyUi(game),
            bulletsUi = getBulletsUi(game),
            meteoritesUi = getMeteoritesUi(game),
            bulletsAnimationsDestroyUi = getBulletsAnimationDestroyUi(game),
            spaceShipsAnimationsDestroyUi = getSpaceshipsAnimationDestroyUi(game),
            helpersPlayerUi = getHelperPlayerUi(game),
            helpersMeteoriteUi = getHelperMeteoritesUi(game),
            textsInfoUi = getInfoTextUi(game),
            infoUi = getInfoUi(game),
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

    fun getHelperPlayerUi(game: Game): List<HelperPlayerUi> {

        val result = mutableListOf<HelperPlayerUi>()

        val mainSpaceship = game.listActors.spaceShips.filter { it.player.main }
        if (mainSpaceship.isNotEmpty()) {
            game.listActors.spaceShips.filter { it.inGame && !it.player.main }
                .forEach {
                    if (viewport.getAllPoints(it).size == 0) {

                        val angle = Physics.findAngle(mainSpaceship[0].center, it.center)
                        val angleToRadians = angle / 180.0 * Math.PI
                        val vec = Vec(cos(angleToRadians), sin(angleToRadians))
                        val cx =
                            (mainSpaceship[0].center.x - viewport.left + vec.x * viewport.width / 2).toFloat() * sizeUnit - 60F
                        val cy =
                            (mainSpaceship[0].center.y - viewport.top + vec.y * viewport.height / 2).toFloat() * sizeUnit - 60F

                        result.add(
                            HelperPlayerUi(
                                value = it.player.number,
                                centerX = cx,
                                centerY = cy,
                                radius = 30F,
                                color = getColor(it.player.number)
                            )
                        )
                    }
                }

        }

        return result
    }

    fun getHelperMeteoritesUi(game: Game): List<HelperMeteoritesUi> {

        val result = mutableListOf<HelperMeteoritesUi>()

        val mainSpaceship = game.listActors.spaceShips.filter { it.player.main }
        if (mainSpaceship.isNotEmpty()) {

            if (game.listActors.meteorites.all { (viewport.getAllPoints(it).size == 0) })
                game.listActors.meteorites.forEach {

                    val angle = Physics.findAngle(mainSpaceship[0].center, it.center)
                    val angleToRadians = angle / 180.0 * Math.PI
                    val vec = Vec(cos(angleToRadians), sin(angleToRadians))
                    val cx =
                        (mainSpaceship[0].center.x - viewport.left + vec.x * viewport.width / 2).toFloat() * sizeUnit - 40F
                    val cy =
                        (mainSpaceship[0].center.y - viewport.top + vec.y * viewport.height / 2).toFloat() * sizeUnit - 40F

                    result.add(
                        HelperMeteoritesUi(
                            centerX = cx,
                            centerY = cy,
                            radius = 30F
                        )
                    )
                }
        }

        return result
    }

    private fun getInfoTextUi(game: Game): List<TextInfoUi> {
        val result = mutableListOf<TextInfoUi>()

        val width = InfoWidth
        val height = InfoHeight
        val minCharacteristic = min(width, height)
        val bmplife = minCharacteristic / 3 / 3

        val baseTextSize = minCharacteristic / 6F
        val paintRoundTextSize = baseTextSize

        result.add(
            TextInfoUi(
                value = "Round ${game.round}",
                x = InfoWidth / 2F,
                y = baseTextSize,
                textSize = paintRoundTextSize,
                color = Color.WHITE
            )
        )

        val textSize = baseTextSize * 0.75F
        val maxTextWidth = getMaxTextWidth(game.players, textSize)

        for (n in 0 until game.players.size) {
            result.add(
                TextInfoUi(
                    value = game.players[n].name,
                    x = 0F,
                    y = baseTextSize + (textSize * (n + 1)),
                    textSize = textSize,
                    color = getColor(n)
                )
            )

            result.add(
                TextInfoUi(
                    value = game.players[n].score.toString(),
                    x = (maxTextWidth + bmplife * 3).toFloat(),
                    y = baseTextSize + textSize * (n + 1),
                    textSize = textSize,
                    color = getColor(n)
                )
            )
        }

        return result

    }


    private fun getInfoUi(game: Game): List<InfoUi> {
        val result = mutableListOf<InfoUi>()

        val width = InfoWidth
        val height = InfoHeight
        val minCharacteristic = min(width, height)
        val bmplife = minCharacteristic / 3 / 3

        val rectSrc = Rect(
            0, 0,
            bitmapRepository.bmpSpaceshipLife[0].width, bitmapRepository.bmpSpaceshipLife[0].height
        )

        val baseTextSize = minCharacteristic / 6F

        val textSize = baseTextSize * 0.75F
        val maxTextWidth = getMaxTextWidth(game.players, textSize)

        for (n in 0 until game.players.size) {

            for (k in 0 until game.players[n].life)
                result.add(
                    InfoUi(
                        value = n,
                        src = rectSrc,
                        dst = RectF(
                            maxTextWidth + bmplife * k.toFloat(),
                            baseTextSize + textSize * n,
                            (maxTextWidth + bmplife * (k + 1) + bmplife).toFloat(),
                            baseTextSize + textSize * n + textSize
                        )
                    )
                )
        }

        return result

    }

    private fun getMaxTextWidth(players: MutableList<Player>, textSize: Float): Int {
        var result = 0
        paint.textSize = textSize
        for (namePlayer in players) {
            val textWidth = paint.measureText(namePlayer.name).toInt()
            result = max(textWidth, result)
        }
        return result
    }

    private fun getColor(n: Int) = when (n) {
        0 -> Color.GREEN
        1 -> Color.CYAN
        // Color = pink
        2 -> Color.rgb(255, 192, 203)
        3 -> Color.YELLOW
        else -> Color.WHITE
    }

    fun setInfo(infoWidth: Int, infoHeight: Int) {
        this.InfoWidth = infoWidth
        this.InfoHeight = infoHeight
    }
}