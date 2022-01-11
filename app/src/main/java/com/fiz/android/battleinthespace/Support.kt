package com.fiz.android.battleinthespace


data class SpaceShip(
    var centerX: Double,
    var centerY: Double,

    var speedX: Double,
    var speedY: Double,

    var angle: Double,
    var inGame: Boolean
)

data class Bullet(
    var centerX: Double,
    var centerY: Double,

    var speedX: Double,
    var speedY: Double,

    var angle: Double,

    var roadLength: Double,

    var player: Int
)

data class Meteorite(
    var centerX: Double,
    var centerY: Double,

    var speedX: Double,
    var speedY: Double,

    var angle: Double,
    var sizePx: Double,

    var viewSize: Int,

    var view: Int
)


data class AnimationBulletDestroy(
    var centerX: Double,
    var centerY: Double,

    var numberFrame: Int
)

data class AnimationSpaceShipDestroy(
    var centerX: Double,
    var centerY: Double,

    var numberFrame: Int
)

data class Respawn(
    var centerX: Int,
    var centerY: Int,
    var angle: Int
)