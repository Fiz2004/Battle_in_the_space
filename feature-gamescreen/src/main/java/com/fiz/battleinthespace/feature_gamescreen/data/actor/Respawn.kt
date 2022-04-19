package com.fiz.battleinthespace.feature_gamescreen.data.actor

import com.fiz.battleinthespace.feature_gamescreen.data.engine.Vec


class Respawn(
    center: Vec,
    angle: Double,
    size: Double = 4.0
) : Actor(center, angle, size)