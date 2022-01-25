package com.fiz.android.battleinthespace

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import com.fiz.android.battleinthespace.actor.*
import com.fiz.android.battleinthespace.engine.Physics
import kotlin.math.*

const val NUMBER_BITMAP_METEORITE_OPTION = 4

const val NUMBER_BITMAP_BACKGROUND = 8

private const val NUMBER_BITMAP_BULLET_DESTROY = 3

private const val DIVISION_BY_SCREEN = 11

class Display(
    private val surface: SurfaceView,
    private val context: Context,
) : BitmapLoad(context) {

    companion object {
        interface Listener {
            fun pauseButtonClick(status: String)
        }
    }

    private val paint: Paint = Paint()
    private lateinit var canvas: Canvas
    private lateinit var canvasInfo: Canvas
    private lateinit var state: State
    private lateinit var listener: Listener

    // Определить почему в самом начале загрузки класса width=0
    private var sizeUnit: Float = min(surface.width, surface.height).toFloat() / DIVISION_BY_SCREEN

    private val viewport = Viewport()

    fun render(state: State, controller: Controller, canvas: Canvas) {
        this.state = state
        this.canvas = canvas

        sizeUnit = min(surface.width, surface.height).toFloat() / DIVISION_BY_SCREEN
        viewport.update(surface, state, sizeUnit)
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
                val x = Physics.changeXifBorderTop(n.toDouble())
                val y = Physics.changeYifBorderTop(k.toDouble())
                val background = state.level.backgrounds[x.toInt()][y.toInt()]

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
        for (actor in state.level.listActors.actors) {
            if (actor is SpaceShip)
                if (!actor.inGame)
                    continue
            drawActor(actor, getbmp(actor))
        }
    }

    private fun getbmp(actor: MoveableActor): Bitmap {
        return when (actor) {
            is SpaceShip -> {
                if (actor.isFly)
                    bmpSpaceshipFly[state.level.listActors.spaceShips.indexOf(state.level.listActors.spaceShips.find { it === actor })]
                else
                    bmpSpaceship[state.level.listActors.spaceShips.indexOf(state.level.listActors.spaceShips.find { it === actor })]
            }
            is Bullet -> {
                bmpBullet
            }
            is Meteorite -> {
                bmpMeteorites[actor.view][actor.viewSize]
            }
            else -> {
                throw Error("No Moveable actor")
            }
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
        for (animationDestroy in state.level.listActors.listAnimationDestroy) {
            val step = animationDestroy.timeShowMax / NUMBER_BITMAP_BULLET_DESTROY
            val frame = NUMBER_BITMAP_BULLET_DESTROY - ceil(animationDestroy.timeShow / step).toInt()

            if (animationDestroy is BulletAnimationDestroy)
                drawActor(animationDestroy as Actor, bmpBulletDestroy[frame])
            else
                drawActor(animationDestroy as Actor, bmpSpaceshipDestroy[frame])
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
        //        state.level.spaceShips.filter{it.inGame&&it!=state.level.spaceShips[state.mainPlayer]}.forEach {
        //
        //        }
    }

    fun renderInfo(state: State, canvasInfo: Canvas) {
        this.state = state
        this.canvasInfo = canvasInfo

        drawInfo()

        listener = context as Listener
        listener.pauseButtonClick(state.status)
    }

    private fun drawInfo() {
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
        canvasInfo.drawText("Round ${state.round}", canvasInfo.width / 2F, baseTextSize, paintFont)

        val maxTextWidth = getMaxTextWidth(paintFont)

        for (n in 0 until state.options.countPlayers) {
            paintFont.textSize = textSize
            paintFont.textAlign = Paint.Align.LEFT
            paintFont.color = when (n) {
                0 -> Color.GREEN
                1 -> Color.CYAN
                // Color = pink
                2 -> Color.rgb(255, 192, 203)
                3 -> Color.YELLOW
                else -> Color.WHITE
            }
            canvasInfo.drawText(
                state.options.name[n],
                0F,
                baseTextSize + (textSize * (n + 1)),
                paintFont
            )

            for (k in 0 until state.players[n].life)
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
                state.players[n].score.toString(),
                (maxTextWidth + bmplife * 2).toFloat(),
                baseTextSize + textSize * (n + 1),
                paintFont
            )
        }

    }

    private fun getMaxTextWidth(paintFont: Paint): Int {
        var result = 0
        for (namePlayer in state.options.name) {
            val mTextBoundRect = Rect(0, 0, 0, 0)
            paintFont.getTextBounds(namePlayer, 0, namePlayer.length, mTextBoundRect)
            val textWidth = mTextBoundRect.width()
            result = max(textWidth, result)
        }
        return result
    }

}

