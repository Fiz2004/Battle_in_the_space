package com.fiz.battleinthespace.database.entity

import com.fiz.battleinthespace.domain.models.StateProduct
import io.realm.RealmObject

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
