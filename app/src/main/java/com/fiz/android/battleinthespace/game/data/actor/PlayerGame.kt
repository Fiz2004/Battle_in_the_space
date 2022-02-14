package com.fiz.android.battleinthespace.game.data.actor

import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.Item
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.game.domain.Controller

class PlayerGame(var number: Int, val controller: Controller, player: Player) {
    var score: Int = 0
    var main: Boolean = false
    var life: Int = 3
    private val items = player.items
    var weapon: Int = 0

    init {
        val allWeapon = Item.getListProduct(R.string.weapon, items)
        val installWeapon = allWeapon.filter { it.state == StateProduct.INSTALL }[0]
        val index = allWeapon.indexOfFirst { it.name == installWeapon.name }
        weapon = index
    }

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