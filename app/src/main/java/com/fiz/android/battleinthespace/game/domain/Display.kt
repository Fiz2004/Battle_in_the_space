package com.fiz.android.battleinthespace.game.domain

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.SurfaceView
import com.fiz.android.battleinthespace.game.data.actor.Actor
import com.fiz.android.battleinthespace.game.data.actor.SpaceShip
import com.fiz.android.battleinthespace.game.data.engine.Physics
import com.fiz.android.battleinthespace.game.data.engine.Vec
import kotlin.math.*

const val NUMBER_BITMAP_METEORITE_OPTION = 4

const val NUMBER_BITMAP_BACKGROUND = 8

private const val NUMBER_BITMAP_BULLET_DESTROY = 3

private const val DIVISION_BY_SCREEN = 11

class Display(
    var surface: SurfaceView,
    private var stateGame: StateGame,
    private val context: Context,
) : BitmapLoad(context) {

    private val paint: Paint = Paint()
    private lateinit var canvas: Canvas
    private lateinit var canvasInfo: Canvas
    private lateinit var listener: Listener

    private var sizeUnit: Float = min(surface.width, surface.height).toFloat() / DIVISION_BY_SCREEN

    private var viewport = Viewport(surface, stateGame, sizeUnit)

    fun viewPortUpdate() {
        viewport = Viewport(surface, stateGame, sizeUnit)
    }

    fun render(stateGame: StateGame, controller: Controller, canvas: Canvas) {
        this.stateGame = stateGame
        this.canvas = canvas

        viewport.update(stateGame)
        drawBackground()
        drawActors()
        drawAnimationDestroys()
        drawJoystick(controller)
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
            bmpBackground[0].width, bmpBackground[0].height
        )
        for (n in xStart until xEnd)
            for (k in yStart until yEnd) {
                val x = Physics.changeCoorIfBorderTop(n.toDouble(), Physics.width)
                val y = Physics.changeCoorIfBorderTop(k.toDouble(), Physics.height)
                val background = stateGame.level.backgrounds[x.toInt()][y.toInt()]

                val xStartDst = (n - viewport.left).toFloat() * sizeUnit
                val yStartDst = (k - viewport.top).toFloat() * sizeUnit
                val rectDst = RectF(
                    xStartDst,
                    yStartDst,
                    xStartDst + sizeUnit,
                    yStartDst + sizeUnit
                )

                canvas.drawBitmap(bmpBackground[background], rectSrc, rectDst, paint)
            }
    }

    private fun drawActors() {
        for (actor in stateGame.level.listActors.actors) {
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
        for (animationDestroy in stateGame.level.listActors.listAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_BULLET_DESTROY
            animationDestroy.frame =
                NUMBER_BITMAP_BULLET_DESTROY - ceil(animationDestroy.timeShow / step).toInt()
            drawActor(animationDestroy as Actor, animationDestroy.getBitmap(this))
        }
    }

    private fun drawJoystick(controller: Controller) {
        val paintFont = Paint()
        paintFont.color = Color.RED
        paintFont.style = Paint.Style.STROKE
        paintFont.alpha = 80
        paintFont.strokeWidth = 6F

        val a = IntArray(2)
        surface.getLocationOnScreen(a)
        val left = a[0]
        val top = a[1]

        var cx = controller.leftSide.point.x.toFloat() - left
        var cy = controller.leftSide.point.y.toFloat() - top
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
        val mainSpaceship = stateGame.level.listActors.spaceShips.filter { it.playerGame.main }
        if (mainSpaceship.isNotEmpty()) {
            stateGame.level.listActors.spaceShips.filter { it.inGame && !it.playerGame.main }
                .forEach {
                    if (viewport.getAllPoints(it).size == 0) {
                        val paintFont = Paint()
                        paintFont.color = getColor(it.playerGame.number)
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

            if (stateGame.level.listActors.meteorites.all { (viewport.getAllPoints(it).size == 0) })
                stateGame.level.listActors.meteorites.forEach {
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

    fun renderInfo(stateGame: StateGame, canvasInfo: Canvas, FPS: Int) {
        this.stateGame = stateGame
        this.canvasInfo = canvasInfo

        drawInfo(FPS)

        listener = context as Listener
        listener.pauseButtonClick(stateGame.status)
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
            bmpSpaceshipLife[0].width, bmpSpaceshipLife[0].height
        )

        val baseTextSize = minCharacteristic / 6F
        val textSize = baseTextSize * 0.75F
        paintFont.textSize = baseTextSize
        paintFont.color = Color.WHITE
        paintFont.textAlign = Paint.Align.CENTER
        canvasInfo.drawText(
            "Round ${stateGame.round}",
            canvasInfo.width / 2F,
            baseTextSize,
            paintFont
        )

        val maxTextWidth = getMaxTextWidth(paintFont)

        for (n in 0 until stateGame.countPlayers) {
            paintFont.textSize = textSize
            paintFont.textAlign = Paint.Align.LEFT
            paintFont.color = getColor(n)
            canvasInfo.drawText(
                stateGame.name[n],
                0F,
                baseTextSize + (textSize * (n + 1)),
                paintFont
            )

            for (k in 0 until stateGame.playerGames[n].life)
                canvasInfo.drawBitmap(
                    bmpSpaceshipLife[n], rectSrc,
                    RectF(
                        maxTextWidth + bmplife * (k - 1).toFloat(),
                        baseTextSize + textSize * n,
                        (maxTextWidth + bmplife * (k - 1) + bmplife).toFloat(),
                        baseTextSize + textSize * n + textSize
                    ),
                    paint
                )

            canvasInfo.drawText(
                stateGame.playerGames[n].score.toString(),
                (maxTextWidth + bmplife * 2).toFloat(),
                baseTextSize + textSize * (n + 1),
                paintFont
            )
        }

        Log.d("AAA", FPS.toString())
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
        for (namePlayer in stateGame.name) {
            val mTextBoundRect = Rect(0, 0, 0, 0)
            paintFont.getTextBounds(namePlayer, 0, namePlayer.length, mTextBoundRect)
            val textWidth = mTextBoundRect.width()
            result = max(textWidth, result)
        }
        return result
    }

    companion object {
        interface Listener {
            fun pauseButtonClick(status: String)
        }
    }
}

