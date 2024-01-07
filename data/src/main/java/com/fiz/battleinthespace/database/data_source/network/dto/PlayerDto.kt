package com.fiz.battleinthespace.database.data_source.network.dto

import androidx.annotation.Keep
import com.fiz.battleinthespace.domain.models.Player

@Keep
internal data class PlayerDto(
    var name: String = "Player",
    var controllerPlayer: Boolean = true,
    var money: Int = 1000,
    var items: List<SubItemDto> = emptyList(),
) : java.io.Serializable {

    companion object {

        fun from(player: Player): PlayerDto {
            return PlayerDto(
                name = player.name,
                controllerPlayer = player.controllerPlayer,
                money = player.money,
                items = player.categoryItems.map {
                    it.subItems.mapNotNull {
                        SubItemDto.from(it)
                    }
                }.flatten(),
            )
        }
    }
}