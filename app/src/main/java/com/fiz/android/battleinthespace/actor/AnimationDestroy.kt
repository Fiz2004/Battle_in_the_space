package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Vec

class AnimationDestroy(
    _center: Vec,
    _angle:Double=0.0,
    _size:Double,

    var timeShowMax: Int,
    var timeShow: Int = timeShowMax

):Actor(_center,_angle,_size) {
    constructor(bullet: Bullet):this(Vec(bullet.center),bullet.angle,bullet.size,1000)
    constructor(spaceShip: SpaceShip):this(Vec(spaceShip.center),spaceShip.angle,spaceShip.size,1500)
}

