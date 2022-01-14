package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.Vec

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
        val stepX = speedX * deltaTime / 1000
        center.x += stepX
        if (center.x > width)
            center.x = 0.0
        if (center.x < 0)
            center.x = width

        val stepY = speedY * deltaTime / 1000
        center.y += stepY
        if (center.y > height)
            center.y = 0.0
        if (center.y < 0)
            center.y = height
    }
}