package com.fiz.android.battleinthespace

open class Actor(
    open var centerX: Double,
    open var centerY: Double,

    open var speedX: Double,
    open var speedY: Double,

    open var angle: Double
) {
    fun update(deltaTime: Int, width: Double, height: Double) {
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