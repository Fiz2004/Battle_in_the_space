package com.fiz.android.battleinthespace.engine

import com.fiz.android.battleinthespace.EPSILON
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vec2(var x: Double, var y: Double) {
    var m: Array<Array<Double>> = arrayOf(emptyArray())
    var v: Array<Double> = emptyArray()

    fun Set(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    operator fun unaryMinus(): Vec2 {
        return Vec2(-x, -y)
    }

    operator fun times(s: Double): Vec2 {
        return Vec2(x * s, y * s)
    }

    operator fun div(s: Double): Vec2 {
        return Vec2(x / s, y / s)
    }

    operator fun plus(rhs: Vec2): Vec2 {
        return Vec2(x + rhs.x, y + rhs.y)
    }

    operator fun plus(s: Double): Vec2 {
        return Vec2(x + s, y + s)
    }

    operator fun minus(rhs: Vec2): Vec2 {
        return Vec2(x - rhs.x, y - rhs.y)
    }

    fun LenSqr(): Double {
        return x * x + y * y
    }

    fun Len(): Double {
        return sqrt(x * x + y * y)
    }

    fun Rotate(radians: Double) {
        val c: Double = cos(radians)
        val s: Double = sin(radians)

        val xp: Double = x * c - y * s
        val yp: Double = x * s + y * c

        x = xp
        y = yp
    }

    fun Normalize() {
        val len: Double = Len()

        if (len > EPSILON) {
            val invLen: Double = 1.0f / len
            x *= invLen
            y *= invLen
        }
    }
}