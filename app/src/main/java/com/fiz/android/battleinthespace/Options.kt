package com.fiz.android.battleinthespace

import android.content.Context
import java.io.Serializable

class Options(context: Context) : Serializable {
    var countPlayers = 4
    var name: MutableList<String> =
        MutableList(4) { i -> "${context.resources.getString(R.string.player)} ${i + 1}" }
    var playerControllerPlayer: MutableList<Boolean> = mutableListOf(true, false, false, false)
}
