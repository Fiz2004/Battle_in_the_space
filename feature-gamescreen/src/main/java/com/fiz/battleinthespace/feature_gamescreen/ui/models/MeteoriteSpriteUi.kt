package com.fiz.battleinthespace.feature_gamescreen.ui.models

import android.graphics.Rect
import android.graphics.RectF

data class MeteoriteSpriteUi(
    val view: Int,
    val viewSize: Int,
    val centerX: Float,
    val centerY: Float,
    val src: Rect,
    val dst: RectF,
    val angle: Float
)