package com.fiz.battleinthespace.feature_gamescreen.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fiz.battleinthespace.feature_gamescreen.R
import com.fiz.battleinthespace.feature_gamescreen.ui.NUMBER_BITMAP_METEORITE_OPTION
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val NUMBER_IMAGES_FIGURE = 5

const val NUMBER_BITMAP_BACKGROUND = 8
private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_METEORITE_LEVEL = 4
private const val NUMBER_BITMAP_SPACESHIP = 4

private const val NUMBER_BITMAP_SPACESHIP_LIFE = 4

private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 7

@Singleton
class BitmapRepository @Inject constructor(@ApplicationContext context: Context) {

    val bmpBackground: List<Bitmap> by lazy {
        val result: MutableList<Bitmap> = mutableListOf()
        for (i in 1..NUMBER_BITMAP_BACKGROUND)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "background$i",
                    "drawable", context.packageName
                )
            )
        result
    }

    val bmpWeapon: List<Bitmap> by lazy {
        val result: MutableList<Bitmap> = mutableListOf()
        result += BitmapFactory.decodeResource(context.resources, R.drawable.bullet)
        result += BitmapFactory.decodeResource(context.resources, R.drawable.double_bullet)
        result += BitmapFactory.decodeResource(context.resources, R.drawable.missile)
        result += BitmapFactory.decodeResource(context.resources, R.drawable.ball)
        result
    }

    val bmpBulletDestroy: List<Bitmap> by lazy {
        val temp: Bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.bullet_destroy)
        val result: MutableList<Bitmap> = mutableListOf()
        val size = temp.width / NUMBER_BITMAP_BULLET_DESTROY
        for (i in 0 until NUMBER_BITMAP_BULLET_DESTROY)
            result += Bitmap.createBitmap(temp, i * size, 0, size, size)
        result
    }

    val bmpMeteorites: List<List<Bitmap>> by lazy {
        val result: MutableList<MutableList<Bitmap>> = mutableListOf()
        for (i in 1..NUMBER_BITMAP_METEORITE_OPTION) {
            val row: MutableList<Bitmap> = mutableListOf()
            for (j in 1..NUMBER_BITMAP_METEORITE_LEVEL)
                row += BitmapFactory.decodeResource(
                    context.resources, context.resources.getIdentifier(
                        "meteorite$i$j",
                        "drawable", context.packageName
                    )
                )
            result += row
        }
        result
    }

    val bmpSpaceship: List<Bitmap> by lazy {
        val result: MutableList<Bitmap> = mutableListOf()
        for (i in 1..NUMBER_BITMAP_SPACESHIP)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "spaceship$i",
                    "drawable", context.packageName
                )
            )
        result
    }

    val bmpSpaceshipFly: List<Bitmap> by lazy {
        val result: MutableList<Bitmap> = mutableListOf()
        for (i in 1..NUMBER_BITMAP_SPACESHIP)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "spaceship_fly$i",
                    "drawable", context.packageName
                )
            )
        result
    }

    val bmpSpaceshipDestroy: List<Bitmap> by lazy {
        val temp: Bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_destroy)
        val size = temp.width / NUMBER_BITMAP_SPACESHIP_DESTROY
        val result: MutableList<Bitmap> = mutableListOf()
        for (i in 0 until NUMBER_BITMAP_SPACESHIP_DESTROY)
            result += Bitmap.createBitmap(temp, i * size, 0, size, size)
        result
    }

    val bmpSpaceshipLife: List<Bitmap> by lazy {
        val result: MutableList<Bitmap> = mutableListOf()
        for (i in 1..NUMBER_BITMAP_SPACESHIP_LIFE)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "spaceship_life$i",
                    "drawable", context.packageName
                )
            )
        result
    }
}