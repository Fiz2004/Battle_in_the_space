package com.fiz.android.battleinthespace

open class Actor(
    var centerX: Double,
    var centerY: Double,

    var speedX: Double,
    var speedY: Double,

    var angle: Double,

    var size:Double
) {
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