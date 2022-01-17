package com.fiz.android.battleinthespace.engine

import com.fiz.android.battleinthespace.EPSILON
import kotlin.math.sqrt

class Vec(var x: Double, var y: Double) {


    constructor (vec: Vec):this(vec.x,vec.y)

    fun Set(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    var m: Array<Array<Double>> = arrayOf(emptyArray())
    var v: Array<Double> = emptyArray()

    operator fun plus(vec: Vec): Vec {
        return Vec(x + vec.x, y + vec.y)
    }

    operator fun minus(vec: Vec): Vec {
        return Vec(x - vec.x, y - vec.y)
    }

    operator fun times(factor: Double): Vec {
        return Vec(x * factor, y * factor)
    }

    operator fun unaryMinus(): Vec {
        return Vec(-x, -y)
    }

    operator fun div(s: Double): Vec {
        return Vec(x / s, y / s)
    }

    fun copy():Vec{
        return Vec(x,y)
    }

    fun sumPow2():Double{
        return x*x+y*y
    }

    fun length():Double{
        return sqrt(sumPow2())
    }

    fun Normalize() {
        val len: Double = length()

        if (len > EPSILON) {
            val invLen: Double = 1.0f / len
            x *= invLen
            y *= invLen
        }
    }

}

inline operator fun Double.times(v: Vec): Vec {
    return Vec(this * v.x, this * v.y)
}

inline fun Cross(v: Vec, a: Double): Vec {
    return Vec(a * v.y, -a * v.x)
}

inline fun Cross(a: Double, v: Vec): Vec {
    return Vec(-a * v.y, a * v.x)
}

inline fun Cross(a: Vec, b: Vec): Double {
    return a.x * b.y - a.y * b.x;
}

inline fun Dot(a: Vec, b: Vec): Double {
    return a.x * b.x + a.y * b.y
}