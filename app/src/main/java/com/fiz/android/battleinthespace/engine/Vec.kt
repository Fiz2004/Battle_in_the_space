package com.fiz.android.battleinthespace.engine

class Vec(val x: Double, val y: Double) {
    constructor (vec: Vec):this(vec.x,vec.y)

    operator fun plus(vec: Vec): Vec {
        return Vec(x + vec.x, y + vec.y)
    }

    operator fun times(factor: Double): Vec {
        return Vec(x * factor, y * factor)
    }
}