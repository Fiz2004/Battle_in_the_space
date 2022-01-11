package com.fiz.android.battleinthespace

class Meteorite(
    centerX: Double,
    centerY: Double,

    speedX: Double = 0.0,
    speedY: Double = 0.0,

    angle: Double,

    size:Double,

    var viewSize: Int,

    var view: Int
) : Actor(
    centerX, centerY, speedX, speedY, angle,size
) {

}