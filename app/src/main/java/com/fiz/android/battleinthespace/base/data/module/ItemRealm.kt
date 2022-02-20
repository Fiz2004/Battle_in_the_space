package com.fiz.android.battleinthespace.base.data.module

import com.fiz.android.battleinthespace.base.data.StateProductRealm
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