package com.fiz.battleinthespace.database.data_source.network.dto

import androidx.annotation.Keep
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.SubItem

@Keep
internal data class SubItemDto(
    val id: String = "",
    var state: StateProductDto = StateProductDto.NONE
) : java.io.Serializable {

    companion object {

        fun from(subItem: SubItem.Item): SubItemDto? {
            return if (subItem.state != StateProduct.NONE)
                SubItemDto(
                    id = subItem.id,
                    state = StateProductDto.from(subItem.state)
                )
            else
                null
        }
    }
}