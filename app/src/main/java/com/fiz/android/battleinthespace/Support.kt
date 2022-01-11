package com.fiz.android.battleinthespace

data class AnimationBulletDestroy(
    var centerX: Double,
    var centerY: Double,

    var numberFrame: Int,

    var timeShowMax:Int=1000,
    var timeShow:Int=timeShowMax

)

data class AnimationSpaceShipDestroy(
    var centerX: Double,
    var centerY: Double,

    var numberFrame: Int,

    var timeShowMax:Int=1000,
    var timeShow:Int=timeShowMax
)

class Respawn(
    centerX: Double,
    centerY: Double,
    angle: Double
):Actor(centerX,centerY,0.0,0.0,angle,4.0)