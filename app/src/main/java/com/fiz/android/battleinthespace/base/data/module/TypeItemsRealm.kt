package com.fiz.android.battleinthespace.base.data.module

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
open class TypeItemsRealm(
    var id: Int = 0,
    var nameId: Int = 0,
    var imageId: Int = 0,
    var items: RealmList<ItemRealm> = RealmList()
) : RealmObject()