package com.fiz.android.battleinthespace

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.widget.Button
import kotlin.math.ceil
import kotlin.math.min

private const val NUMBER_BITMAP_BACKGROUND = 8
private const val NUMBER_BITMAP_METEORITE_LEVEL = 4
private const val NUMBER_BITMAP_METEORITE_OPTION = 2
private const val NUMBER_BITMAP_SPACESHIP = 4
private const val NUMBER_BITMAP_SPACESHIP_LIFE = 4

private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 5

private const val DIVISION = 8

class Display(
    private val resources: Resources,
    private val context: Context,
    private val pauseButton: Button
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

    private val scale: Float = resources.displayMetrics.density
    private val sizeUnit = min(
        resources.displayMetrics.widthPixels,
        resources.displayMetrics.heightPixels
    ) / scale / DIVISION

    fun render(state: State, canvas: Canvas, canvasInfo: Canvas) {
        drawBackground(state, canvas)
        drawSpaceships(state, canvas)
        drawBullet(state, canvas)
        drawAnimationBulletDestroy(state, canvas)
        drawMeteorite(state, canvas)
        drawAnimationSpaceshipDestroy(state, canvas)
        drawInfo(state, canvasInfo)

        if (state.status == "pause")
            pauseButton.post { pauseButton.text = resources.getString(R.string.resume_game_button) }
        else
            pauseButton.post { pauseButton.text = resources.getString(R.string.pause_game_button) }
    }

    private fun drawBackground(state: State, canvas: Canvas) {
        val rectSrc = Rect(
            0, 0,
            bmpBackground[0].width, bmpBackground[0].height
        )
        for (n in 0 until state.height.toInt())
            for (k in 0 until state.width.toInt()) {
                val background = state.backgrounds[n][k]
                val rectDst = RectF(
                    n * sizeUnit, k * sizeUnit,
                    n * sizeUnit + sizeUnit, k * sizeUnit + sizeUnit
                )

                canvas.drawBitmap(bmpBackground[background], rectSrc, rectDst, paint)
            }
    }

    private fun drawSpaceships(state: State, canvas: Canvas) {
        for (player in 0 until state.countPlayers)
            if (state.spaceShips[player].inGame) {
                drawSpaceship(
                    state.spaceShips[player].angle,
                    state.spaceShips[player].centerX,
                    state.spaceShips[player].centerY,
                    player,
                    canvas
                )
                if (state.spaceShips[player].centerY + 50 >= canvas.height)
                    drawSpaceship(
                        state.spaceShips[player].angle,
                        state.spaceShips[player].centerX,
                        state.spaceShips[player].centerY - canvas.height,
                        player,
                        canvas
                    )
                if (state.spaceShips[player].centerX + 50 >= canvas.width)
                    drawSpaceship(
                        state.spaceShips[player].angle,
                        state.spaceShips[player].centerX - canvas.width,
                        state.spaceShips[player].centerY,
                        player,
                        canvas
                    )
                if ((state.spaceShips[player].centerX + 50 >= canvas.width) && (state.spaceShips[player].centerY + 50 >= canvas.height))
                    drawSpaceship(
                        state.spaceShips[player].angle,
                        state.spaceShips[player].centerX - canvas.width,
                        state.spaceShips[player].centerY - canvas.height,
                        player,
                        canvas
                    )
                if (state.spaceShips[player].centerX - 50 <= 0)
                    drawSpaceship(
                        state.spaceShips[player].angle,
                        state.spaceShips[player].centerX + canvas.width,
                        state.spaceShips[player].centerY,
                        player,
                        canvas
                    )
                if (state.spaceShips[player].centerY - 50 <= 0)
                    drawSpaceship(
                        state.spaceShips[player].angle,
                        state.spaceShips[player].centerX,
                        state.spaceShips[player].centerY + canvas.height,
                        player,
                        canvas
                    )
                if ((state.spaceShips[player].centerX - 50 <= 0) && (state.spaceShips[player].centerY - 50 <= 0))
                    drawSpaceship(
                        state.spaceShips[player].angle,
                        state.spaceShips[player].centerX + canvas.width,
                        state.spaceShips[player].centerY + canvas.height,
                        player,
                        canvas
                    )
            }
    }

    private fun drawSpaceship(angle: Double, x: Double, y: Double, player: Int, canvas: Canvas) {
        canvas.save()
        canvas.translate(x.toFloat() * sizeUnit, y.toFloat() * sizeUnit)
        canvas.rotate(angle.toFloat())

        val rectSrc = Rect(
            0, 0,
            bmpSpaceship[player].width, bmpSpaceship[player].height
        )
        val rectDst = RectF(
            -(sizeUnit / 2), -(sizeUnit / 2),
            sizeUnit / 2, sizeUnit / 2
        )

        canvas.drawBitmap(bmpSpaceship[player], rectSrc, rectDst, paint)

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
                    bullet.centerX.toFloat() * sizeUnit, bullet.centerY.toFloat() * sizeUnit,
                    bullet.centerX.toFloat() * sizeUnit + size, bullet.centerY.toFloat() * sizeUnit + size
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
            val step=state.animationBulletDestroys[n].timeShowMax/NUMBER_BITMAP_BULLET_DESTROY.toDouble()
            val frame=NUMBER_BITMAP_BULLET_DESTROY-ceil(state.animationBulletDestroys[n].timeShow/step).toInt()

            val rectDst = RectF(
                (state.animationBulletDestroys[n].centerX * sizeUnit - size / 2).toFloat(),
                (state.animationBulletDestroys[n].centerY * sizeUnit - size / 2).toFloat(),
                (state.animationBulletDestroys[n].centerX * sizeUnit - size / 2).toFloat() + size,
                (state.animationBulletDestroys[n].centerY * sizeUnit - size / 2).toFloat() + size
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
                    meteorite.centerX.toFloat() * sizeUnit,
                    meteorite.centerY.toFloat() * sizeUnit,
                    meteorite.centerX.toFloat() * sizeUnit + meteorite.size.toFloat() * sizeUnit * 2F,
                    meteorite.centerY.toFloat() * sizeUnit + meteorite.size.toFloat() * sizeUnit * 2F
                )

                canvas.drawBitmap(bmpMeteorite, rectSrc, rectDst, paint)
            }
    }


    private fun drawAnimationSpaceshipDestroy(state: State, canvas: Canvas) {
        val rectSrc = Rect(
            0, 0,
            bmpSpaceshipDestroy[0].width, bmpSpaceshipDestroy[0].height
        )
        for (n in 0 until state.animationSpaceShipDestroys.size) {
            val step=state.animationSpaceShipDestroys[n].timeShowMax/NUMBER_BITMAP_SPACESHIP_DESTROY.toDouble()
            val frame=NUMBER_BITMAP_SPACESHIP_DESTROY-ceil(state.animationSpaceShipDestroys[n].timeShow/step).toInt()
            val rectDst = RectF(
                state.animationSpaceShipDestroys[n].centerX.toFloat() - sizeUnit/2,
                state.animationSpaceShipDestroys[n].centerY.toFloat() - sizeUnit/2,
                state.animationSpaceShipDestroys[n].centerX.toFloat() - sizeUnit/2 + sizeUnit,
                state.animationSpaceShipDestroys[n].centerY.toFloat() - sizeUnit/2 + sizeUnit
            )

            canvas.drawBitmap(
                bmpSpaceshipDestroy[frame], rectSrc,
                rectDst, paint
            )
        }
    }


    private fun drawInfo(state: State, canvas: Canvas) {
        val paintFont = Paint()
        val rectSrc = Rect(
            0, 0,
            bmpSpaceshipLife[0].width, bmpSpaceshipLife[0].height
        )

        paintFont.textSize = 24F
        if (state.countPlayers >= 1) {
            paintFont.color = Color.GREEN
            canvas.drawText("Player 1", 0F, 0F+sizeUnit/2.5F/2, paintFont)

            for (n in 0 until state.lifes[0])
                canvas.drawBitmap(
                    bmpSpaceshipLife[0],rectSrc,
                    RectF(
                        200F + sizeUnit/2.5F* n, 0F,
                        200F + sizeUnit/2.5F *n + sizeUnit/2.5F, 0F + sizeUnit/2.5F
                    ),
                    paint
                )
        }

        if (state.countPlayers >= 2) {
            paintFont.color = Color.CYAN
            canvas.drawText("Player 2", 0F, 70F+sizeUnit/2.5F/2, paintFont)

            for (n in 0 until state.lifes[1])
                canvas.drawBitmap(
                    bmpSpaceshipLife[1],rectSrc,
                    RectF(
                        200F + sizeUnit/2.5F* n, 70F,
                        200F + sizeUnit/2.5F *n + sizeUnit/2.5F, 70F + sizeUnit/2.5F
                    ),
                    paint
                )
        }

        if (state.countPlayers >= 3) {
            paintFont.color = Color.YELLOW
            canvas.drawText("Player 3", 0F, 140F+sizeUnit/2.5F/2, paintFont)

            for (n in 0 until state.lifes[2])
                canvas.drawBitmap(
                    bmpSpaceshipLife[2],rectSrc,
                    RectF(
                        200F + sizeUnit/2.5F* n, 140F,
                        200F + sizeUnit/2.5F *n + sizeUnit/2.5F, 140F + sizeUnit/2.5F
                    ),
                    paint
                )
        }

        if (state.countPlayers >= 4) {
            paintFont.color = Color.YELLOW
            canvas.drawText("Player 4", 0F, 210F+sizeUnit/2.5F/2, paintFont)

            for (n in 0 until state.lifes[3])
                canvas.drawBitmap(
                    bmpSpaceshipLife[3],rectSrc,
                    RectF(
                        200F + sizeUnit/2.5F* n, 210F,
                        200F + sizeUnit/2.5F *n + sizeUnit/2.5F, 210F + sizeUnit/2.5F
                    ),
                    paint
                )
        }
    }

}

