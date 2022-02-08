package com.fiz.android.battleinthespace.game.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fiz.android.battleinthespace.R

private const val NUMBER_BITMAP_METEORITE_LEVEL = 4
private const val NUMBER_BITMAP_SPACESHIP = 4
private const val NUMBER_BITMAP_SPACESHIP_LIFE = 4

private const val NUMBER_BITMAP_BULLET_DESTROY = 3
private const val NUMBER_BITMAP_SPACESHIP_DESTROY = 7


open class BitmapLoad(private val context: Context) {
    val bmpBackground: Array<Bitmap> by lazy(::initBmpBackground)
    val bmpBullet: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bullet)
    val bmpBulletDestroy: Array<Bitmap> by lazy(::initBmpBulletDestroy)
    val bmpMeteorites: Array<Array<Bitmap>> by lazy(::initBmpMeteorite)
    val bmpSpaceship: Array<Bitmap> by lazy(::initBmpSpaceship)
    val bmpSpaceshipFly: Array<Bitmap> by lazy(::initBmpSpaceshipFly)
    val bmpSpaceshipDestroy: Array<Bitmap> by lazy(::initBmpSpaceshipDestroy)
    val bmpSpaceshipLife: Array<Bitmap> by lazy(::initBmpSpaceshipLife)
    private fun initBmpBackground(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_BITMAP_BACKGROUND)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "background$i",
                    "drawable", context.packageName
                )
            )
        return result
    }

    private fun initBmpBulletDestroy(): Array<Bitmap> {
        val temp: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bullet_destroy)
        var result: Array<Bitmap> = emptyArray()
        val size = temp.width / NUMBER_BITMAP_BULLET_DESTROY
        for (i in 0 until NUMBER_BITMAP_BULLET_DESTROY)
            result += Bitmap.createBitmap(temp, i * size, 0, size, size)
        return result
    }

    private fun initBmpMeteorite(): Array<Array<Bitmap>> {
        var result: Array<Array<Bitmap>> = emptyArray()
        for (i in 1..NUMBER_BITMAP_METEORITE_OPTION) {
            var row: Array<Bitmap> = emptyArray()
            for (j in 1..NUMBER_BITMAP_METEORITE_LEVEL)
                row += BitmapFactory.decodeResource(
                    context.resources, context.resources.getIdentifier(
                        "meteorite$i$j",
                        "drawable", context.packageName
                    )
                )
            result += row
        }
        return result
    }

    private fun initBmpSpaceship(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_BITMAP_SPACESHIP)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "spaceship$i",
                    "drawable", context.packageName
                )
            )
        return result
    }

    private fun initBmpSpaceshipFly(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_BITMAP_SPACESHIP)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "spaceship_fly$i",
                    "drawable", context.packageName
                )
            )
        return result
    }

    private fun initBmpSpaceshipDestroy(): Array<Bitmap> {
        val temp: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_destroy)
        val size = temp.width / NUMBER_BITMAP_SPACESHIP_DESTROY
        var result: Array<Bitmap> = emptyArray()
        for (i in 0 until NUMBER_BITMAP_SPACESHIP_DESTROY)
            result += Bitmap.createBitmap(temp, i * size, 0, size, size)
        return result
    }

    private fun initBmpSpaceshipLife(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_BITMAP_SPACESHIP_LIFE)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "spaceship_life$i",
                    "drawable", context.packageName
                )
            )
        return result
    }
}