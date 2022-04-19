package com.fiz.battleinthespace.feature_gamescreen.domain

import android.content.Context
import android.view.MotionEvent
import com.fiz.battleinthespace.feature_gamescreen.data.actor.PlayerGame
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Vec
import kotlin.math.abs
import kotlin.math.atan2

class Controller(
    var fire: Boolean = false,
    var power: Float = 0F,
    context: Context,
    _timeBetweenFireMin: Double = 0.250,
    _angle: Float = 0F
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
            if (value >= 360)
                field = value - 360
            if (value < 0)
                field = value + 360
        }

    private val sensivity: Vec = Vec(0.0, 0.0)

    private var timeBetweenFireMin = _timeBetweenFireMin
    private var timeLastFire: Double = 0.0
    lateinit var playerGame: PlayerGame

    fun linkPlayer(playerGame: PlayerGame) {
        this.playerGame = playerGame
    }

    fun isCanFire(deltaTime: Double): Boolean {
        if (!fire) return false

        if (timeLastFire == 0.0) {
            timeLastFire = timeBetweenFireMin
            if (!rightSide.touch)
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
            leftSide.point = point.copy()
            leftSide.touch = true
            leftSide.ID = pointerId
        } else {
            fire = true
            rightSide.touch = true
        }
    }

    fun pointerDown(touchLeftSide: Boolean, point: Vec, pointerId: Int) {
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

    fun up() {
        leftSide.touch = false
        power = 0F

        rightSide.touch = false
    }

    fun powerUp(event: MotionEvent) {
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

    fun move(event: MotionEvent) {
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