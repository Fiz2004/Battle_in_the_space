package com.fiz.battleinthespace.feature_gamescreen.game.models

import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec


class Respawn(
    center: Vec,
    angle: Double,
    size: Double = 4.0
) : Actor(center, angle, size)