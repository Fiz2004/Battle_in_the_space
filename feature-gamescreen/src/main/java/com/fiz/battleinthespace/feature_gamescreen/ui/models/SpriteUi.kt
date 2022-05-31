package com.fiz.battleinthespace.feature_gamescreen.ui.models

import android.graphics.Rect
import android.graphics.RectF

data class SpriteUi(
    val value: Int,
    val centerX: Float,
    val centerY: Float,
    val src: Rect,
    val dst: RectF,
    val angle: Float
)