package com.fiz.android.battleinthespace

import kotlin.math.abs
import kotlin.math.pow
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

    fun overlapCircle(
        centerX1: Double,
        centerY1: Double,
        size1: Double,
        centerX2: Double,
        centerY2: Double,
        size2: Double
    ): Boolean {
        val radius1 = (size1 / 2)
        val radius2 = (size2 / 2)
        var radius = radius1 + radius2
        radius = radius.pow(2)
        val globalCenterX1 = if (centerX1 - radius1 < 0) centerX1 + width else centerX1
        val globalCenterX2 = if (centerX2 - radius2 < 0) centerX2 + width else centerX2
        val globalCenterY1 = if (centerY1 - radius1 < 0) centerY1 + height else centerY1
        val globalCenterY2 = if (centerY2 - radius2 < 0) centerY2 + height else centerY2
        return radius > (globalCenterX1 - globalCenterX2).pow(2) + (globalCenterY1 - globalCenterY2).pow(2)
    }

    fun overlapRectangle(
        centerX1: Double,
        centerY1: Double,
        size1: Double,
        centerX2: Double,
        centerY2: Double,
        size2: Double
    ): Boolean {
        val halfSize1 = (size1 / 2)
        val halfSize2 = (size2 / 2)
        val (l1, r1) = changeXifDoubleBorder(centerX1 - halfSize1, centerX1 + halfSize1)
        val (l2, r2) = changeXifDoubleBorder(centerX2 - halfSize2, centerX2 + halfSize2)
        val (u1, d1) = changeYifDoubleBorder(centerY1 - halfSize1, centerY1 + halfSize1)
        val (u2, d2) = changeYifDoubleBorder(centerY2 - halfSize2, centerY2 + halfSize2)
        if ((r1 < l2) || (l1 > r2)) return false
        if ((d1 < u2) || (u1 > d2)) return false
        return true
    }

    private fun changeXifDoubleBorder(
        coordinateCenterX1: Double,
        coordinateCenterX2: Double
    ): List<Double> {
        return when {
            (coordinateCenterX1 < 0.0) || (coordinateCenterX2 < 0.0) -> listOf(
                coordinateCenterX1 + width,
                coordinateCenterX2 + width
            )
            else -> listOf(coordinateCenterX1, coordinateCenterX2)
        }
    }

    private fun changeYifDoubleBorder(
        coordinateCenterY1: Double,
        coordinateCenterY2: Double
    ): List<Double> {
        return when {
            (coordinateCenterY1 < 0.0) || (coordinateCenterY2 < 0.0) -> listOf(
                coordinateCenterY1 + height,
                coordinateCenterY2 + height
            )
            else -> listOf(coordinateCenterY1, coordinateCenterY2)
        }
    }

    private fun changeXifBorder(coordinateCenterX: Double): Double {
        return when {
            (coordinateCenterX < 0.0) -> coordinateCenterX + width
            else -> coordinateCenterX
        }
    }

    private fun changeYifBorder(coordinateCenterY: Double): Double {
        return when {
            (coordinateCenterY < 0.0) -> coordinateCenterY + height
            else -> coordinateCenterY
        }
    }
}