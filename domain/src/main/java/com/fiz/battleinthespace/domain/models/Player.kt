package com.fiz.battleinthespace.domain.models

data class Player(
    val id: String = "",
    var name: String = "Player",
    var controllerPlayer: Boolean = true,
    var mission: Int = 0,
    var money: Int = 1000,
    var categoryItems: List<CategoryItem> = emptyList(),
) : java.io.Serializable {

    val weapon: Int
        get() = categoryItems.first().subItems.indexOfFirst { it.state == StateProduct.INSTALL }
}