package com.fiz.android.battleinthespace.base.data.module

import com.fiz.android.battleinthespace.base.data.*
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


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

@RealmClass(embedded = true)
open class TypeItemsRealm(
    var id: Int = 0,
    var nameId: Int = 0,
    var imageId: Int = 0,
    var items: RealmList<ItemRealm> = RealmList()
) : RealmObject()

@RealmClass(embedded = true)
open class ItemRealm(
    var id: Int = 0,
    var nameId: Int = 0,
    var imageId: Int = 0,
    var cost: Int = 0,
    var state: StateProductRealm? = StateProductRealm()
) : RealmObject()
