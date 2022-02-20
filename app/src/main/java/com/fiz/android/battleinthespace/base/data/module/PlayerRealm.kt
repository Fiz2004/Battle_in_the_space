package com.fiz.android.battleinthespace.base.data.module

import com.fiz.android.battleinthespace.base.data.Item
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.base.data.TypeItems
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
    return Player(
        id = this.id,
        name = this.name,
        controllerPlayer = this.controllerPlayer,
        mission = this.mission,
        money = this.money,
        items = this.items.map { typeItem ->
            TypeItems(id = typeItem.id,
                name = typeItem.nameId,
                imageId = typeItem.imageId,
                items = typeItem.items.map {
                    Item(
                        id = typeItem.id,
                        name = it.nameId,
                        imageId = it.imageId,
                        cost = it.cost,
                        state = it.state?.stateProduct ?: StateProduct.NONE
                    )
                } as MutableList<Item>)
        }
    )
}

