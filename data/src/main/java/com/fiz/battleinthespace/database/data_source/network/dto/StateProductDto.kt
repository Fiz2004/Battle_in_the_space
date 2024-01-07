package com.fiz.battleinthespace.database.data_source.network.dto

import androidx.annotation.Keep
import com.fiz.battleinthespace.domain.models.StateProduct

@Keep
internal enum class StateProductDto : java.io.Serializable {
    NONE, PREPARE, BUY, INSTALL;

    fun toStateProduct(): StateProduct {
        return when (this) {
            NONE -> StateProduct.NONE
            BUY -> StateProduct.BUY
            INSTALL -> StateProduct.INSTALL
            PREPARE -> StateProduct.PREPARE
        }
    }

    companion object {

        fun from(stateProduct: StateProduct): StateProductDto {
            return when (stateProduct) {
                StateProduct.NONE -> NONE
                StateProduct.BUY -> BUY
                StateProduct.INSTALL -> INSTALL
                StateProduct.PREPARE -> PREPARE
            }
        }
    }
}