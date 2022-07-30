package com.fiz.battleinthespace.database.data_source.network.dto

import com.fiz.battleinthespace.domain.models.Player

data class PlayerDto(
    var name: String = "Player",
    var controllerPlayer: Boolean = true,
    var money: Int = 1000,
    var items: List<ItemDto> = emptyList(),
    var score: Int = 0,
) : java.io.Serializable {
    companion object {
        fun from(player: Player): PlayerDto {
            return PlayerDto(
                name = player.name,
                controllerPlayer = player.controllerPlayer,
                money = player.money,
                items = player.items.map {
                    it.items.mapNotNull {
                        ItemDto.from(it)
                    }
                }.flatten(),
                score = player.score,
            )
        }
    }
}