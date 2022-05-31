package com.fiz.battleinthespace.feature_gamescreen.ui

data class ControllerState(
    val centerXOutsideCircle: Float,
    val centerYOutsideCircle: Float,
    val centerXInnerCircle: Float,
    val centerYInnerCircle: Float,
    val widthJoystick: Float
) : java.io.Serializable