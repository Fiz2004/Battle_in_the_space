package com.fiz.battleinthespace.feature_gamescreen.data.actor

import com.fiz.battleinthespace.database.Player
import com.fiz.battleinthespace.database.StateProduct
import com.fiz.battleinthespace.feature_gamescreen.domain.Controller


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