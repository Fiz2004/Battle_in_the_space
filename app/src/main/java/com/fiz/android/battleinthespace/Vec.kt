package com.fiz.android.battleinthespace

class Vec(var x: Double, var y: Double) {
    operator fun plus(vec: Vec): Vec {
        return Vec(x + vec.x, y + vec.y)
    }

    operator fun plusAssign(vec: Vec) {
        x+=vec.x
        y+=vec.y
    }

    operator fun times(factor: Double): Vec {
        return Vec(x * factor, y * factor)
    }
}