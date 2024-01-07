package com.fiz.battleinthespace.feature_gamescreen.ui

internal data class ControllerState(
    val centerXOutsideCircle: Double,
    val centerYOutsideCircle: Double,
    val centerXInnerCircle: Double,
    val centerYInnerCircle: Double,
    val widthJoystick: Double
) : java.io.Serializable