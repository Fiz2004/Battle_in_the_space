package com.fiz.battleinthespace.database.module

import com.fiz.battleinthespace.database.StateProductRealm
import io.realm.RealmObject

class ItemRealm : RealmObject {
    var id: Int = 0
    var nameId: Int = 0
    var imageId: Int = 0
    var cost: Int = 0
    var state: StateProductRealm? = StateProductRealm()
}