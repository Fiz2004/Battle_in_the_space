package com.fiz.android.battleinthespace

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