package com.fiz.android.battleinthespace.engine

import kotlin.math.sqrt

class Vec(var x: Double, var y: Double) {
    constructor (vec: Vec):this(vec.x,vec.y)

    operator fun plus(vec: Vec): Vec {
        return Vec(x + vec.x, y + vec.y)
    }

    operator fun minus(vec: Vec): Vec {
        return Vec(x - vec.x, y - vec.y)
    }

    operator fun times(factor: Double): Vec {
        return Vec(x * factor, y * factor)
    }

    fun copy():Vec{
        return Vec(x,y)
    }

    fun length():Double{
        return x*x+y*y
    }

    fun lengthSqrt():Double{
        return sqrt(length())
    }
}