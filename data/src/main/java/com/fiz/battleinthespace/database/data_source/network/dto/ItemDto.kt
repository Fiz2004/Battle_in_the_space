package com.fiz.battleinthespace.database.data_source.network.dto

import com.fiz.battleinthespace.domain.models.Item
import com.fiz.battleinthespace.domain.models.StateProduct

data class ItemDto(
    val id: String = "",
    var state: StateProduct = StateProduct.NONE
) : java.io.Serializable {
    companion object {
        fun from(item: Item): ItemDto? {
            return if (item.state != StateProduct.NONE)
                ItemDto(
                    id = item.id,
                    state = item.state
                )
            else
                null
        }
    }
}