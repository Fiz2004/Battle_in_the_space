package com.fiz.android.battleinthespace.base.data.module

import com.fiz.android.battleinthespace.base.data.ItemsDatabase
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.TypeItems
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey


open class PlayerRealm : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var name: String = "Player"
    var controllerPlayer: Boolean = true
    var mission: Int = 0
    var money: Int = 1000

    @Ignore
    var items: List<TypeItems> = ItemsDatabase.getStartItems()
}

fun PlayerRealm.asPlayer(): Player {
    return Player(
        id = this.id,
        name = this.name,
        controllerPlayer = this.controllerPlayer,
        mission = this.mission,
        money = this.money,
        items = ItemsDatabase.getStartItems()
    )
}

//open class TypeItemsRealm(@PrimaryKey val id: Int, val name: Int, val imageId: Int, var items: RealmList<Item>)
//
//open class Item(@PrimaryKey val id: Int, val name: Int, val imageId: Int, val cost: Int, var state: StateProduct)
