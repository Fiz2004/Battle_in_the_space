package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.*
import com.fiz.battleinthespace.feature_gamescreen.data.actor.Actor
import com.fiz.battleinthespace.feature_gamescreen.data.actor.SpaceShip
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.BitmapRepository
import com.fiz.battleinthespace.feature_gamescreen.domain.Controller
import kotlin.math.*

const val NUMBER_BITMAP_METEORITE_OPTION = 4

private const val NUMBER_BITMAP_BULLET_DESTROY = 3

private const val DIVISION_BY_SCREEN = 11

class Display(
    surfaceWidth: Int, surfaceHeight: Int,
    private var stateGame: ViewState,
    val bitmapRepository: BitmapRepository,
    private val leftLocationOnScreen: Int,
    private val topLocationOnScreen: Int
) {

    private val paint: Paint = Paint()
    private lateinit var canvas: Canvas
    private lateinit var canvasInfo: Canvas

    private var sizeUnit: Float = min(surfaceWidth, surfaceHeight).toFloat() / DIVISION_BY_SCREEN

    private var viewport = Viewport(
        surfaceWidth,
        surfaceHeight,
        stateGame.gameState.width,
        stateGame.gameState.height,
        sizeUnit
    )

    fun render(
        stateGame: ViewState,
        canvas: Canvas,
        controller: Controller
    ) {
        this.stateGame = stateGame
        this.canvas = canvas

        viewport.update(stateGame)
        drawBackground()
        drawActors()
        drawAnimationDestroys()
        drawJoystick(controller, leftLocationOnScreen, topLocationOnScreen)
        drawHelper()
    }

    private fun drawBackground() {
        canvas.drawColor(Color.BLACK)

        val xStart = floor(viewport.left).toInt()
        val xEnd = ceil(viewport.left + viewport.width).toInt()
        val yStart = floor(viewport.top).toInt()
        val yEnd = ceil(viewport.top + viewport.height).toInt()

        val rectSrc = Rect(
            0, 0,
            bitmapRepository.bmpBackground[0].width, bitmapRepository.bmpBackground[0].height
        )
        for (n in xStart until xEnd)
            for (k in yStart until yEnd) {
                val x = Physics.changeCoorIfBorderTop(n.toDouble(), Physics.width)
                val y = Physics.changeCoorIfBorderTop(k.toDouble(), Physics.height)
                val background = stateGame.gameState.backgrounds[x.toInt()][y.toInt()]

                val xStartDst = (n - viewport.left).toFloat() * sizeUnit
                val yStartDst = (k - viewport.top).toFloat() * sizeUnit
                val rectDst = RectF(
                    xStartDst,
                    yStartDst,
                    xStartDst + sizeUnit,
                    yStartDst + sizeUnit
                )

                canvas.drawBitmap(
                    bitmapRepository.bmpBackground[background],
                    rectSrc,
                    rectDst,
                    paint
                )
            }
    }

    private fun drawActors() {
        for (actor in stateGame.gameState.listActors.actors) {
            if (actor is SpaceShip)
                if (!actor.inGame)
                    continue
            drawActor(actor, actor.getBitmap(this))
        }
    }

    private fun drawActor(actor: Actor, bmp: Bitmap) {
        for (point in viewport.getAllPoints(actor))
            drawFrame(bmp, point.x, point.y, actor.size, actor.angle)
    }

    private fun drawFrame(
        bmp: Bitmap,
        x: Double,
        y: Double,
        size: Double,
        angle: Double = 0.0,
    ) {
        canvas.save()
        canvas.translate(x.toFloat() * sizeUnit, y.toFloat() * sizeUnit)
        canvas.rotate(angle.toFloat())

        val rectSrc = Rect(
            0, 0,
            bmp.width, bmp.height
        )
        val halfSize = (size * sizeUnit / 2).toFloat()
        val rectDst = RectF(
            -halfSize, -halfSize,
            halfSize, halfSize
        )

        canvas.drawBitmap(bmp, rectSrc, rectDst, paint)

        canvas.restore()
    }

    private fun drawAnimationDestroys() {
        for (animationDestroy in stateGame.gameState.listActors.listAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_BULLET_DESTROY
            animationDestroy.frame =
                NUMBER_BITMAP_BULLET_DESTROY - ceil(animationDestroy.timeShow / step).toInt()
            drawActor(animationDestroy as Actor, animationDestroy.getBitmap(this))
        }
    }

    private fun drawJoystick(
        controller: Controller,
        leftLocationOnScreen: Int,
        topLocationOnScreen: Int
    ) {
        val paintFont = Paint()
        paintFont.color = Color.RED
        paintFont.style = Paint.Style.STROKE
        paintFont.alpha = 80
        paintFont.strokeWidth = 6F

        var cx = controller.leftSide.point.x.toFloat() - leftLocationOnScreen
        var cy = controller.leftSide.point.y.toFloat() - topLocationOnScreen
        if (controller.leftSide.touch) {
            canvas.drawCircle(
                cx,
                cy,
                controller.widthJoystick,
                paintFont
            )
        }

        paintFont.color = Color.GREEN
        paintFont.style = Paint.Style.FILL
        paintFont.alpha = 40
        paintFont.strokeWidth = 30F
        cx += controller.widthJoystick * controller.power * cos(controller.angle / 180.0 * Math.PI).toFloat()
        cy += controller.widthJoystick * controller.power * sin(controller.angle / 180.0 * Math.PI).toFloat()
        if (controller.leftSide.touch)
            canvas.drawCircle(
                cx,
                cy,
                30F,
                paintFont
            )
    }

    //TODO сделать указатели на соседние корабли и метеориты, если они не в зоне видимости экрана
    private fun drawHelper() {
        val mainSpaceship = stateGame.gameState.listActors.spaceShips.filter { it.player.main }
        if (mainSpaceship.isNotEmpty()) {
            stateGame.gameState.listActors.spaceShips.filter { it.inGame && !it.player.main }
                .forEach {
                    if (viewport.getAllPoints(it).size == 0) {
                        val paintFont = Paint()
                        paintFont.color = getColor(it.player.number)
                        paintFont.style = Paint.Style.FILL
                        paintFont.alpha = 80
                        paintFont.strokeWidth = 30F

                        val angle = Physics.findAngle(mainSpaceship[0].center, it.center)
                        val angleToRadians = angle / 180.0 * Math.PI
                        val vec = Vec(cos(angleToRadians), sin(angleToRadians))
                        val cx =
                            (mainSpaceship[0].center.x - viewport.left + vec.x * viewport.width / 2).toFloat() * sizeUnit - 60F
                        val cy =
                            (mainSpaceship[0].center.y - viewport.top + vec.y * viewport.height / 2).toFloat() * sizeUnit - 60F
                        canvas.drawCircle(
                            cx,
                            cy,
                            30F,
                            paintFont
                        )
                    }
                }

            if (stateGame.gameState.listActors.meteorites.all { (viewport.getAllPoints(it).size == 0) })
                stateGame.gameState.listActors.meteorites.forEach {
                    val paintFont = Paint()
                    paintFont.color = Color.RED
                    paintFont.style = Paint.Style.FILL
                    paintFont.alpha = 160
                    paintFont.strokeWidth = 20F

                    val angle = Physics.findAngle(mainSpaceship[0].center, it.center)
                    val angleToRadians = angle / 180.0 * Math.PI
                    val vec = Vec(cos(angleToRadians), sin(angleToRadians))
                    val cx =
                        (mainSpaceship[0].center.x - viewport.left + vec.x * viewport.width / 2).toFloat() * sizeUnit - 40F
                    val cy =
                        (mainSpaceship[0].center.y - viewport.top + vec.y * viewport.height / 2).toFloat() * sizeUnit - 40F
                    canvas.drawCircle(
                        cx,
                        cy,
                        30F,
                        paintFont
                    )
                }
        }
    }

    fun renderInfo(stateGame: ViewState, canvasInfo: Canvas, FPS: Int) {
        this.stateGame = stateGame
        this.canvasInfo = canvasInfo

        drawInfo(FPS)

    }

    private fun drawInfo(FPS: Int) {
        val width = canvasInfo.width
        val height = canvasInfo.height
        val minCharacteristic = min(width, height)
        val bmplife = minCharacteristic / 3 / 3

        canvasInfo.drawColor(Color.BLACK)
        val paintFont = Paint()
        val rectSrc = Rect(
            0, 0,
            bitmapRepository.bmpSpaceshipLife[0].width, bitmapRepository.bmpSpaceshipLife[0].height
        )

        val baseTextSize = minCharacteristic / 6F
        val textSize = baseTextSize * 0.75F
        paintFont.textSize = baseTextSize
        paintFont.color = Color.WHITE
        paintFont.textAlign = Paint.Align.CENTER
        canvasInfo.drawText(
            "Round ${stateGame.gameState.round}",
            canvasInfo.width / 2F,
            baseTextSize,
            paintFont
        )

        val maxTextWidth = getMaxTextWidth(paintFont)

        for (n in 0 until stateGame.gameState.players.size) {
            paintFont.textSize = textSize
            paintFont.textAlign = Paint.Align.LEFT
            paintFont.color = getColor(n)
            canvasInfo.drawText(
                stateGame.gameState.players[n].name,
                0F,
                baseTextSize + (textSize * (n + 1)),
                paintFont
            )

            for (k in 0 until stateGame.gameState.players[n].life)
                canvasInfo.drawBitmap(
                    bitmapRepository.bmpSpaceshipLife[n], rectSrc,
                    RectF(
                        maxTextWidth + bmplife * (k - 1).toFloat(),
                        baseTextSize + textSize * n,
                        (maxTextWidth + bmplife * (k - 1) + bmplife).toFloat(),
                        baseTextSize + textSize * n + textSize
                    ),
                    paint
                )

            canvasInfo.drawText(
                stateGame.gameState.players[n].score.toString(),
                (maxTextWidth + bmplife * 2).toFloat(),
                baseTextSize + textSize * (n + 1),
                paintFont
            )
        }

        canvasInfo.drawText(
            FPS.toString(),
            0F,
            baseTextSize,
            paintFont
        )

    }

    private fun getColor(n: Int) = when (n) {
        0 -> Color.GREEN
        1 -> Color.CYAN
        // Color = pink
        2 -> Color.rgb(255, 192, 203)
        3 -> Color.YELLOW
        else -> Color.WHITE
    }

    private fun getMaxTextWidth(paintFont: Paint): Int {
        var result = 0
        for (namePlayer in stateGame.gameState.players) {
            val mTextBoundRect = Rect(0, 0, 0, 0)
            paintFont.getTextBounds(namePlayer.name, 0, namePlayer.name.length, mTextBoundRect)
            val textWidth = mTextBoundRect.width()
            result = max(textWidth, result)
        }
        return result
    }

}

