package com.fiz.battleinthespace.domain.models

import com.fiz.battleinthespace.common.Vec
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.atan2

const val WIDTH_JOYSTICK_DEFAULT = 50.0

data class InfoTouch(
    val point: Vec = Vec(0.0, 0.0),
    val touch: Boolean = false,
    val ID: Int = 0
) : Serializable

class Controller(
    var fire: Boolean = false,
    var power: Double = 0.0
) : Serializable {

    var moveInfoTouch = InfoTouch()

    var angle: Double = 0.0
        set(value) {
            field = value
            if (value >= 360.0)
                field = value - 360.0
            if (value < 0.0)
                field = value + 360.0
        }

    private val sensitivity: Vec =
        Vec(0.0, 0.0)

    fun down(touchMoveSide: Boolean, point: Vec, pointerId: Int) {
        if (touchMoveSide) {
            moveInfoTouch = moveInfoTouch.copy(
                point = point.copy(),
                touch = true,
                ID = pointerId
            )
        } else {
            fire = true
        }
    }

    fun pointerDown(
        touchLeftSide: Boolean,
        point: Vec,
        pointerId: Int
    ) {
        if (!moveInfoTouch.touch && touchLeftSide) {
            moveInfoTouch = moveInfoTouch.copy(
                point = point.copy(),
                touch = true,
                ID = pointerId
            )
        }
        if (!fire && !touchLeftSide) {
            fire = true
        }
    }

    fun up() {
        moveInfoTouch = moveInfoTouch.copy(
            touch = false
        )
        power = 0.0

        fire = false
    }

    fun powerUp(pointIndex: Int) {
        val isMoveSide = pointIndex == moveInfoTouch.ID
        if (isMoveSide) {
            moveInfoTouch = moveInfoTouch.copy(
                touch = false
            )
            power = 0.0
        }
        if (!isMoveSide) {
            fire = false
        }
    }

    fun move(point: Vec) {
        if (moveInfoTouch.touch) {

            var deltaX = point.x - moveInfoTouch.point.x
            if (abs(deltaX) <= sensitivity.x)
                deltaX = 0.0

            var deltaY = point.y - moveInfoTouch.point.y
            if (abs(deltaY) <= sensitivity.y)
                deltaY = 0.0

            val delta = Vec(deltaX, deltaY)

            val tempPower = (1.0 / 3.0) * (delta.length() / WIDTH_JOYSTICK_DEFAULT)
            power = if (tempPower > 1) 1.0 else tempPower

            angle = atan2(delta.y, delta.x).convertRadToDeg()
        }
    }
}

fun Double.convertRadToDeg() = this * 180 / Math.PI