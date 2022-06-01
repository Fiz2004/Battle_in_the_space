package com.fiz.battleinthespace.feature_gamescreen.domain

import android.view.MotionEvent
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
    class Side : Serializable {
        var point: Vec = Vec(0.0, 0.0)
        var touch: Boolean = false
        var ID: Int = 0
    }

    val moveSide = Side()
    val fireSide = Side()

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
            if (!fireSide.touch)
                fire = false
            return true
        }
        timeLastFire -= deltaTime
        if (timeLastFire < 0)
            timeLastFire = 0.0
        return false
    }

    fun down(touchLeftSide: Boolean, point: Vec, pointerId: Int) {
        if (touchLeftSide) {
            moveSide.point = point.copy()
            moveSide.touch = true
            moveSide.ID = pointerId
        } else {
            fire = true
            fireSide.touch = true
        }
    }

    fun pointerDown(touchLeftSide: Boolean, point: Vec, pointerId: Int) {
        if (!moveSide.touch && touchLeftSide) {
            moveSide.point = point.copy()
            moveSide.touch = true
            moveSide.ID = pointerId
        }
        if (!fireSide.touch && !touchLeftSide) {
            fire = true
            fireSide.touch = true
            fireSide.ID = pointerId
        }
    }

    fun up() {
        moveSide.touch = false
        power = 0F

        fireSide.touch = false
    }

    fun powerUp(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val isLeftSide = event.findPointerIndex(pointerIndex) == moveSide.ID
        val isRightSide = event.findPointerIndex(pointerIndex) == fireSide.ID
        if (isLeftSide) {
            moveSide.touch = false
            power = 0F
        }
        if (isRightSide)
            fireSide.touch = false
    }

    fun move(event: MotionEvent) {
        if (moveSide.touch) {
            val point = Vec(
                event.getX(event.findPointerIndex(moveSide.ID)).toDouble(),
                event.getY(event.findPointerIndex(moveSide.ID)).toDouble()
            )

            val delta = Vec(
                if (abs(point.x - moveSide.point.x) > sensivity.x) point.x - moveSide.point.x else 0.0,
                if (abs(point.y - moveSide.point.y) > sensivity.y) point.y - moveSide.point.y else 0.0
            )

            val tempPower = delta.length() / (WIDTH_JOYSTICK_DEFAULT * 3)
            power = (if (tempPower > 1) 1.0 else tempPower).toFloat()

            angle = (atan2(delta.y, delta.x) * 180 / Math.PI).toFloat()
        }
    }
}