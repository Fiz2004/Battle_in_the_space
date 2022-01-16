package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Vec

data class AnimationSpaceShipDestroy(
    var center: Vec,

    var angle:Double,

    var timeShowMax:Int=1500,
    var timeShow:Int=timeShowMax
){
    constructor(spaceShip: SpaceShip):this(Vec(spaceShip.center),spaceShip.angle)
}