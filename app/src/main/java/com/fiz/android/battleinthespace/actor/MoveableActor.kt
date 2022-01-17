package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.*
import kotlin.math.abs
import kotlin.math.sign

open class MoveableActor(
    center: Vec,
    _speed: Vec,
    angle: Double,
    size: Double,
    private var speedMax: Double
) :Actor(center,angle,size){
    var angleSpeed: Double=0.0
    var torque: Double=0.0

    //радианы
    var orient: Double= Random(-Math.PI, Math.PI)

    var force: Vec = Vec(0.0, 0.0)

    // Устанавливается по форме
    // момент инерции
    var I: Double=0.0

    // обратная инерция
    var iI: Double=0.0

    // масса
    private var m: Double=0.0

    // обратный масса
    var im: Double=0.0

    var staticFriction: Double= 0.5
    var dynamicFriction: Double= 0.3
    var restitution: Double= 0.2

    // Сохранение цвета в формате RGB
    var r: Double= Random(0.2, 1.0)
    var g: Double= Random(0.2, 1.0)
    var b: Double= Random(0.2, 1.0)

    fun Initialize() {
        ComputeMass(1.0)
    }

    fun ComputeMass(density: Double) {
        m = Math.PI * (size/2) * (size/2) * density
        im =  1.0 / m
        I = m * (size/2) * (size/2)
        iI =  1.0 / I
    }

    fun ApplyForce( f: Vec)    {
        force += f
    }

    fun ApplyImpulse(impulse: Vec, contactVector: Vec)    {
        speed += im * impulse
        angleSpeed += iI * Cross(contactVector, impulse)
    }

    var speed: Vec = _speed.copy()
        set(value) {
            var x = value.x
            if (abs(value.x) > speedMax)
                x = sign(value.x)*speedMax

            var y = value.y
            if (abs(value.y) > speedMax)
                y = sign(value.y)*speedMax

            field = Vec(x, y)
        }

    open fun update(deltaTime: Int, width: Double, height: Double) {
        center += speed * (deltaTime / 1000.0)

        if (center.x > width)
            center = Vec(center.x - width, center.y)
        if (center.x < 0)
            center = Vec(center.x + width, center.y)

        if (center.y > height)
            center = Vec(center.x, center.y - height)
        if (center.y < 0)
            center = Vec(center.x, center.y + height)
    }
}