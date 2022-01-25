package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Vec

interface DrawableAnimationDestroy : Drawable {
    var timeShowMax: Double
    var timeShow: Double
}

class BulletAnimationDestroy(
    _center: Vec,
    _angle: Double = 0.0,
    _size: Double,

    override var timeShowMax: Double,
    override var timeShow: Double = timeShowMax) : Actor(_center, _angle, _size), DrawableAnimationDestroy {
    constructor(bullet: Bullet) : this(Vec(bullet.center), bullet.angle, bullet.size, 1.0)
}

class SpaceShipAnimationDestroy(
    _center: Vec,
    _angle: Double = 0.0,
    _size: Double,

    override var timeShowMax: Double,
    override var timeShow: Double = timeShowMax) : Actor(_center, _angle, _size), DrawableAnimationDestroy {
    constructor(spaceShip: SpaceShip) : this(Vec(spaceShip.center), spaceShip.angle, spaceShip.size, 1.5)
}

