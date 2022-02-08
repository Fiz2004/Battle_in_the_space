package com.fiz.android.battleinthespace.game.data.actor

import com.fiz.android.battleinthespace.game.domain.Controller

class PlayerGame(var number: Int, val controller: Controller) {
    var score: Int = 0
    var main: Boolean = false
    var life: Int = 3

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