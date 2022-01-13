package com.fiz.android.battleinthespace

data class AnimationSpaceShipDestroy(
    var centerX: Double,
    var centerY: Double,

    var angle:Double,

    var timeShowMax:Int=2000,
    var timeShow:Int=timeShowMax
){
    constructor(spaceShip:SpaceShip):this(spaceShip.centerX,spaceShip.centerY,spaceShip.angle)
}