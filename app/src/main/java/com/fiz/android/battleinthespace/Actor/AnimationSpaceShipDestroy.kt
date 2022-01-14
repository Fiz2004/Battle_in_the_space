package com.fiz.android.battleinthespace.Actor

data class AnimationSpaceShipDestroy(
    var centerX: Double,
    var centerY: Double,

    var angle:Double,

    var timeShowMax:Int=1500,
    var timeShow:Int=timeShowMax
){
    constructor(spaceShip: SpaceShip):this(spaceShip.centerX,spaceShip.centerY,spaceShip.angle)
}