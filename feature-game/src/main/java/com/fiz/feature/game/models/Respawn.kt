package com.fiz.feature.game.models

import com.fiz.battleinthespace.common.Vec


class Respawn(
    center: Vec,
    angle: Double,
    size: Double = 4.0
) : Actor(center, angle, size), java.io.Serializable