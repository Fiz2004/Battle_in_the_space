package com.fiz.android.battleinthespace

import com.fiz.android.battleinthespace.actor.Bullet
import com.fiz.android.battleinthespace.actor.Meteorite
import com.fiz.android.battleinthespace.actor.SpaceShip

class Level(val width: Double,
            val height: Double){
    var backgrounds: MutableList<MutableList<Int>> = mutableListOf()
    var spaceShips: MutableList<SpaceShip> = mutableListOf()
    var bullets: MutableList<Bullet> = mutableListOf()
    var meteorites: MutableList<Meteorite> = mutableListOf()
}