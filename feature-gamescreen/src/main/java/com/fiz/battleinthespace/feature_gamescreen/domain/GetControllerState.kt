package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.feature_gamescreen.ui.ControllerState

class GetControllerState(
    private val leftLocationOnScreen: Int,
    private val topLocationOnScreen: Int,
    private val widthJoystick: Float
) {

    operator fun invoke(controller: Controller): ControllerState? {

        val centerXOutsideCircle = controller.moveInfoTouch.point.x.toFloat() - leftLocationOnScreen
        val centerYOutsideCircle = controller.moveInfoTouch.point.y.toFloat() - topLocationOnScreen

        val centerXInnerCircle =
            centerXOutsideCircle + widthJoystick * controller.power * kotlin.math.cos(controller.angle / 180.0 * java.lang.Math.PI)
                .toFloat()
        val centerYInnerCircle =
            centerYOutsideCircle + widthJoystick * controller.power * kotlin.math.sin(controller.angle / 180.0 * java.lang.Math.PI)
                .toFloat()

        return if (controller.moveInfoTouch.touch)
            ControllerState(
                centerXOutsideCircle = centerXOutsideCircle,
                centerYOutsideCircle = centerYOutsideCircle,
                centerXInnerCircle = centerXInnerCircle,
                centerYInnerCircle = centerYInnerCircle,
                widthJoystick = widthJoystick
            )
        else
            null
    }
}