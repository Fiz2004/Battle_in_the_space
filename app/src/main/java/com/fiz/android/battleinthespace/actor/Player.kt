package com.fiz.android.battleinthespace.actor

class Player {
    lateinit var spaceShip: SpaceShip
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