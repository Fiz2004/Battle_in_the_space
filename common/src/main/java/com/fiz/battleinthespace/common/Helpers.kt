package com.fiz.battleinthespace.common

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import java.io.Serializable


@Suppress("DEPRECATION")
fun <T : Serializable?> getSerializable(
    savedInstanceState: Bundle?,
    clazz: Class<T>
): T {
    @Suppress("UNCHECKED_CAST")
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        savedInstanceState?.getSerializable(clazz.simpleName, clazz)!!
    else
        savedInstanceState?.getSerializable(clazz.simpleName) as T
}


fun getMaxTextWidth(texts: List<String>, textSize: Float): Int {
    val paint = Paint()
    paint.textSize = textSize
    return texts
        .map { text -> paint.measureText(text) }
        .max().toInt()
}