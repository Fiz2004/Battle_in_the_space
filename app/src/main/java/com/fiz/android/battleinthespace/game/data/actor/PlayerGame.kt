package com.fiz.android.battleinthespace.game.data.actor

import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.game.domain.Controller

class PlayerGame(var number: Int, val controller: Controller, player: Player) {
    var score: Int = 0
    var main: Boolean = false
    var life: Int = 3
    private val items = player.items
    var weapon: Int = items[0].items.indexOfFirst { it.state == StateProduct.INSTALL }

    init {
        controller.linkPlayer(this)
    }

    fun newGame() {
        score = 0
    }

    fun newRound() {
        life = 3
    }
}