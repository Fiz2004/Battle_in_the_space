package com.fiz.android.battleinthespace

private const val SPEED_BULLET_MAX: Double = 0.2

class Bullet(
    centerX: Double,
    centerY: Double,

    speedX: Double,
    speedY: Double,

    angle: Double,

    size:Double=0.1,

    var roadLength: Double,

    var player: Int,
) : Actor(
    centerX, centerY, speedX, speedY, angle,size
) {
    override fun update(deltaTime: Int, width: Double, height: Double) {
        super.update(deltaTime, width, height)
        roadLength += SPEED_BULLET_MAX
    }
}