package com.fiz.battleinthespace.database.data_source.network.dto

import androidx.annotation.Keep
import com.fiz.battleinthespace.domain.models.Item
import com.fiz.battleinthespace.domain.models.StateProduct
@Keep
data class ItemDto(
    val id: String = "",
    var state: StateProductDto = StateProductDto.NONE
) : java.io.Serializable {
    companion object {
        fun from(item: Item): ItemDto? {
            return if (item.state != StateProduct.NONE)
                ItemDto(
                    id = item.id,
                    state = StateProductDto.from(item.state)
                )
            else
                null
        }
    }
}