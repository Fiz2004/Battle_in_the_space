package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Vec

class AnimationDestroy(
    _center: Vec,
    _angle:Double=0.0,
    _size:Double,

    var timeShowMax: Double,
    var timeShow: Double = timeShowMax

):Actor(_center,_angle,_size) {
    constructor(bullet: Bullet):this(Vec(bullet.center),bullet.angle,bullet.size,1.0)
    constructor(spaceShip: SpaceShip):this(Vec(spaceShip.center),spaceShip.angle,spaceShip.size,1.5)
}

