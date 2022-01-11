package com.fiz.android.battleinthespace

data class Bullet(
    var centerX: Double,
    var centerY: Double,

    var speedX: Double,
    var speedY: Double,

    var angle: Double,

    var roadLength: Double,

    var player: Int,

    val speedBulletMax: Double = 200.0/1000
) {
    fun update(deltaTime: Int, width: Double, height: Double) {
        centerX += speedX*deltaTime/1000
        if (centerX > width)
            centerX = 0.0
        if (centerX < 0)
            centerX = width
        centerY += speedY*deltaTime/1000
        if (centerY > height)
            centerY = 0.0
        if (centerY < 0)
            centerY = height
        roadLength += speedBulletMax
    }
}