package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.Vec
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val SPEED_MAX:Double = 4.0

class Bullet(
    center:Vec,

    speedX: Double,
    speedY: Double,

    angle: Double,

    size:Double=0.1,

    var roadLength: Double,

    var player: Int,
) : Actor(
    center, speedX, speedY, angle,size, SPEED_MAX
) {
    constructor(spaceShips:MutableList<SpaceShip>,numberPlayer:Int):this(
        center=Vec(spaceShips[numberPlayer].center.x + 1 * cos(spaceShips[numberPlayer].angle / 180.0 * Math.PI),
        spaceShips[numberPlayer].center.y + 1 * sin(spaceShips[numberPlayer].angle / 180.0 * Math.PI)),
        speedX = SPEED_MAX * cos(spaceShips[numberPlayer].angle / 180.0 * Math.PI),
        speedY = SPEED_MAX * sin(spaceShips[numberPlayer].angle / 180.0 * Math.PI),
        angle = spaceShips[numberPlayer].angle,
        roadLength = 0.0,
        player = numberPlayer
    )
    override fun update(deltaTime: Int, width: Double, height: Double) {
        super.update(deltaTime, width, height)
        val roadX=speedX*deltaTime/1000
        val roadY=speedY*deltaTime/1000
        roadLength += sqrt(roadX*roadX+roadY*roadY)
    }
}