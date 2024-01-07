package com.fiz.battleinthespace.exceptions

import android.content.Context
import androidx.annotation.StringRes

class RemoteException(override val message: String? = null) : Exception(message) {

    @StringRes
    private var id: Int? = null

    constructor(id: Int) : this() {
        this.id = id
    }

    fun getString(context: Context): String {
        val id = requireNotNull(id)
        return context.getString(id)
    }
}