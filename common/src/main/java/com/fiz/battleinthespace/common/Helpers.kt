package com.fiz.battleinthespace.common

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import java.io.Serializable

fun <T : Serializable> Bundle.serializable(key: String?, klass: Class<T>): T? {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getSerializable(key, klass)
        }

        else -> {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            getSerializable(key) as? T
        }
    }
}

fun <T : Serializable> Bundle.parcelable(key: String?, klass: Class<T>): T? {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getParcelable(key, klass)
        }

        else -> {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            getParcelable(key) as? T
        }
    }
}

fun getMaxTextWidth(texts: List<String>, textSize: Float): Int {
    val paint = Paint()
    paint.textSize = textSize
    return texts
        .map { text -> paint.measureText(text) }
        .max().toInt()
}