package com.fiz.android.battleinthespace.base.data.module

import com.fiz.android.battleinthespace.base.data.*
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
        TypeItems(id = typeItem.id,
            name = items[index].name,
            imageId = items[index].imageId,
            items = typeItem.items.mapIndexed { indexItem, item ->
                Item(
                    id = typeItem.id,
                    name = items[index].items[indexItem].name,
                    imageId = items[index].items[indexItem].imageId,
                    cost = items[index].items[indexItem].cost,
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

