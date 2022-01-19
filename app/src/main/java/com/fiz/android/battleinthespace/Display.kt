package com.fiz.android.battleinthespace

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.SurfaceView
import android.widget.Button
import com.fiz.android.battleinthespace.actor.Actor
import com.fiz.android.battleinthespace.actor.SpaceShip
import com.fiz.android.battleinthespace.engine.Physics
import kotlin.math.*

const val NUMBER_BITMAP_METEORITE_OPTION = 4

const val NUMBER_BITMAP_BACKGROUND = 8
private const val NUMBER_BITMAP_METEORITE_LEVEL = 4
private const val NUMBER_BITMAP_SPACESHIP = 4
private const val NUMBER_BITMAP_SPACESHIP_LIFE = 4

private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 7

private const val DIVISION_BY_SCREEN = 11

class Display(
    private val resources: Resources,
    private val context: Context,
    private val pauseButton: Button,
    private val surface: SurfaceView
) {
    private val paint: Paint = Paint()

    private val bmpBackground: Array<Bitmap> by lazy(::initBmpBackground)
    private fun initBmpBackground(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_BITMAP_BACKGROUND)
            result += BitmapFactory.decodeResource(
                resources, resources.getIdentifier(
                    "background$i",
                    "drawable", context.packageName
                )
            )
        return result
    }

    private val bmpBullet: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bullet)

    private val bmpBulletDestroy: Array<Bitmap> by lazy(::initBmpBulletDestroy)
    private fun initBmpBulletDestroy(): Array<Bitmap> {
        val temp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bullet_destroy)
        var result: Array<Bitmap> = emptyArray()
        val size = temp.width / NUMBER_BITMAP_BULLET_DESTROY
        for (i in 0 until NUMBER_BITMAP_BULLET_DESTROY)
            result += Bitmap.createBitmap(temp, i * size, 0, size, size)
        return result
    }

    private val bmpMeteorites: Array<Array<Bitmap>> by lazy(::initBmpMeteorite)
    private fun initBmpMeteorite(): Array<Array<Bitmap>> {
        var result: Array<Array<Bitmap>> = emptyArray()
        for (i in 1..NUMBER_BITMAP_METEORITE_OPTION) {
            var row: Array<Bitmap> = emptyArray()
            for (j in 1..NUMBER_BITMAP_METEORITE_LEVEL)
                row += BitmapFactory.decodeResource(
                    resources, resources.getIdentifier(
                        "meteorite$i$j",
                        "drawable", context.packageName
                    )
                )
            result += row
        }
        return result
    }

    private val bmpSpaceship: Array<Bitmap> by lazy(::initBmpSpaceship)
    private fun initBmpSpaceship(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_BITMAP_SPACESHIP)
            result += BitmapFactory.decodeResource(
                resources, resources.getIdentifier(
                    "spaceship$i",
                    "drawable", context.packageName
                )
            )
        return result
    }

    private val bmpSpaceshipFly: Array<Bitmap> by lazy(::initBmpSpaceshipFly)
    private fun initBmpSpaceshipFly(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_BITMAP_SPACESHIP)
            result += BitmapFactory.decodeResource(
                resources, resources.getIdentifier(
                    "spaceship_fly$i",
                    "drawable", context.packageName
                )
            )
        return result
    }

    private val bmpSpaceshipDestroy: Array<Bitmap> by lazy(::initBmpSpaceshipDestroy)
    private fun initBmpSpaceshipDestroy(): Array<Bitmap> {
        val temp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.spaceship_destroy)
        val size = temp.width / NUMBER_BITMAP_SPACESHIP_DESTROY
        var result: Array<Bitmap> = emptyArray()
        for (i in 0 until NUMBER_BITMAP_SPACESHIP_DESTROY)
            result += Bitmap.createBitmap(temp, i * size, 0, size, size)
        return result
    }

    private val bmpSpaceshipLife: Array<Bitmap> by lazy(::initBmpSpaceshipLife)
    private fun initBmpSpaceshipLife(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_BITMAP_SPACESHIP_LIFE)
            result += BitmapFactory.decodeResource(
                resources, resources.getIdentifier(
                    "spaceship_life$i",
                    "drawable", context.packageName
                )
            )
        return result
    }

    // Определить почему в самом начале загрузки класса width=0
    private var sizeUnit: Float = min(surface.width, surface.height).toFloat() / DIVISION_BY_SCREEN

    inner class Viewport {
        var left: Double = 0.0
        var top: Double = 0.0
        var width: Double = 0.0
        var height: Double = 0.0
        var levelWidth: Double = 0.0
        var levelHeight: Double = 0.0

        fun update(state: State) {
            // Определить почему в самом начале загрузки класса width=0
            width = (surface.width / sizeUnit).toDouble()
            height = (surface.height / sizeUnit).toDouble()

            levelWidth = state.level.width
            levelHeight = state.level.height

            val marginX = surface.width / sizeUnit / 2
            val marginY = surface.height / sizeUnit / 2
            val spaceship = state.level.spaceShips[state.mainPlayer]
            val center = spaceship.center

            left = center.x - marginX

            top = center.y - marginY
        }
    }

    private val viewport = Viewport()

    fun render(state: State, controller: Controller, canvas: Canvas) {
        sizeUnit = min(surface.width, surface.height).toFloat() / DIVISION_BY_SCREEN
        viewport.update(state)
        drawBackground(state, canvas)
        drawSpaceships(state, canvas)
        drawBullets(state, canvas)
        drawAnimationBulletDestroys(state, canvas)
        drawMeteorites(state, canvas)
        drawAnimationSpaceshipDestroys(state, canvas)
        drawJoystick(controller, canvas)
    }

    fun renderInfo(state: State, canvasInfo: Canvas) {
        drawInfo(state, canvasInfo)


        if (state.status == "pause")
            pauseButton.post { pauseButton.text = resources.getString(R.string.resume_game_button) }
        else
            pauseButton.post { pauseButton.text = resources.getString(R.string.pause_game_button) }
    }

    private fun drawBackground(state: State, canvas: Canvas) {
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

    private fun drawSpaceships(state: State, canvas: Canvas) {
        val countViewportXOnScreen = ceil(viewport.width / viewport.levelWidth)
        val countViewportYOnScreen = ceil(viewport.height / viewport.levelHeight)
        val widthAllViewportsOnScreen = viewport.levelWidth * countViewportXOnScreen
        val heightAllViewportsOnScreen = viewport.levelHeight * countViewportYOnScreen
        for ((index, spaceShip) in state.level.spaceShips.withIndex())
            if (spaceShip.inGame) {

                var relativeX =
                    spaceShip.center.x - widthAllViewportsOnScreen

                while (relativeX <= spaceShip.center.x + widthAllViewportsOnScreen) {
                    var relativeY =
                        spaceShip.center.y - heightAllViewportsOnScreen
                    while (relativeY <= spaceShip.center.y + heightAllViewportsOnScreen) {
                        drawSpaceShip(relativeX, relativeY, spaceShip, index, canvas)

                        relativeY += viewport.levelHeight
                    }

                    relativeX += viewport.levelWidth
                }

            }
    }

    private fun drawSpaceShip(
        relativeX: Double,
        relativeY: Double,
        spaceShip: SpaceShip,
        index: Int,
        canvas: Canvas
    ) {
        if (spaceShip.isFly) {
            drawObjectWithAngle(
                bmpSpaceshipFly[index],
                spaceShip.angle,
                relativeX - viewport.left,
                relativeY - viewport.top,
                canvas
            )
        } else {
            drawObjectWithAngle(
                bmpSpaceship[index],
                spaceShip.angle,
                relativeX - viewport.left,
                relativeY - viewport.top,
                canvas
            )
        }
    }

    private fun drawObjectWithAngle(
        bmp: Bitmap,
        angle: Double,
        x: Double,
        y: Double,
        canvas: Canvas
    ) {
        canvas.save()
        canvas.translate(x.toFloat() * sizeUnit, y.toFloat() * sizeUnit)
        canvas.rotate(angle.toFloat())

        val rectSrc = Rect(
            0, 0,
            bmp.width, bmp.height
        )
        val rectDst = RectF(
            -(sizeUnit / 2), -(sizeUnit / 2),
            sizeUnit / 2, sizeUnit / 2
        )

        canvas.drawBitmap(bmp, rectSrc, rectDst, paint)

        canvas.restore()
    }

    private fun drawBullets(state: State, canvas: Canvas) {
        for (bullet in state.level.bullets)
            drawActor(bullet, bmpBullet, canvas)
    }

    private fun drawAnimationBulletDestroys(state: State, canvas: Canvas) {
        for (animationBulletDestroy in state.level.animationBulletDestroys) {
            val step =
                animationBulletDestroy.timeShowMax / NUMBER_BITMAP_BULLET_DESTROY.toDouble()
            val frame =
                NUMBER_BITMAP_BULLET_DESTROY - ceil(animationBulletDestroy.timeShow / step).toInt()

            drawActor(animationBulletDestroy, bmpBulletDestroy[frame], canvas)
        }
    }

    private fun drawMeteorites(state: State, canvas: Canvas) {
        for (meteorite in state.level.meteorites)
            drawActor(meteorite, bmpMeteorites[meteorite.view][meteorite.viewSize], canvas)
    }

    private fun drawAnimationSpaceshipDestroys(state: State, canvas: Canvas) {
        for (animationSpaceShipDestroy in state.level.animationSpaceShipDestroys) {
            val step =
                animationSpaceShipDestroy.timeShowMax / NUMBER_BITMAP_SPACESHIP_DESTROY.toDouble()
            val frame =
                NUMBER_BITMAP_SPACESHIP_DESTROY - ceil(animationSpaceShipDestroy.timeShow / step).toInt()

            drawActor(animationSpaceShipDestroy, bmpSpaceshipDestroy[frame], canvas)
        }
    }

    private fun drawActor(actor: Actor, bmp: Bitmap, canvas: Canvas) {
        val countViewportXOnScreen = ceil(viewport.width / viewport.levelWidth)
        val countViewportYOnScreen = ceil(viewport.height / viewport.levelHeight)
        val widthAllViewportsOnScreen = viewport.levelWidth * countViewportXOnScreen
        val heightAllViewportsOnScreen = viewport.levelHeight * countViewportYOnScreen
        var relativeX =
            actor.center.x - widthAllViewportsOnScreen

        while (relativeX <= actor.center.x + widthAllViewportsOnScreen) {
            var relativeY =
                actor.center.y - heightAllViewportsOnScreen
            while (relativeY <= actor.center.y + heightAllViewportsOnScreen) {

                drawOneFrame(
                    relativeX - viewport.left,
                    relativeY - viewport.top,
                    actor.size,
                    bmp,
                    canvas
                )

                relativeY += viewport.levelHeight
            }

            relativeX += viewport.levelWidth
        }
    }

    private fun drawOneFrame(x: Double, y: Double, size: Double, bmp: Bitmap, canvas: Canvas) {
        val rectSrc = Rect(
            0, 0,
            bmp.width, bmp.height
        )

        val rectDst = RectF(
            (x * sizeUnit - size * sizeUnit / 2).toFloat(),
            (y * sizeUnit - size * sizeUnit / 2).toFloat(),
            (x * sizeUnit + size * sizeUnit / 2).toFloat(),
            (y * sizeUnit + size * sizeUnit / 2).toFloat()
        )

        canvas.drawBitmap(bmp, rectSrc, rectDst, paint)
    }

    private fun drawInfo(state: State, canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        val paintFont = Paint()
        val rectSrc = Rect(
            0, 0,
            bmpSpaceshipLife[0].width, bmpSpaceshipLife[0].height
        )

        paintFont.textSize = 36F
        paintFont.color = Color.WHITE
        canvas.drawText("Round ${state.round}", canvas.width / 2F, 25F, paintFont)

        for (n in 1..state.countPlayers) {
            paintFont.color = when (n) {
                1 -> Color.GREEN
                2 -> Color.CYAN
                // Color = pink
                3 -> Color.rgb(255, 192, 203)
                4 -> Color.YELLOW
                else -> Color.WHITE
            }
            canvas.drawText(
                state.namePlayers[n - 1],
                0F,
                70F + 70F * (n - 1) + sizeUnit / 2.5F / 2,
                paintFont
            )

            for (k in 0 until state.level.lifes[n - 1])
                canvas.drawBitmap(
                    bmpSpaceshipLife[n - 1], rectSrc,
                    RectF(
                        160F + sizeUnit / 2.5F * k,
                        55F + 70F * (n - 1),
                        160F + sizeUnit / 2.5F * k + sizeUnit / 2.5F,
                        55F + 70F * (n - 1) + sizeUnit / 2.5F
                    ),
                    paint
                )

            canvas.drawText(
                state.scores[n - 1].toString(),
                160F + sizeUnit / 2.5F * 4,
                70F + 70F * (n - 1) + sizeUnit / 2.5F / 2,
                paintFont
            )
        }

    }

    private fun drawJoystick(controller: Controller, canvas: Canvas) {
        val paintFont = Paint()
        paintFont.color = Color.RED
        paintFont.style = Paint.Style.STROKE
        paintFont.alpha=80
        paintFont.strokeWidth = 6F

        val a= IntArray(2)
        surface.getLocationOnScreen(a)
        val left=a[0]
        val top=a[1]

        var cx = controller.leftSide.point.x.toFloat()-left
        var cy = controller.leftSide.point.y.toFloat()-top
        if (controller.leftSide.touch) {
            canvas.drawCircle(
                cx,
                cy,
                controller.widthJoystick,
                paintFont
            )
        }

        paintFont.color = Color.GREEN
        paintFont.style = Paint.Style.FILL;
        paintFont.alpha=40
        paintFont.strokeWidth = 30F
        cx+=controller.widthJoystick*controller.power*cos(controller.angle/ 180.0 * Math.PI).toFloat()
        cy+=controller.widthJoystick*controller.power*sin(controller.angle/ 180.0 * Math.PI).toFloat()
        if (controller.leftSide.touch)
            canvas.drawCircle(
                cx,
                cy,
                30F,
                paintFont
            )
    }
}

