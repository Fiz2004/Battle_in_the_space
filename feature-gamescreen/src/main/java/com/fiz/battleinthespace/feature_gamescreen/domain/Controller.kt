package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.atan2

const val WIDTH_JOYSTICK_DEFAULT = 50F

class Controller(
    var fire: Boolean = false,
    var power: Float = 0F,
    _timeBetweenFireMin: Double = 0.250,
    _angle: Float = 0F
) : Serializable {
    class Part : Serializable {
        var point: Vec = Vec(0.0, 0.0)
        var touch: Boolean = false
        var ID: Int = 0
    }

    val movePart = Part()
    val firePart = Part()

    var angle: Float = _angle
        set(value) {
            field = value
            if (value >= 360)
                field = value - 360
            if (value < 0)
                field = value + 360
        }

    private val sensivity: Vec = Vec(0.0, 0.0)

    private var timeBetweenFireMin = _timeBetweenFireMin
    private var timeLastFire: Double = 0.0

    fun isCanFire(deltaTime: Double): Boolean {
        if (!fire) return false

        if (timeLastFire == 0.0) {
            timeLastFire = timeBetweenFireMin
            if (!firePart.touch)
                fire = false
            return true
        }
        timeLastFire -= deltaTime
        if (timeLastFire < 0)
            timeLastFire = 0.0
        return false
    }

    fun down(touchMoveSide: Boolean, point: Vec, pointerId: Int) {
        if (touchMoveSide) {
            movePart.point = point.copy()
            movePart.touch = true
            movePart.ID = pointerId
        } else {
            fire = true
            firePart.touch = true
        }
    }

    fun pointerDown(touchLeftSide: Boolean, point: Vec, pointerId: Int) {
        if (!movePart.touch && touchLeftSide) {
            movePart.point = point.copy()
            movePart.touch = true
            movePart.ID = pointerId
        }
        if (!firePart.touch && !touchLeftSide) {
            fire = true
            firePart.touch = true
            firePart.ID = pointerId
        }
    }

    fun up() {
        movePart.touch = false
        power = 0F

        firePart.touch = false
    }

    fun powerUp(pointIndex: Int) {
        val isMoveSide = pointIndex == movePart.ID
        val isFireSide = pointIndex == firePart.ID
        if (isMoveSide) {
            movePart.touch = false
            power = 0F
        }
        if (isFireSide)
            firePart.touch = false
    }

    fun move(point: Vec) {
        if (movePart.touch) {

            val delta = Vec(
                if (abs(point.x - movePart.point.x) > sensivity.x) point.x - movePart.point.x else 0.0,
                if (abs(point.y - movePart.point.y) > sensivity.y) point.y - movePart.point.y else 0.0
            )

            val tempPower = delta.length() / (WIDTH_JOYSTICK_DEFAULT * 3)
            power = (if (tempPower > 1) 1.0 else tempPower).toFloat()

            angle = (atan2(delta.y, delta.x) * 180 / Math.PI).toFloat()
        }
    }
}