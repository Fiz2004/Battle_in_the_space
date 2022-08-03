package com.fiz.battleinthespace.domain.models

data class Player(
    val id: String = "",
    var name: String = "Player",
    var controllerPlayer: Boolean = true,
    var mission: Int = 0,
    var money: Int = 1000,
    var items: List<TypeItems> = emptyList(),
) : java.io.Serializable {

    val weapon: Int get() = items.first().items.indexOfFirst { it.state == StateProduct.INSTALL }
}