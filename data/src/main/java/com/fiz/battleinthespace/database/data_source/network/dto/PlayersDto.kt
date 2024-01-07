package com.fiz.battleinthespace.database.data_source.network.dto

import androidx.annotation.Keep
import com.fiz.battleinthespace.database.ItemsDatabase
import com.fiz.battleinthespace.domain.models.Player

@Keep
internal data class PlayersDto(
    val players: List<PlayerDto> = emptyList(),
    var mission: Int = 0,
) : java.io.Serializable {

    fun toPlayers(): List<Player> {
        return players.map { playerDto ->
            val items = ItemsDatabase.getStartItems().map { typeItems ->
                typeItems.copy(subItems = typeItems.subItems.map { item ->
                    val findItem = playerDto.items.find { it.id == item.id }
                    if (findItem != null)
                        item.copy(
                            state = findItem.state.toStateProduct()
                        )
                    else
                        item
                })
            }
            Player(
                mission = mission,
                name = playerDto.name,
                controllerPlayer = playerDto.controllerPlayer,
                money = playerDto.money,
                categoryItems = items,
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