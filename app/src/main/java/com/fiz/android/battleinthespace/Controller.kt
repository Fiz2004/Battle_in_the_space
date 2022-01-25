package com.fiz.android.battleinthespace

import android.content.Context
import android.view.MotionEvent
import com.fiz.android.battleinthespace.engine.Vec
import kotlin.math.abs
import kotlin.math.atan2

class Controller(
    var fire: Boolean = false,
    _timeBetweenFireMin: Double = 0.250,
    _angle: Float = 0F,
    var power: Float = 0F,
    context: Context
) {
    class Side {
        var point: Vec = Vec(0.0, 0.0)
        var touch: Boolean = false
        var ID: Int = 0
    }

    val leftSide = Side()
    val rightSide = Side()

    val widthJoystick = 50F * context.resources.displayMetrics.scaledDensity

    var angle: Float = _angle
        set(value) {
            field = value
            if (value > 360)
                field = value - 360
            if (value < 0)
                field = value + 360
        }

    private val sensivity: Vec = Vec(0.0, 0.0)

    private var timeBetweenFireMin = _timeBetweenFireMin
    private var timeLastFire: Double = 0.0

    fun isCanFire(deltaTime: Double): Boolean {
        if (timeLastFire == 0.0) {
            timeLastFire = timeBetweenFireMin
            return true
        }
        timeLastFire -= deltaTime
        if (timeLastFire < 0)
            timeLastFire = 0.0
        return false
    }

    fun ACTION_DOWN(touchLeftSide: Boolean, point: Vec, pointerId: Int) {
        if (touchLeftSide) {
            leftSide.point = point.copy()
            leftSide.touch = true
            leftSide.ID = pointerId
        } else {
            fire = true
            rightSide.touch = true
        }
    }

    fun ACTION_POINTER_DOWN(touchLeftSide: Boolean, point: Vec, pointerId: Int) {
        if (!leftSide.touch && touchLeftSide) {
            leftSide.point = point.copy()
            leftSide.touch = true
            leftSide.ID = pointerId
        }
        if (!rightSide.touch && !touchLeftSide) {
            fire = true
            rightSide.touch = true
            rightSide.ID = pointerId
        }
    }

    fun ACTION_UP() {
        leftSide.touch = false
        power = 0F

        rightSide.touch = false
    }

    fun ACTION_POINTER_UP(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val isLeftSide = event.findPointerIndex(pointerIndex) == leftSide.ID
        val isRightSide = event.findPointerIndex(pointerIndex) == rightSide.ID
        if (isLeftSide) {
            leftSide.touch = false
            power = 0F
        }
        if (isRightSide)
            rightSide.touch = false
    }

    fun ACTION_MOVE(event: MotionEvent) {
        if (leftSide.touch) {
            val point = Vec(
                event.getX(event.findPointerIndex(leftSide.ID)).toDouble(),
                event.getY(event.findPointerIndex(leftSide.ID)).toDouble()
            )

            val delta = Vec(
                if (abs(point.x - leftSide.point.x) > sensivity.x) point.x - leftSide.point.x else 0.0,
                if (abs(point.y - leftSide.point.y) > sensivity.y) point.y - leftSide.point.y else 0.0
            )

            val tempPower = delta.length() / (widthJoystick * 3)
            power = (if (tempPower > 1) 1.0 else tempPower).toFloat()

            angle = (atan2(delta.y, delta.x) * 180 / Math.PI).toFloat()
        }
    }
}