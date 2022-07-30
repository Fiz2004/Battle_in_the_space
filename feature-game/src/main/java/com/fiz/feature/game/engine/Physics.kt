package com.fiz.feature.game.engine

import com.fiz.battleinthespace.common.Vec
import kotlin.math.*

object Physics {

    var width: Double = 0.0
    var height: Double = 0.0

    fun createWorld(width: Int, height: Int) {
        Physics.width = width.toDouble()
        Physics.height = height.toDouble()
    }

    fun overlap(
        center1: Vec,
        size1: Double,
        center2: Vec,
        size2: Double
    ): Boolean {
        val halfSize1 = (size1 / 2)
        val halfSize2 = (size2 / 2)
        val l1 = changeCoorIfBorder(center1.x - halfSize1, width)
        val l2 = changeCoorIfBorder(center2.x - halfSize2, width)
        val r1 = changeCoorIfBorder(center1.x + halfSize1, width)
        val r2 = changeCoorIfBorder(center2.x + halfSize2, width)
        val u1 = changeCoorIfBorder(center1.y - halfSize1, height)
        val u2 = changeCoorIfBorder(center2.y - halfSize2, height)
        val d1 = changeCoorIfBorder(center1.y + halfSize1, height)
        val d2 = changeCoorIfBorder(center2.y + halfSize2, height)
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
        center1: Vec,
        size1: Double,
        center2: Vec,
        size2: Double
    ): Boolean {
        val radius1 = (size1 / 2)
        val radius2 = (size2 / 2)
        var radius = radius1 + radius2
        radius = radius.pow(2)
        val globalCenter1 = Vec(
            if (center1.x - radius1 < 0) center1.x + width else center1.x,
            if (center1.y - radius1 < 0) center1.y + height else center1.y
        )
        val globalCenter2 = Vec(
            if (center2.x - radius2 < 0) center2.x + width else center2.x,
            if (center2.y - radius2 < 0) center2.y + height else center2.y
        )
        return radius > (globalCenter1.x - globalCenter2.x).pow(2) + (globalCenter1.y - globalCenter2.y).pow(2)
    }

    fun overlapRectangle(
        center1: Vec,
        size1: Double,
        center2: Vec,
        size2: Double
    ): Boolean {
        val halfSize1 = (size1 / 2)
        val halfSize2 = (size2 / 2)
        val (l1, r1) = changeCoorIfDoubleBorder(center1.x - halfSize1, center1.x + halfSize1, width)
        val (l2, r2) = changeCoorIfDoubleBorder(center2.x - halfSize2, center2.x + halfSize2, width)
        val (u1, d1) = changeCoorIfDoubleBorder(center1.y - halfSize1, center1.y + halfSize1, height)
        val (u2, d2) = changeCoorIfDoubleBorder(center2.y - halfSize2, center2.y + halfSize2, height)
        if ((r1 < l2) || (l1 > r2)) return false
        if ((d1 < u2) || (u1 > d2)) return false
        return true
    }

    private fun changeCoorIfDoubleBorder(
        center1: Double,
        center2: Double, border: Double
    ): List<Double> {
        return when {
            (center1 < 0.0) || (center2 < 0.0) -> listOf(
                center1 + border,
                center2 + border
            )
            else -> listOf(center1, center2)
        }
    }

    private fun changeCoorIfBorder(center: Double, border: Double): Double {
        return when {
            (center < 0.0) -> center + border
            else -> center
        }
    }

    fun changeCoordinateIfBorderTop(center: Double, border: Double): Double {
        return when {
            (center < 0.0) -> center + border
            (center >= border) -> center - border
            else -> center
        }
    }

    fun findDistance(center1: Vec, center2: Vec): Double {
        val distanceX = getMinDistance(center1.x, center2.x, width)
        val distanceY = getMinDistance(center1.y, center2.y, height)
        return sqrt(distanceX * distanceX + distanceY * distanceY)
    }

    private fun getMinDistance(coor1: Double, coor2: Double, border: Double): Double {
        val base = abs(coor2 - coor1)
        val reverse1 = abs((border - coor2) - (0 - coor1))
        val reverse2 = abs((border - coor1) - (0 - coor2))
        return min(base, min(reverse1, reverse2))
    }

    fun findAngle(center1: Vec, center2: Vec): Double {
        val baseX = abs(center2.x - center1.x)
        val reverseX1 = abs((width - center2.x) - (0 - center1.x))
        val reverseX2 = abs((width - center1.x) - (0 - center2.x))
        val baseY = abs(center2.y - center1.y)
        val reverseY1 = abs((height - center2.y) - (0 - center1.y))
        val reverseY2 = abs((height - center1.y) - (0 - center2.y))

        val cx2 = when {
            (baseX < reverseX1) && (baseX < reverseX2) -> {
                center2.x
            }
            (reverseX1 < baseX) && (reverseX1 < reverseX2) -> {
                center2.x - width
            }
            else -> {
                width + center2.x
            }
        }
        val cy2 = when {
            (baseY < reverseY1) && (baseY < reverseY2) -> {
                center2.y
            }
            (reverseY1 < baseY) && (reverseY1 < reverseY2) -> {
                center2.y - height
            }
            else -> {
                height + center2.y
            }
        }

        val direction = Vec(cx2, cy2) - center1
        return atan2(direction.y, direction.x) * 180 / Math.PI
    }
}