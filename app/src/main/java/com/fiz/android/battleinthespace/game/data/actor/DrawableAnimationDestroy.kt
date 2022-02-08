package com.fiz.android.battleinthespace.game.data.actor

import android.graphics.Bitmap
import com.fiz.android.battleinthespace.game.data.engine.Vec
import com.fiz.android.battleinthespace.game.domain.Display

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
    constructor(bullet: Bullet) : this(Vec(bullet.center), bullet.angle, bullet.size, 1.0)

    override fun getBitmap(display: Display): Bitmap {
        return display.bmpBulletDestroy[frame]
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
        return display.bmpSpaceshipDestroy[frame]
    }
}

