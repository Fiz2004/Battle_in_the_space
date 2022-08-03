package com.fiz.battleinthespace.feature_gamescreen.domain

import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import com.fiz.battleinthespace.common.Vec
import com.fiz.battleinthespace.feature_gamescreen.ui.GameState
import com.fiz.battleinthespace.feature_gamescreen.ui.WIDTH_WORLD
import com.fiz.battleinthespace.feature_gamescreen.ui.models.*
import com.fiz.battleinthespace.repositories.BitmapRepository
import com.fiz.feature.game.Game
import com.fiz.feature.game.engine.Physics
import com.fiz.feature.game.models.Actor
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 7

@Singleton
class GetGameStateFromGame @Inject constructor(
    private val bitmapRepository: BitmapRepository
) {
    private var surfaceWidth: Int = 0
    private var surfaceHeight: Int = 0
    var infoWidth: Int = 0
    var infoHeight: Int = 0
    private var gameWidth: Int = 0
    private var gameHeight: Int = 0
    private var sizeUnit: Float = 0f
    private var bmplife: Int = 0
    private var baseTextSize: Float = 0f
    private var maxTextNameWidth: Int = 0

    private var viewport: Viewport = Viewport(
        0,
        0,
        0,
        0,
        0f
    )

    fun setInfoScreen(
        infoWidth: Int,
        infoHeight: Int,
    ) {
        this.infoWidth = infoWidth
        this.infoHeight = infoHeight
    }

    fun setInfo(
        bmpLife: Int,
        baseTextSize: Float,
        maxTextNameWidth: Int
    ) {
        this.bmplife = bmpLife
        this.baseTextSize = baseTextSize
        this.maxTextNameWidth = maxTextNameWidth
    }

    fun setViewport(
        surfaceWidth: Int,
        surfaceHeight: Int,
        gameWidth: Int,
        gameHeight: Int,
        sizeUnit: Float
    ) {
        this.surfaceWidth = surfaceWidth
        this.surfaceHeight = surfaceHeight
        this.gameWidth = WIDTH_WORLD
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
            round = game.round,
            status = game.status,
            backgroundsUi = getBackgroundsUI(game),
            spaceshipsUi = getSpaceshipsUi(game),
            spaceshipsFlyUi = getSpaceshipsFlyUi(game),
            bulletsUi = getBulletsUi(game),
            meteoritesUi = getMeteoritesUi(game),
            bulletsAnimationsDestroyUi = getBulletsAnimationDestroyUi(game),
            spaceShipsAnimationsDestroyUi = getSpaceshipsAnimationDestroyUi(game),
            helpersPlayerUi = getHelperPlayerUi(game),
            helpersMeteoriteUi = getHelperMeteoritesUi(game),
            textRoundInfoUi = getTextRoundInfoUi(game),
            textsInfoUi = getInfoTextUi(game),
            infoUi = getInfoUi(game),
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
                val x = Physics.changeCoordinateIfBorderTop(
                    n.toDouble(),
                    Physics.width
                )
                val y = Physics.changeCoordinateIfBorderTop(
                    k.toDouble(),
                    Physics.height
                )
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
        for (animationDestroy in game.listActors.spaceShipsAnimationDestroy) {
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
                        value = actor.number,
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
                        value = actor.number,
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
        return game.listActors.weapons.map{ weapon->
            viewport.getAllPoints(weapon).map { point->
                val rectSrc = Rect(
                    0,
                    0,
                    bitmapRepository.bmpWeapon[weapon.getType()].width,
                    bitmapRepository.bmpWeapon[weapon.getType()].height
                )
                val halfSize = (weapon.size * sizeUnit / 2).toFloat()
                val rectDst = RectF(
                    -halfSize, -halfSize,
                    halfSize, halfSize
                )

                    SpriteUi(
                        value = weapon.getType(),
                        centerX = (point.x * sizeUnit).toFloat(),
                        centerY = (point.y * sizeUnit).toFloat(),
                        src = rectSrc,
                        dst = rectDst,
                        angle = weapon.angle.toFloat(),
                    )
            }
        }.flatten()
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

    private fun getHelperPlayerUi(game: Game): List<HelperPlayerUi> {

        val result = mutableListOf<HelperPlayerUi>()

        val mainSpaceship = game.listActors.spaceShips.first()

        if (!mainSpaceship.inGame) return emptyList()

        game.listActors.spaceShips
            .filterIndexed { index, spaceShip -> spaceShip.inGame && index != 0 }
            .forEach {
                if (viewport.getAllPoints(it).isEmpty()) {

                    val angle = Physics.findAngle(
                        mainSpaceship.center,
                        it.center
                    )
                    val angleToRadians = Physics.getAngleToRadians(angle)
                    val vec = Vec(
                        cos(angleToRadians),
                        sin(angleToRadians)
                    )
                    val cx =
                        (mainSpaceship.center.x - viewport.left + vec.x * (viewport.width - 1) / 2).toFloat() * sizeUnit
                    val cy =
                        (mainSpaceship.center.y - viewport.top + vec.y * (viewport.height - 1) / 2).toFloat() * sizeUnit

                    result.add(
                        HelperPlayerUi(
                            value = it.number,
                            centerX = cx,
                            centerY = cy,
                            angle = angle.toFloat(),
                            color = getColor(it.number)
                        )
                    )
                }
            }

        return result
    }

    private fun getHelperMeteoritesUi(game: Game): List<HelperMeteoritesUi> {

        val result = mutableListOf<HelperMeteoritesUi>()

        val mainSpaceship = game.listActors.spaceShips.first()

        if (!mainSpaceship.inGame) return emptyList()

        if (game.listActors.meteorites.all { (viewport.getAllPoints(it).isEmpty()) })
            game.listActors.meteorites.forEach {

                val angle = Physics.findAngle(
                    mainSpaceship.center,
                    it.center
                )
                val angleToRadians = Physics.getAngleToRadians(angle)
                val vec =
                    Vec(cos(angleToRadians), sin(angleToRadians))
                val cx =
                    (mainSpaceship.center.x - viewport.left + vec.x * (viewport.width - 1) / 2).toFloat() * sizeUnit
                val cy =
                    (mainSpaceship.center.y - viewport.top + vec.y * (viewport.height - 1) / 2).toFloat() * sizeUnit

                result.add(
                    HelperMeteoritesUi(
                        centerX = cx,
                        centerY = cy,
                        angle = angle.toFloat(),
                    )
                )
            }

        return result
    }

    private fun getTextRoundInfoUi(game: Game): TextInfoUi {

        val paintRoundTextSize = baseTextSize

        return TextInfoUi(
            value = "Round ${game.round}",
            x = infoWidth / 2F,
            y = baseTextSize,
            textSize = paintRoundTextSize,
            color = Color.WHITE
        )

    }

    private fun getInfoTextUi(game: Game): List<TextInfoUi> {
        val result = mutableListOf<TextInfoUi>()

        val textSize = baseTextSize * 0.75F

        val isGorisontal = 2 * infoHeight > infoWidth

        for (n in 0 until game.players.size) {
            val left = if (isGorisontal)
                0f
            else
                (n / 2) * (infoWidth / 2).toFloat()

            val top = if (isGorisontal)
                baseTextSize + (textSize * (n + 1))
            else
                baseTextSize + (textSize * (n % 2 + 1))


            result.add(
                TextInfoUi(
                    value = game.players[n].name,
                    x = left,
                    y = top,
                    textSize = textSize,
                    color = getColor(n)
                )
            )

            result.add(
                TextInfoUi(
                    value = game.listActors.spaceShips[n].score.toString(),
                    x = left + (maxTextNameWidth + bmplife * 3).toFloat(),
                    y = top,
                    textSize = textSize,
                    color = getColor(n)
                )
            )
        }

        return result

    }


    private fun getInfoUi(game: Game): List<InfoUi> {
        val textSize = baseTextSize * 0.75F

        val isGorisontal = 2 * infoHeight > infoWidth

        val rectSrc = Rect(
            0, 0,
            bitmapRepository.bmpSpaceshipLife.first().width,
            bitmapRepository.bmpSpaceshipLife.first().height
        )

        return game.players.indices.map { indexPlayer ->
            val leftStart = if (isGorisontal)
                0F
            else
                (indexPlayer / 2) * (this.surfaceWidth / 2).toFloat()
            (0 until game.listActors.spaceShips[indexPlayer].life).map { numberLife ->
                val left = leftStart + maxTextNameWidth + bmplife * numberLife.toFloat()
                val top = if (isGorisontal)
                    baseTextSize + textSize * indexPlayer + 5
                else
                    baseTextSize + textSize * (indexPlayer % 2) + 5
                InfoUi(
                    value = indexPlayer,
                    src = rectSrc,
                    dst = RectF(
                        left,
                        top,
                        left + bmplife,
                        top + textSize
                    )
                )
            }
        }.flatten()
    }

    private fun getColor(n: Int) = when (n) {
        0 -> Color.GREEN
        1 -> Color.CYAN
        // Color = pink
        2 -> Color.rgb(255, 192, 203)
        3 -> Color.YELLOW
        else -> Color.WHITE
    }
}