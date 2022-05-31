package com.fiz.battleinthespace.domain.models

data class Player(
    val id: Int = 0,
    var name: String = "Player",
    var controllerPlayer: Boolean = true,
    var mission: Int = 0,
    var money: Int = 1000,
    var items: List<TypeItems>,
    var score: Int = 0,
    var number: Int = 0,
    var main: Boolean = false,
    var life: Int = 3
) : java.io.Serializable {

    fun newGame() {
        score = 0
    }

    fun newRound() {
        life = 3
    }

    val weapon: Int get() = items[0].items.indexOfFirst { it.state == StateProduct.INSTALL }
}