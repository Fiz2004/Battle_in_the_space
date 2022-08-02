package com.fiz.battleinthespace.common

import android.os.Build
import android.os.Bundle
import android.view.View
import java.io.Serializable

fun View.setVisible(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}

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