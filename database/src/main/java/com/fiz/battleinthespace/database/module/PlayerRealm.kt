package com.fiz.battleinthespace.database.module

import com.fiz.battleinthespace.database.Item
import com.fiz.battleinthespace.database.ItemsDatabase
import com.fiz.battleinthespace.database.Player
import com.fiz.battleinthespace.database.StateProduct
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class PlayerRealm : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var name: String = "Player"
    var controllerPlayer: Boolean = true
    var mission: Int = 0
    var money: Int = 1000
    var items: RealmList<TypeItemsRealm> = RealmList()
}

fun PlayerRealm.asPlayer(): Player {
    var items = ItemsDatabase.getStartItems()

    items = this.items.mapIndexed { index, typeItem ->
        items[index].copy(id = typeItem.id,
            items = typeItem.items.mapIndexed { indexItem, item ->
                items[index].items[indexItem].copy(
                    id = typeItem.id,
                    state = item.state?.stateProduct ?: StateProduct.NONE
                )
            } as MutableList<Item>)
    }

    return Player(
        id = this.id,
        name = this.name,
        controllerPlayer = this.controllerPlayer,
        mission = this.mission,
        money = this.money,
        items = items
    )
}

