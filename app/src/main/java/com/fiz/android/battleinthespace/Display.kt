package com.fiz.android.battleinthespace

import android.content.Context
import android.content.res.Resources
import android.graphics.*

private const val NUMBER_BITMAP_BACKGROUND = 8
private const val NUMBER_BITMAP_METEORITE_LEVEL = 2
private const val NUMBER_BITMAP_METEORITE_OPTION = 4
private const val NUMBER_BITMAP_SPACESHIP = 4
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 5
private const val NUMBER_BITMAP_SPACESHIP_LIFE = 4
private const val NUMBER_BITMAP_BULLET_DESTROY = 3

private const val NUMBER_IMAGES_FIGURE = 5
private const val TIMES_BREATH_LOSE = 60
private const val NUMBER_COLUMNS_IMAGES_FON = 4
private const val NUMBER_ROWS_IMAGES_FON = 4

class Display(
    private val resources: Resources,
    private val context: Context
) {
    private val paint: Paint = Paint()

    private val bmpBackground: Array<Bitmap> by lazy(::initBmpBackground)
    private val bmpBullet: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bullet)

    private val bmpBulletDestroy: Array<Bitmap> by lazy(::initBmpBulletDestroy)
    private fun initBmpBulletDestroy(): Array<Bitmap> {
        val temp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bullet_destroy)
        var result: Array<Bitmap> = emptyArray()
        for (i in 0 until NUMBER_BITMAP_BULLET_DESTROY)
            result += Bitmap.createBitmap(temp, i * 50, 0, 5, 5)
        return result
    }

    private val bmpMeteorite: Array<Array<Bitmap>> by lazy(::initBmpMeteorite)
    private val bmpSpaceship: Array<Bitmap> by lazy(::initBmpSpaceship)

    private val bmpSpaceshipDestroy: Array<Bitmap> by lazy(::initBmpSpaceshipDestroy)
    private fun initBmpSpaceshipDestroy(): Array<Bitmap> {
        val temp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.spaceship_destroy)
        var result: Array<Bitmap> = emptyArray()
        for (i in 0 until NUMBER_BITMAP_SPACESHIP_DESTROY)
            result += Bitmap.createBitmap(temp, i * 50, 0, 50, 50)
        return result
    }

    private val bmpSpaceshipLife: Array<Bitmap> by lazy(::initBmpSpaceshipLife)

    private val tile = bmpBackground[0].width / NUMBER_COLUMNS_IMAGES_FON
    private val newTile = (tile / 1.5).toFloat()


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

    private fun initBmpMeteorite(): Array<Array<Bitmap>> {
        var result: Array<Array<Bitmap>> = emptyArray()
        for (i in 0 until NUMBER_BITMAP_METEORITE_OPTION) {
            var result1: Array<Bitmap> = emptyArray()
            for (j in 0 until NUMBER_BITMAP_METEORITE_LEVEL)
                result1 += BitmapFactory.decodeResource(
                    resources, resources.getIdentifier(
                        "meteorite$i$j",
                        "drawable", context.packageName
                    )
                )
            result += result1
        }
        return result
    }

    private fun Ust(angle: Double, x: Double, y: Double, canvas: Canvas) {

//    var fi: Double
//    var t: TXFORM
//
//    fi = angle / 180 * Math.PI
//    t.eM11: = cos(fi);
//    t.eM12: = -sin(fi);
//    t.eM21: = sin(fi);
//    t.eM22: = cos(fi);
//    t.eDx = x
//    t.eDy = y
//    SetWorldTransform(Pole.Canvas.Handle, t);

        val fi = angle / 180.0 * Math.PI
        canvas.translate(x.toFloat(), y.toFloat())
        canvas.rotate(fi.toFloat())
    }

    private fun Risov(angle: Double, x: Double, y: Double, Nom: Int, canvas: Canvas) {
        Ust(angle, x, y, canvas)

        canvas.drawBitmap(
            bmpSpaceship[Nom],
            Rect(0, 0, bmpSpaceship[Nom].width, bmpSpaceship[Nom].height),
            RectF(-25F, -25F, -25F + 50, -25F + 50),
            paint
        )
        canvas.restore()
        //Ust(0.0, 0.0, 0.0,canvas)
    }

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

    private fun drawInfo(state: State, canvas: Canvas) {
        val paintFont: Paint = Paint()
        paintFont.textSize = 12F
        if (state.Gam.Players >= 1) {
            paintFont.color = Color.GREEN
            canvas.drawText("Player 1", 30F, 30F, paintFont)

            for (n in 0..state.Gam.lifes[0])
                canvas.drawBitmap(
                    bmpSpaceshipLife[0],
                    Rect(0, 0, bmpSpaceshipLife[0].width, bmpSpaceshipLife[0].height),
                    RectF(30F + 25 * n, 45F, 30F + 25 * n + 20, 45F + 20),
                    paint
                )
        }

        if (state.Gam.Players >= 2) {
            paintFont.color = Color.CYAN
            canvas.drawText("Player 2", 60F, 30F, paintFont)

            for (n in 0..state.Gam.lifes[0])
                canvas.drawBitmap(
                    bmpSpaceshipLife[0],
                    Rect(0, 0, bmpSpaceshipLife[0].width, bmpSpaceshipLife[0].height),
                    RectF(60F + 25 * n, 45F, 60F + 25 * n + 20, 45F + 20),
                    paint
                )
        }

        if (state.Gam.Players >= 3) {
            paintFont.color = Color.YELLOW
            canvas.drawText("Player 3", 30F, 60F, paintFont)

            for (n in 0..state.Gam.lifes[0])
                canvas.drawBitmap(
                    bmpSpaceshipLife[0],
                    Rect(0, 0, bmpSpaceshipLife[0].width, bmpSpaceshipLife[0].height),
                    RectF(30F + 25 * n, 85F, 30F + 25 * n + 20, 85F + 20),
                    paint
                )
        }

        if (state.Gam.Players >= 4) {
            paintFont.color = Color.YELLOW
            canvas.drawText("Player 4", 60F, 60F, paintFont)

            for (n in 0..state.Gam.lifes[0])
                canvas.drawBitmap(
                    bmpSpaceshipLife[0],
                    Rect(0, 0, bmpSpaceshipLife[0].width, bmpSpaceshipLife[0].height),
                    RectF(60F + 25 * n, 85F, 60F + 25 * n + 20, 85F + 20),
                    paint
                )
        }
    }

    fun render(state: State, canvas: Canvas, canvasInfo: Canvas) {
        drawBackground(state, canvas)
        drawSpaceship(state, canvas)
        drawBullet(state, canvas)
        drawAnimationBulletDestroy(state, canvas)
        drawMeteorite(state, canvas)
        drawAnimationSpaceshipDestroy(state, canvas)
        drawInfo(state, canvasInfo)

//    scoresTextView.text = "${resources.getString(R.string.scores_game_textview)}: ${
//      state.scores.toString().padStart(6, '0')
//    }"
//    recordTextView.text = "${resources.getString(R.string.record_game_textview)}: ${
//      state.record.toString().padStart(6, '0')
//    }"
//
//    if (state.status == "pause")
//      pauseButton.text = resources.getString(R.string.resume_game_button)
//    else
//      pauseButton.text = resources.getString(R.string.pause_game_button)

//    if (state.status != "pause") {
//      val sec: Int = if (!state.character.breath)
//        max(
//          TIMES_BREATH_LOSE - ceil(
//            (System.currentTimeMillis().toDouble() - state.character
//              .timeBreath) / 1000
//          ),
//          0.0
//        ).toInt()
//      else
//        TIMES_BREATH_LOSE

//      if (!state.character.breath) {
//        if (!breathTextview.isVisible) {
//          infoBreathTextview.post { infoBreathTextview.visibility = View.VISIBLE }
//          breathTextview.post { breathTextview.visibility = View.VISIBLE }
//        }
//        breathTextview.post { breathTextview.text = "$sec" }
//      } else if (breathTextview.isVisible) {
//        infoBreathTextview.post { infoBreathTextview.visibility = View.INVISIBLE }
//        breathTextview.post { breathTextview.visibility = View.INVISIBLE }
//      }
//      val cl = ((floor(sec.toDouble()) * 255) / TIMES_BREATH_LOSE).toInt()
//      breathTextview.post {breathTextview.setBackgroundColor(Color.rgb(255, cl, cl)) }

    }

    private fun drawAnimationSpaceshipDestroy(state: State, canvas: Canvas) {
        var nach = 0
        for (n in nach..state.Gam.animationSpaceshipDestroy.size - 1) {
            canvas.drawBitmap(
                bmpSpaceshipDestroy[state.Gam.animationSpaceshipDestroy[n].Kadr],
                Rect(
                    0,
                    0,
                    bmpSpaceshipDestroy[state.Gam.animationSpaceshipDestroy[n].Kadr].width,
                    bmpSpaceshipDestroy[state.Gam.animationSpaceshipDestroy[n].Kadr].height
                ),
                RectF(
                    Math.floor(state.Gam.animationSpaceshipDestroy[n].X - 25).toFloat(),
                    Math.floor(state.Gam.animationSpaceshipDestroy[n].Y - 25).toFloat(),
                    Math.floor(state.Gam.animationSpaceshipDestroy[n].X - 25).toFloat() + 50,
                    Math.floor(state.Gam.animationSpaceshipDestroy[n].Y - 25).toFloat() + 50
                ),
                paint
            )
            if (state.Gam.Raz1 == 0)
                state.Gam.animationSpaceshipDestroy[n].Kadr += 1
            state.Gam.Raz1 += 1;
            if (state.Gam.Raz1 > 7)
                state.Gam.Raz1 = 0
        }

        var tempArrayAnimationSpaceshipDestroy: Array<AnimationSpaceshipDestroy> = emptyArray()
        for (n in 0..state.Gam.animationSpaceshipDestroy.size - 1)
            if (state.Gam.animationSpaceshipDestroy[n].Kadr <= 7)
                tempArrayAnimationSpaceshipDestroy += state.Gam.animationSpaceshipDestroy[n]
        state.Gam.animationSpaceshipDestroy = tempArrayAnimationSpaceshipDestroy

    }

    private fun drawMeteorite(state: State, canvas: Canvas) {
        if (state.Gam.meteorites.size > 0)
            for (n in 0..state.Gam.meteorites.size - 1) {
                canvas.drawBitmap(
                    bmpMeteorite[state.Gam.meteorites[n].TipRazmer][state.Gam.meteorites[n].Tip],
                    Rect(
                        0,
                        0,
                        bmpMeteorite[state.Gam.meteorites[n].TipRazmer][state.Gam.meteorites[n].Tip].width,
                        bmpMeteorite[state.Gam.meteorites[n].TipRazmer][state.Gam.meteorites[n].Tip].height
                    ),
                    RectF(
                        state.Gam.meteorites[n].X.toFloat(),
                        state.Gam.meteorites[n].Y.toFloat(),
                        state.Gam.meteorites[n].X.toFloat() + state.Gam.meteorites[n].Razmer * 2,
                        state.Gam.meteorites[n].Y.toFloat() + state.Gam.meteorites[n].Razmer * 2
                    ),
                    paint
                )
            }
    }

    private fun drawAnimationBulletDestroy(state: State, canvas: Canvas) {
        var k: Int
        for (n in 0..state.Gam.animationBulletDestroy.size - 1) {
            k = state.Gam.animationBulletDestroy[n].Kadr
            if (state.Gam.animationBulletDestroy[n].Kadr == 3)
                k = 1
            if (state.Gam.animationBulletDestroy[n].Kadr == 4)
                k = 0

            canvas.drawBitmap(
                bmpBulletDestroy[k],
                Rect(
                    0,
                    0,
                    bmpBulletDestroy[k].width,
                    bmpBulletDestroy[k].height
                ),
                RectF(
                    Math.floor(state.Gam.animationBulletDestroy[n].X - 2).toFloat(),
                    Math.floor(state.Gam.animationBulletDestroy[n].Y - 2).toFloat(),
                    Math.floor(state.Gam.animationBulletDestroy[n].X - 2).toFloat() + 5,
                    Math.floor(state.Gam.animationBulletDestroy[n].Y - 2).toFloat() + 5
                ),
                paint
            )
            if (state.Gam.Raz == 0)
                state.Gam.animationBulletDestroy[n].Kadr += 1
            state.Gam.Raz += 1
            if (state.Gam.Raz > 5)
                state.Gam.Raz = 0
        }

        var tempArrayAnimationBulletDestroy: Array<AnimationBulletDestroy> = emptyArray()
        for (n in 0..state.Gam.animationBulletDestroy.size - 1)
            if (state.Gam.animationBulletDestroy[n].Kadr <= 4)
                tempArrayAnimationBulletDestroy += state.Gam.animationBulletDestroy[n]
        state.Gam.animationBulletDestroy = tempArrayAnimationBulletDestroy
    }


    private fun drawBackground(state: State, canvas: Canvas) {
        for (n in 0..(canvas.width / 50))
            for (k in 0..(canvas.height / 50))

                canvas.drawBitmap(
                    bmpBackground[state.Gam.background[n][k]],
                    Rect(
                        0,
                        0,
                        bmpBackground[state.Gam.background[n][k]].width,
                        bmpBackground[state.Gam.background[n][k]].height
                    ),
                    RectF(n * 50F, k * 50F, n * 50 + 50F, k * 50 + 50F),
                    paint
                )
//
//    for (y in 0 until grid.height)
//      for (x in 0 until grid.width)
//        if (grid.space[y][x].block != 0) {
//          val screenX = x * newTile
//          val screenY = y * newTile
//          val offset: Point = getOffset(grid.space[y][x])
//          canvas.drawBitmap(
//            bmpSpaceship[0/*grid.space[y][x].block - 1*/],
//            Rect(
//              offset.x * tile,
//              offset.y * tile,
//              offset.x * tile + tile,
//              offset.y * tile + tile
//            ),
//            RectF(screenX, screenY, screenX + newTile, screenY + newTile),
//            paint
//          )
//        }
    }

    private fun drawSpaceship(state: State, canvas: Canvas) {
        for (nom in 0 until state.Gam.Players)
            if (state.Gam.spaceship[nom].InGame == true) {
                Risov(
                    state.Gam.spaceship[nom].Angle,
                    state.Gam.spaceship[nom].CX,
                    state.Gam.spaceship[nom].CY,
                    nom,
                    canvas
                );
                if (state.Gam.spaceship[nom].CY + 50 >= canvas.height)
                    Risov(
                        state.Gam.spaceship[nom].Angle,
                        state.Gam.spaceship[nom].CX,
                        state.Gam.spaceship[nom].CY - canvas.height,
                        nom,
                        canvas
                    )
                if (state.Gam.spaceship[nom].CX + 50 >= canvas.width)
                    Risov(
                        state.Gam.spaceship[nom].Angle,
                        state.Gam.spaceship[nom].CX - canvas.width,
                        state.Gam.spaceship[nom].CY,
                        nom,
                        canvas
                    );
                if ((state.Gam.spaceship[nom].CX + 50 >= canvas.width) && (state.Gam.spaceship[nom].CY + 50 >= canvas.height))
                    Risov(
                        state.Gam.spaceship[nom].Angle,
                        state.Gam.spaceship[nom].CX - canvas.width,
                        state.Gam.spaceship[nom].CY - canvas.height,
                        nom,
                        canvas
                    );
                if (state.Gam.spaceship[nom].CX - 50 <= 0)
                    Risov(
                        state.Gam.spaceship[nom].Angle,
                        state.Gam.spaceship[nom].CX + canvas.width,
                        state.Gam.spaceship[nom].CY,
                        nom,
                        canvas
                    );
                if (state.Gam.spaceship[nom].CY - 50 <= 0)
                    Risov(
                        state.Gam.spaceship[nom].Angle,
                        state.Gam.spaceship[nom].CX,
                        state.Gam.spaceship[nom].CY + canvas.height,
                        nom,
                        canvas
                    );
                if ((state.Gam.spaceship[nom].CX - 50 <= 0) && (state.Gam.spaceship[nom].CY - 50 <= 0))
                    Risov(
                        state.Gam.spaceship[nom].Angle,
                        state.Gam.spaceship[nom].CX + canvas.width,
                        state.Gam.spaceship[nom].CY + canvas.height,
                        nom,
                        canvas
                    );
            }
    }

    private fun drawBullet(state: State, canvas: Canvas) {
        if (state.Gam.bullet.size > 0)
            for (n in 0 until state.Gam.bullet.size)
                canvas.drawBitmap(
                    bmpBullet,
                    Rect(
                        0,
                        0,
                        bmpBullet.width,
                        bmpBullet.height
                    ),
                    RectF(
                        Math.floor(state.Gam.bullet[n].X).toFloat(),
                        Math.floor(state.Gam.bullet[n].Y).toFloat(),
                        Math.floor(state.Gam.bullet[n].X).toFloat() + 5,
                        Math.floor(state.Gam.bullet[n].Y).toFloat() + 5
                    ),
                    paint
                )
    }
}
