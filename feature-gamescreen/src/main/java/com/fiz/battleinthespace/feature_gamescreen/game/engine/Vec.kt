package com.fiz.battleinthespace.feature_gamescreen.game.engine

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

const val EPSILON: Double = 0.0001

class Vec(var x: Double, var y: Double) {
    constructor (vec: Vec) : this(vec.x, vec.y)

    operator fun plus(vec: Vec): Vec {
        return Vec(x + vec.x, y + vec.y)
    }

    operator fun plus(s: Double): Vec {
        return Vec(x + s, y + s)
    }

    operator fun minus(vec: Vec): Vec {
        return Vec(x - vec.x, y - vec.y)
    }

    operator fun times(factor: Double): Vec {
        return Vec(x * factor, y * factor)
    }

    operator fun times(vec: Vec): Double {
        return x * vec.x + y * vec.y
    }

    operator fun unaryMinus(): Vec {
        return Vec(-x, -y)
    }

    operator fun div(s: Double): Vec {
        return Vec(x / s, y / s)
    }

    fun copy(): Vec {
        return Vec(x, y)
    }

    fun sumPow2(): Double {
        return x * x + y * y
    }

    fun length(): Double {
        return sqrt(sumPow2())
    }

    fun rotate(radians: Double) {
        val c: Double = cos(radians)
        val s: Double = sin(radians)

        val xp: Double = x * c - y * s
        val yp: Double = x * s + y * c

        x = xp
        y = yp
    }

    fun normalize() {
        val len: Double = length()

        if (len > EPSILON) {
            val invLen: Double = 1.0f / len
            x *= invLen
            y *= invLen
        }
    }

}

operator fun Double.times(vec: Vec): Vec {
    return Vec(this * vec.x, this * vec.y)
}

fun cross(a: Double, v: Vec): Vec {
    return Vec(-a * v.y, a * v.x)
}

fun cross(a: Vec, b: Vec): Double {
    return a.x * b.y - a.y * b.x
}

fun dot(a: Vec, b: Vec): Double {
    return a.x * b.x + a.y * b.y
}

fun sqr(a: Double): Double {
    return a * a
}
