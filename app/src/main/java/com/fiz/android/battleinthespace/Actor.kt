package com.fiz.android.battleinthespace

open class Actor(
    var centerX: Double,
    var centerY: Double,

    _speedX: Double,
    _speedY: Double,

    _angle: Double,

    var size:Double,

    var speedMax:Double
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
        centerX += stepX
        if (centerX > width)
            centerX = 0.0
        if (centerX < 0)
            centerX = width

        val stepY = speedY * deltaTime / 1000
        centerY += stepY
        if (centerY > height)
            centerY = 0.0
        if (centerY < 0)
            centerY = height
    }
}