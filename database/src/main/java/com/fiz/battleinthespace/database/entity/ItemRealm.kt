package com.fiz.battleinthespace.database.entity

import com.fiz.battleinthespace.database.models.StateProductRealm
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
open class ItemRealm(
    var id: Int = 0,
    var nameId: Int = 0,
    var imageId: Int = 0,
    var cost: Int = 0,
    var state: StateProductRealm? = StateProductRealm()
) : RealmObject()