package com.fiz.android.battleinthespace

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.SurfaceView
import android.widget.Button
import com.fiz.android.battleinthespace.engine.Physics
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private const val NUMBER_BITMAP_BACKGROUND = 8
private const val NUMBER_BITMAP_METEORITE_LEVEL = 4
const val NUMBER_BITMAP_METEORITE_OPTION = 4
private const val NUMBER_BITMAP_SPACESHIP = 4
private const val NUMBER_BITMAP_SPACESHIP_LIFE = 4

private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 7

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

    private val DIVISION_BY_SCREEN = 21
    private val DENSITY_MAX = 6
    private val density: Float = resources.displayMetrics.density

    // Определить почему в самом начале загрузки класса width=0
    private var sizeUnit: Float = min(surface.width, surface.height).toFloat() / DIVISION_BY_SCREEN

    inner class Viewport {
        var left: Double = 0.0
        var top: Double = 0.0
        var width: Double = .0
        var height: Double = .0

        fun update(state: State) {
            // Определить почему в самом начале загрузки класса width=0
            width = (surface.width / sizeUnit).toDouble()
            height= (surface.height / sizeUnit).toDouble()

            var marginX = surface.width / sizeUnit/2
            var marginY = surface.height / sizeUnit/2
            var spaceship = state.spaceShips[0]
            var center = spaceship.center

            left = center.x - marginX

           top = center.y - marginY
        }
    }

    val viewport = Viewport()

    fun render(state: State, canvas: Canvas) {
        sizeUnit = min(surface.width, surface.height).toFloat() / DIVISION_BY_SCREEN
        viewport.update(state)
        drawBackground(state, canvas)
//        drawSpaceships(state, canvas)
//        drawBullet(state, canvas)
//        drawAnimationBulletDestroy(state, canvas)
//        drawMeteorite(state, canvas)
//        drawAnimationSpaceshipDestroy(state, canvas)
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
                val x=Physics.changeXifBorderTop(n.toDouble())
                val y=Physics.changeYifBorderTop(k.toDouble())
                val background = state.backgrounds[x.toInt()][y.toInt()]
                val rectDst = RectF(
                    (n-viewport.left).toFloat()* sizeUnit,
                    (k-viewport.top).toFloat() * sizeUnit,
                    (n-viewport.left).toFloat()* sizeUnit  + sizeUnit,
                    (k-viewport.top).toFloat() * sizeUnit  + sizeUnit
                )

                canvas.drawBitmap(bmpBackground[background], rectSrc, rectDst, paint)
            }
    }

    private fun drawSpaceships(state: State, canvas: Canvas) {
        for (player in 0 until state.countPlayers)
            if (state.spaceShips[player].inGame) {
                if (state.spaceShips[player].isFly) {
                    drawObjectWithAngle(
                        bmpSpaceshipFly[player],
                        state.spaceShips[player].angle,
                        state.spaceShips[player].center.x,
                        state.spaceShips[player].center.y,
                        canvas
                    )
                } else {
                    drawObjectWithAngle(
                        bmpSpaceship[player],
                        state.spaceShips[player].angle,
                        state.spaceShips[player].center.x,
                        state.spaceShips[player].center.y,
                        canvas
                    )
                }
                if (state.spaceShips[player].center.y + 1 >= canvas.height)
                    drawObjectWithAngle(
                        bmpSpaceship[player],
                        state.spaceShips[player].angle,
                        state.spaceShips[player].center.x,
                        state.spaceShips[player].center.y - canvas.height,
                        canvas
                    )
                if (state.spaceShips[player].center.x + 1 >= canvas.width)
                    drawObjectWithAngle(
                        bmpSpaceship[player],
                        state.spaceShips[player].angle,
                        state.spaceShips[player].center.x - canvas.width,
                        state.spaceShips[player].center.y,
                        canvas
                    )
                if ((state.spaceShips[player].center.x + 1 >= canvas.width) && (state.spaceShips[player].center.y + 1 >= canvas.height))
                    drawObjectWithAngle(
                        bmpSpaceship[player],
                        state.spaceShips[player].angle,
                        state.spaceShips[player].center.x - canvas.width,
                        state.spaceShips[player].center.y - canvas.height,
                        canvas
                    )
                if (state.spaceShips[player].center.x - 1 <= 0)
                    drawObjectWithAngle(
                        bmpSpaceship[player],
                        state.spaceShips[player].angle,
                        state.spaceShips[player].center.x + canvas.width,
                        state.spaceShips[player].center.y,
                        canvas
                    )
                if (state.spaceShips[player].center.y - 1 <= 0)
                    drawObjectWithAngle(
                        bmpSpaceship[player],
                        state.spaceShips[player].angle,
                        state.spaceShips[player].center.x,
                        state.spaceShips[player].center.y + canvas.height,
                        canvas
                    )
                if ((state.spaceShips[player].center.x - 1 <= 0) && (state.spaceShips[player].center.y - 1 <= 0))
                    drawObjectWithAngle(
                        bmpSpaceship[player],
                        state.spaceShips[player].angle,
                        state.spaceShips[player].center.x + canvas.width,
                        state.spaceShips[player].center.y + canvas.height,
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

    private fun drawBullet(state: State, canvas: Canvas) {
        val size = sizeUnit / 10
        val rectSrc = Rect(
            0, 0,
            bmpBullet.width, bmpBullet.height
        )

        if (state.bullets.size > 0)
            for (n in 0 until state.bullets.size) {
                val bullet = state.bullets[n]
                val rectDst = RectF(
                    (bullet.center.x * sizeUnit - size / 2).toFloat(),
                    (bullet.center.y * sizeUnit - size / 2).toFloat(),
                    (bullet.center.x * sizeUnit + size / 2).toFloat(),
                    (bullet.center.y * sizeUnit + size / 2).toFloat()
                )

                canvas.drawBitmap(bmpBullet, rectSrc, rectDst, paint)
            }
    }

    private fun drawAnimationBulletDestroy(state: State, canvas: Canvas) {

        val rectSrc = Rect(
            0, 0,
            bmpBulletDestroy[0].width, bmpBulletDestroy[0].height
        )
        val size = sizeUnit / 10
        for (n in 0 until state.animationBulletDestroys.size) {
            val step =
                state.animationBulletDestroys[n].timeShowMax / NUMBER_BITMAP_BULLET_DESTROY.toDouble()
            val frame =
                NUMBER_BITMAP_BULLET_DESTROY - ceil(state.animationBulletDestroys[n].timeShow / step).toInt()

            val rectDst = RectF(
                (state.animationBulletDestroys[n].center.x * sizeUnit - size / 2).toFloat(),
                (state.animationBulletDestroys[n].center.y * sizeUnit - size / 2).toFloat(),
                (state.animationBulletDestroys[n].center.x * sizeUnit + size / 2).toFloat(),
                (state.animationBulletDestroys[n].center.y * sizeUnit + size / 2).toFloat()
            )

            canvas.drawBitmap(bmpBulletDestroy[frame], rectSrc, rectDst, paint)
        }
    }

    private fun drawMeteorite(state: State, canvas: Canvas) {
        if (state.meteorites.size > 0)
            for (n in 0 until state.meteorites.size) {
                val meteorite = state.meteorites[n]
                val bmpMeteorite = bmpMeteorites[meteorite.view][meteorite.viewSize]
                val rectSrc = Rect(
                    0, 0,
                    bmpMeteorite.width, bmpMeteorite.height
                )

                val rectDst = RectF(
                    (meteorite.center.x * sizeUnit - meteorite.size * sizeUnit / 2).toFloat(),
                    (meteorite.center.y * sizeUnit - meteorite.size * sizeUnit / 2).toFloat(),
                    (meteorite.center.x * sizeUnit + meteorite.size * sizeUnit / 2).toFloat(),
                    (meteorite.center.y * sizeUnit + meteorite.size * sizeUnit / 2).toFloat()
                )

                canvas.drawBitmap(bmpMeteorite, rectSrc, rectDst, paint)
            }
    }

    private fun drawAnimationSpaceshipDestroy(state: State, canvas: Canvas) {
        for (animationSpaceShipDestroy in state.animationSpaceShipDestroys) {
            val step =
                animationSpaceShipDestroy.timeShowMax / NUMBER_BITMAP_SPACESHIP_DESTROY.toDouble()
            val frame =
                NUMBER_BITMAP_SPACESHIP_DESTROY - ceil(animationSpaceShipDestroy.timeShow / step).toInt()

            drawObjectWithAngle(
                bmpSpaceshipDestroy[frame],
                animationSpaceShipDestroy.angle,
                animationSpaceShipDestroy.center.x,
                animationSpaceShipDestroy.center.y,
                canvas
            )
        }
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

            for (k in 0 until state.lifes[n - 1])
                canvas.drawBitmap(
                    bmpSpaceshipLife[n - 1], rectSrc,
                    RectF(
                        200F + sizeUnit / 2.5F * k,
                        55F + 70F * (n - 1),
                        200F + sizeUnit / 2.5F * k + sizeUnit / 2.5F,
                        55F + 70F * (n - 1) + sizeUnit / 2.5F
                    ),
                    paint
                )
        }

    }

}

