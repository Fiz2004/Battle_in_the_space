package com.fiz.android.battleinthespace.base.data.module

import com.fiz.android.battleinthespace.base.data.Item
import com.fiz.android.battleinthespace.base.data.ItemsDatabase
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.StateProduct
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

