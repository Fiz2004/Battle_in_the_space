package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.domain.models.Controller
import com.fiz.battleinthespace.feature_gamescreen.ui.ControllerState

internal class GetControllerState(
    private val leftLocationOnScreen: Int,
    private val topLocationOnScreen: Int,
    private val widthJoystick: Double
) {

    operator fun invoke(controller: Controller): ControllerState? {

        val centerXOutsideCircle = controller.moveInfoTouch.point.x - leftLocationOnScreen
        val centerYOutsideCircle = controller.moveInfoTouch.point.y - topLocationOnScreen

        val centerXInnerCircle =
            centerXOutsideCircle + widthJoystick * controller.power * kotlin.math.cos(controller.angle / 180.0 * Math.PI)
        val centerYInnerCircle =
            centerYOutsideCircle + widthJoystick * controller.power * kotlin.math.sin(controller.angle / 180.0 * Math.PI)

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