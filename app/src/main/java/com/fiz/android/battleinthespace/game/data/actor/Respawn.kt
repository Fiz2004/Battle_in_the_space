package com.fiz.android.battleinthespace.game.data.actor

import com.fiz.android.battleinthespace.game.data.engine.Vec

class Respawn(
    center: Vec,
    angle: Double,
    size: Double = 4.0
) : Actor(center, angle, size)