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
        center1:Vec,
        size1: Double,
        center2:Vec,
        size2: Double
    ): Boolean {
        val halfSize1 = (size1 / 2)
        val halfSize2 = (size2 / 2)
        val l1 = changeXifBorder(center1.x - halfSize1)
        val l2 = changeXifBorder(center2.x - halfSize2)
        val r1 = changeXifBorder(center1.x + halfSize1)
        val r2 = changeXifBorder(center2.x + halfSize2)
        val u1 = changeYifBorder(center1.y - halfSize1)
        val u2 = changeYifBorder(center2.y - halfSize2)
        val d1 = changeYifBorder(center1.y + halfSize1)
        val d2 = changeYifBorder(center2.y + halfSize2)
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
        center1:Vec,
        size1: Double,
        center2:Vec,
        size2: Double
    ): Boolean {
        val radius1 = (size1 / 2)
        val radius2 = (size2 / 2)
        var radius = radius1 + radius2
        radius = radius.pow(2)
        val globalcenter1 = Vec(if (center1.x - radius1 < 0) center1.x+ width else center1.x,
            if (center1.y - radius1 < 0) center1.y + height else center1.y)
        val globalcenter2 = Vec(if (center2.x - radius2 < 0) center2.x + width else center2.x,
            if (center2.y - radius2 < 0) center2.y + height else center2.y)
        return radius > (globalcenter1.x - globalcenter2.x).pow(2) + (globalcenter1.y - globalcenter2.y).pow(2)
    }

    fun overlapRectangle(
        center1:Vec,
        size1: Double,
        center2:Vec,
        size2: Double
    ): Boolean {
        val halfSize1 = (size1 / 2)
        val halfSize2 = (size2 / 2)
        val (l1, r1) = changeXifDoubleBorder(center1.x - halfSize1, center1.x + halfSize1)
        val (l2, r2) = changeXifDoubleBorder(center2.x - halfSize2, center2.x + halfSize2)
        val (u1, d1) = changeYifDoubleBorder(center1.y - halfSize1, center1.y + halfSize1)
        val (u2, d2) = changeYifDoubleBorder(center2.y - halfSize2, center2.y + halfSize2)
        if ((r1 < l2) || (l1 > r2)) return false
        if ((d1 < u2) || (u1 > d2)) return false
        return true
    }

    private fun changeXifDoubleBorder(
        centerX1: Double,
        centerX2: Double
    ): List<Double> {
        return when {
            (centerX1 < 0.0) || (centerX2 < 0.0) -> listOf(
                centerX1 + width,
                centerX2 + width
            )
            else -> listOf(centerX1, centerX2)
        }
    }

    private fun changeYifDoubleBorder(
        centerY1: Double,
        centerY2: Double
    ): List<Double> {
        return when {
            (centerY1 < 0.0) || (centerY2 < 0.0) -> listOf(
                centerY1+ height,
                centerY2 + height
            )
            else -> listOf(centerY1, centerY2)
        }
    }

    private fun changeXifBorder(centerX: Double): Double {
        return when {
            (centerX < 0.0) -> centerX + width
            else -> centerX
        }
    }

    private fun changeYifBorder(centerY: Double): Double {
        return when {
            (centerY < 0.0) -> centerY + height
            else -> centerY
        }
    }
}