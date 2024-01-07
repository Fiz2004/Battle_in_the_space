package com.fiz.feature.game.models

import com.fiz.battleinthespace.common.Vec
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

abstract class MoveableActor(
    center: Vec,
    _speed: Vec,
    angle: Double,
    size: Double,
    var inGame: Boolean = true,
    private var speedMax: Double
) : Actor(center, angle, size), Drawable, java.io.Serializable {
    var angleSpeed: Double = 0.0

    // Устанавливается по форме
    private var density = 1.0

    // масса
    private var weight: Double = Math.PI * density

    // обратный масса
    var inverseWeight: Double = 1.0 / weight

    // момент инерции
    private var momentInertia: Double = weight * halfSize.pow(2) / 1000000000000000

    // обратная инерция
    var inverseMomentInertia: Double = 1.0 / momentInertia

    var restitution: Double = 1.0

    fun applyImpulse(impulse: Vec, contactVector: Vec) {
        speed += impulse * inverseWeight
        angleSpeed += inverseMomentInertia * com.fiz.battleinthespace.common.cross(
            contactVector,
            impulse
        )
    }

    var speed: Vec = _speed.copy()
        set(value) {
            var x = value.x
            if (abs(value.x) > speedMax)
                x = sign(value.x) * speedMax

            var y = value.y
            if (abs(value.y) > speedMax)
                y = sign(value.y) * speedMax

            field = Vec(x, y)
        }

    open fun update(deltaTime: Double, width: Int, height: Int) {
        center += speed * deltaTime

        if (center.x > width)
            center = Vec(center.x - width, center.y)
        if (center.x < 0)
            center = Vec(center.x + width, center.y)

        if (center.y > height)
            center = Vec(center.x, center.y - height)
        if (center.y < 0)
            center = Vec(center.x, center.y + height)

        angle += angleSpeed * 180 / Math.PI * deltaTime
        angleSpeed = 0.0
    }
}