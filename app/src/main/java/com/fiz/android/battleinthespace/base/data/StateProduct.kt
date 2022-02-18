package com.fiz.android.battleinthespace.base.data

import io.realm.RealmObject

enum class StateProduct {
    NONE, BUY, INSTALL,
}

open class StateProductRealm : RealmObject() {
    private var _stateProduct: String = StateProduct.NONE.name
    var stateProduct: StateProduct
        get() = StateProduct.values().first { it.name == _stateProduct }
        set(value) {
            _stateProduct = value.name
        }
}