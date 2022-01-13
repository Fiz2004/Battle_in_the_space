package com.fiz.android.battleinthespace

import kotlin.math.abs
import kotlin.math.sign

object Physics {

    var width: Double = 0.0
    var height: Double = 0.0

    fun createWorld(width: Double, height: Double) {
        this.width = width
        this.height = height
    }

    fun getSpeedFirstAfterKickback(speedFirst: Double, speedSecond: Double): Double {
        if (sign(speedFirst) != sign(speedSecond) && speedFirst != 0.0)
            return -speedFirst

        if (abs(speedFirst) > abs(speedSecond))
            return speedFirst / 2

        return speedFirst + speedSecond / 2
    }

    fun overlap(
        centerX1: Double,
        centerY1: Double,
        size1: Double,
        centerX2: Double,
        centerY2: Double,
        size2: Double
    ): Boolean {
        val halfSize1 = (size1 / 2)
        val halfSize2 = (size2 / 2)
        val l1 = changeXifBorder(centerX1 - halfSize1)
        val l2 = changeXifBorder(centerX2 - halfSize2)
        val r1 = changeXifBorder(centerX1 + halfSize1)
        val r2 = changeXifBorder(centerX2 + halfSize2)
        val u1 = changeYifBorder(centerY1 - halfSize1)
        val u2 = changeYifBorder(centerY2 - halfSize2)
        val d1 = changeYifBorder(centerY1 + halfSize1)
        val d2 = changeYifBorder(centerY2 + halfSize2)
        val lu1 = l1 in l2..r2 && u1 in u2..d2
        val ru1 = r1 in l2..r2 && u1 in u2..d2
        val ld1 = l1 in l2..r2 && d1 in u2..d2
        val rd1 = r1 in l2..r2 && d1 in u2..d2

        val lu2 = l2 in l1..r1 && u2 in u1..d1
        val ru2 = r2 in l1..r1 && u2 in u1..d1
        val ld2 = l2 in l1..r1 && d2 in u1..d1
        val rd2 = r2 in l1..r1 && d2 in u1..d1

        return lu1 || ru1 || ld1 || rd1 || lu2 || ru2 || ld2 || rd2
    }

    private fun changeXifBorder(coordinateCenterX: Double): Double {
        return when {
            (coordinateCenterX > width) -> coordinateCenterX - width
            (coordinateCenterX < 0) -> coordinateCenterX + width
            else -> coordinateCenterX
        }
    }

    private fun changeYifBorder(coordinateCenterY: Double): Double {
        return when {
            (coordinateCenterY > height) -> coordinateCenterY - height
            (coordinateCenterY < 0) -> coordinateCenterY + height
            else -> coordinateCenterY
        }
    }
}