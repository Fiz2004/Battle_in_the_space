package com.fiz.android.battleinthespace

data class Meteorite(
    override var centerX: Double,
    override var centerY: Double,

    override var speedX: Double = 0.0,
    override var speedY: Double = 0.0,

    override var angle: Double,
    var sizePx: Double,

    var viewSize: Int,

    var view: Int
) : Actor(
    centerX, centerY, speedX, speedY, angle
) {

}