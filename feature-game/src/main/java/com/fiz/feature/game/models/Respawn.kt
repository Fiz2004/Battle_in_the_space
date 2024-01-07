package com.fiz.feature.game.models


class Respawn(
    center: com.fiz.battleinthespace.common.Vec,
    angle: Double,
    size: Double = 4.0
) : Actor(center, angle, size), java.io.Serializable