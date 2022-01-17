package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Vec
import kotlin.math.abs
import kotlin.math.sign

open class MoveableActor(
    center: Vec,
    _speed: Vec,
    angle: Double,
    size: Double,
    private var speedMax: Double
) :Actor(center,angle,size){
    var speed: Vec = _speed.copy()
        set(value) {
            var x = value.x
            if (abs(value.x) > speedMax)
                x = sign(value.x)*speedMax

            var y = value.y
            if (abs(value.y) > speedMax)
                y = sign(value.y)*speedMax

            field = Vec(x, y)
        }

    open fun update(deltaTime: Int, width: Double, height: Double) {
        center += speed * (deltaTime / 1000.0)

        if (center.x > width)
            center = Vec(center.x - width, center.y)
        if (center.x < 0)
            center = Vec(center.x + width, center.y)

        if (center.y > height)
            center = Vec(center.x, center.y - height)
        if (center.y < 0)
            center = Vec(center.x, center.y + height)
    }
}