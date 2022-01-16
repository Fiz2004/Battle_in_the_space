package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Vec

open class Actor(
    var center: Vec,

    _speedX: Double,
    _speedY: Double,

    _angle: Double,

    var size:Double,

    private var speedMax:Double
) {
    var speedX:Double=_speedX
    set(value){
        field=value
        if (value>speedMax)
            field=speedMax
        if (value<-speedMax)
            field=-speedMax
    }

    var speedY:Double=_speedY
        set(value){
            field=value
            if (value>speedMax)
                field=speedMax
            if (value<-speedMax)
                field=-speedMax
        }

    var angle: Double=_angle
    set(value){
        field=value
        if (value>360)
            field=value-360
        if (value<0)
            field=value+360
    }

    open fun update(deltaTime: Int, width: Double, height: Double) {
        val step= Vec(speedX * deltaTime / 1000,speedY * deltaTime / 1000)
        center += step

        if (center.x > width)
            center = Vec(center.x-width,center.y)
        if (center.x < 0)
            center = Vec(center.x+width,center.y)

        if (center.y > height)
            center = Vec(center.x,center.y-height)
        if (center.y < 0)
            center = Vec(center.x,center.y+height)
    }
}