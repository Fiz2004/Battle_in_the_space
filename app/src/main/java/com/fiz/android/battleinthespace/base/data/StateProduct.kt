package com.fiz.android.battleinthespace.base.data

import io.realm.RealmObject

enum class StateProduct {
    NONE, BUY, INSTALL,
}

enum class StateProductRealm2(val stateProduct: String) {
    NONE("NONE"), BUY("BUY"), INSTALL("INSTALL"),
}

open class StateProductRealm : RealmObject() {
    private var _stateProduct: String = StateProduct.NONE.name
    var stateProduct: StateProduct
        get() = StateProduct.values().first { it.name == _stateProduct }
        set(value) {
            _stateProduct = value.name
        }
}