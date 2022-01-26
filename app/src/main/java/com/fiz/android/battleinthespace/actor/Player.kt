package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.Controller

class Player(var number: Int, val controller: Controller) {
    var score: Int = 0
    var main: Boolean = false
    var life: Int = 3

    fun newGame() {
        score = 0
    }

    fun newRound() {
        life = 3
    }
}