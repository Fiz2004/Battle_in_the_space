package com.fiz.battleinthespace.feature_gamescreen.game.models

import android.graphics.Bitmap
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon
import com.fiz.battleinthespace.feature_gamescreen.ui.Display

class BulletAnimationDestroy(
    _center: Vec,
    _angle: Double = 0.0,
    _size: Double,

    var timeShowMax: Double,
    var timeShow: Double = timeShowMax, var frame: Int = 0) : Actor(_center, _angle, _size){
    constructor(weapon: Weapon) : this(Vec(weapon.center), weapon.angle, weapon.size, 1.0)

    fun getBitmap(display: Display): Bitmap {
        return display.bitmapRepository.bmpBulletDestroy[frame]
    }
}

class SpaceShipAnimationDestroy(
    _center: Vec,
    _angle: Double = 0.0,
    _size: Double,

    var timeShowMax: Double,
    var timeShow: Double = timeShowMax, var frame: Int = 0) : Actor(_center, _angle, _size){
    constructor(spaceShip: SpaceShip) : this(Vec(spaceShip.center), spaceShip.angle, spaceShip.size, 1.5)

    fun getBitmap(display: Display): Bitmap {
        return display.bitmapRepository.bmpSpaceshipDestroy[frame]
    }
}

