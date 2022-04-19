package com.fiz.battleinthespace.database

data class Player(
    val id: Int = 0,
    var name: String = "Player",
    var controllerPlayer: Boolean = true,
    var mission: Int = 0,
    var money: Int = 1000,
    var items: List<TypeItems> = ItemsDatabase.getStartItems()
)
