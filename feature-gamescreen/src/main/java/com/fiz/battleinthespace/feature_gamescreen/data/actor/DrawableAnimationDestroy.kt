package com.fiz.battleinthespace.feature_gamescreen.data.actor

import android.graphics.Bitmap
import com.fiz.battleinthespace.feature_gamescreen.data.actor.weapon.Weapon
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.ui.Display

interface DrawableAnimationDestroy : Drawable {
    var timeShowMax: Double
    var timeShow: Double
    var frame: Int
}

class BulletAnimationDestroy(
    _center: Vec,
    _angle: Double = 0.0,
    _size: Double,

    override var timeShowMax: Double,
    override var timeShow: Double = timeShowMax, override var frame: Int = 0) : Actor(_center, _angle, _size),
    DrawableAnimationDestroy {
    constructor(weapon: Weapon) : this(Vec(weapon.center), weapon.angle, weapon.size, 1.0)

    override fun getBitmap(display: Display): Bitmap {
        return display.bitmapRepository.bmpBulletDestroy[frame]
    }
}

class SpaceShipAnimationDestroy(
    _center: Vec,
    _angle: Double = 0.0,
    _size: Double,

    override var timeShowMax: Double,
    override var timeShow: Double = timeShowMax, override var frame: Int = 0) : Actor(_center, _angle, _size),
    DrawableAnimationDestroy {
    constructor(spaceShip: SpaceShip) : this(Vec(spaceShip.center), spaceShip.angle, spaceShip.size, 1.5)

    override fun getBitmap(display: Display): Bitmap {
        return display.bitmapRepository.bmpSpaceshipDestroy[frame]
    }
}

