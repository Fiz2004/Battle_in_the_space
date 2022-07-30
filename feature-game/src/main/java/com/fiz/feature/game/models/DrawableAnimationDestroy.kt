package com.fiz.feature.game.models

import com.fiz.battleinthespace.common.Vec
import com.fiz.feature.game.models.weapon.Weapon

class BulletAnimationDestroy(
    _center: Vec,
    _angle: Double = 0.0,
    _size: Double,

    var timeShowMax: Double,
    var timeShow: Double = timeShowMax, var frame: Int = 0
) : Actor(_center, _angle, _size), java.io.Serializable {
    constructor(weapon: Weapon) : this(Vec(weapon.center), weapon.angle, weapon.size, 1.0)
}

class SpaceShipAnimationDestroy(
    _center: Vec,
    _angle: Double = 0.0,
    _size: Double,

    var timeShowMax: Double,
    var timeShow: Double = timeShowMax, var frame: Int = 0
) : Actor(_center, _angle, _size), java.io.Serializable {
    constructor(spaceShip: SpaceShip) : this(
        Vec(spaceShip.center),
        spaceShip.angle,
        spaceShip.size,
        1.5
    )
}

