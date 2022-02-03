package com.fiz.android.battleinthespace.options

import java.util.*

data class Player(
    val id: UUID = UUID.randomUUID(),
    var name: String = "Player",
    var controllerPlayer: Boolean = true
)
