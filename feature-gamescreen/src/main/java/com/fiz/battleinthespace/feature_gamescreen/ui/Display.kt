package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.*
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.BitmapRepository
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.game.models.Actor
import kotlin.math.*

const val NUMBER_BITMAP_METEORITE_OPTION = 4

private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 7

private const val DIVISION_BY_SCREEN = 11

class Display(
    surfaceWidth: Int, surfaceHeight: Int,
    stateGame: ViewState,
    val bitmapRepository: BitmapRepository
) {

    private val paint: Paint = Paint()

    private val paintOutsideCircleJoystick = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        alpha = 80
        strokeWidth = 6F
    }

    private val paintInnerCircleJoystick = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        alpha = 40
        strokeWidth = 30F
    }

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
        canvas: Canvas
    ) {
        viewport.update(stateGame)

        drawBackground(stateGame.gameState.backgroundsUi, canvas)
        drawActors(stateGame, canvas)
        drawAnimationDestroys(stateGame, canvas)
        drawJoystick(stateGame.controllerState, canvas)
        drawHelper(stateGame, canvas)
    }

    private fun drawBackground(backgroundsUi: List<BackgroundUi>, canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        backgroundsUi.forEach {
            canvas.drawBitmap(
                bitmapRepository.bmpBackground[it.value],
                it.src,
                it.dst,
                paint
            )
        }
    }

    private fun drawActors(stateGame: ViewState, canvas: Canvas) {
        for (actor in stateGame.gameState.spaceShips) {
            if (!actor.inGame)
                continue
            for (point in viewport.getAllPoints(actor))
                drawFrame(actor.getBitmap(this), point.x, point.y, actor.size, actor.angle, canvas)
        }

        for (actor in stateGame.gameState.bullets) {
            for (point in viewport.getAllPoints(actor))
                drawFrame(actor.getBitmap(this), point.x, point.y, actor.size, actor.angle, canvas)
        }

        for (actor in stateGame.gameState.meteorites) {
            for (point in viewport.getAllPoints(actor))
                drawFrame(actor.getBitmap(this), point.x, point.y, actor.size, actor.angle, canvas)
        }
    }

    private fun drawFrame(
        bmp: Bitmap,
        x: Double,
        y: Double,
        size: Double,
        angle: Double = 0.0,
        canvas: Canvas
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

    private fun drawAnimationDestroys(stateGame: ViewState, canvas: Canvas) {
        for (animationDestroy in stateGame.gameState.bulletsAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_BULLET_DESTROY
            animationDestroy.frame =
                NUMBER_BITMAP_BULLET_DESTROY - ceil(animationDestroy.timeShow / step).toInt()
            val actor = animationDestroy as Actor
            for (point in viewport.getAllPoints(actor))
                drawFrame(
                    animationDestroy.getBitmap(this),
                    point.x,
                    point.y,
                    actor.size,
                    actor.angle,
                    canvas
                )
        }

        for (animationDestroy in stateGame.gameState.spaceshipsAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_SPACESHIP_DESTROY
            animationDestroy.frame =
                NUMBER_BITMAP_SPACESHIP_DESTROY - ceil(animationDestroy.timeShow / step).toInt()
            val actor = animationDestroy as Actor
            for (point in viewport.getAllPoints(actor))
                drawFrame(
                    animationDestroy.getBitmap(this),
                    point.x,
                    point.y,
                    actor.size,
                    actor.angle,
                    canvas
                )
        }
    }

    private fun drawJoystick(
        controllerState: ControllerState?,
        canvas: Canvas
    ) {
        controllerState ?: return

        canvas.drawCircle(
            controllerState.centerXOutsideCircle,
            controllerState.centerYOutsideCircle,
            controllerState.widthJoystick,
            paintOutsideCircleJoystick
        )

        canvas.drawCircle(
            controllerState.centerXInnerCircle,
            controllerState.centerYInnerCircle,
            30F,
            paintInnerCircleJoystick
        )
    }

    //TODO сделать указатели на соседние корабли и метеориты, если они не в зоне видимости экрана
    private fun drawHelper(stateGame: ViewState, canvas: Canvas) {
        val mainSpaceship = stateGame.gameState.spaceShips.filter { it.player.main }
        if (mainSpaceship.isNotEmpty()) {
            stateGame.gameState.spaceShips.filter { it.inGame && !it.player.main }
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

            if (stateGame.gameState.meteorites.all { (viewport.getAllPoints(it).size == 0) })
                stateGame.gameState.meteorites.forEach {
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
        drawInfo(stateGame, canvasInfo, FPS)

    }

    private fun drawInfo(stateGame: ViewState, canvasInfo: Canvas, FPS: Int) {
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

        val maxTextWidth = getMaxTextWidth(stateGame, paintFont)

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

    private fun getMaxTextWidth(stateGame: ViewState, paintFont: Paint): Int {
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

