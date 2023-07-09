package com.fiz.battleinthespace.database.data_source.network.dto

import androidx.annotation.Keep
import com.fiz.battleinthespace.domain.models.StateProduct

@Keep
enum class StateProductDto : java.io.Serializable {
    NONE, BUY, INSTALL;

    fun toStateProduct():StateProduct{
        return when (this) {
            StateProductDto.NONE -> StateProduct.NONE
            StateProductDto.BUY -> StateProduct.BUY
            StateProductDto.INSTALL -> StateProduct.INSTALL
        }
    }

    companion object {
        fun from(stateProduct: StateProduct): StateProductDto {
            return when (stateProduct) {
                StateProduct.NONE -> StateProductDto.NONE
                StateProduct.BUY -> StateProductDto.BUY
                StateProduct.INSTALL -> StateProductDto.INSTALL
            }
        }
    }
}