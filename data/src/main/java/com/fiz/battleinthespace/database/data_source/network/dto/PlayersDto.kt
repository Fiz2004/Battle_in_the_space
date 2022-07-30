package com.fiz.battleinthespace.database.data_source.network.dto

import com.fiz.battleinthespace.database.ItemsDatabase
import com.fiz.battleinthespace.domain.models.Player

data class PlayersDto(
    val players: List<PlayerDto> = emptyList(),
    var mission: Int = 0,
) : java.io.Serializable {

    fun toPlayers(): List<Player> {
        return players.map { playerDTO ->
            val items = ItemsDatabase.getStartItems().map { typeItems ->
                typeItems.copy(items = typeItems.items.map { item ->
                    val findItem = playerDTO.items.find { it.id == item.id }
                    if (findItem != null)
                        item.copy(
                            state = findItem.state
                        )
                    else
                        item
                })
            }
            Player(
                mission = mission,
                name = playerDTO.name,
                controllerPlayer = playerDTO.controllerPlayer,
                money = playerDTO.money,
                score = playerDTO.score,
                items = items,
            )
        }
    }

    companion object {
        fun from(players: List<Player>): PlayersDto {
            return PlayersDto(
                players = players.map { PlayerDto.from(it) },
                mission = players[0].mission
            )
        }
    }
}