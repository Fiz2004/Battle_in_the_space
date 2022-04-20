package com.fiz.battleinthespace.database.module

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.realmListOf

class TypeItemsRealm : RealmObject {
    var id: Int = 0
    var nameId: Int = 0
    var imageId: Int = 0
    var items: RealmList<ItemRealm> = realmListOf()
}