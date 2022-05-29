package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.data.actor.ListActors
import com.fiz.battleinthespace.feature_gamescreen.domain.AI

data class GameState(
    val width: Int,
    val height: Int,
    val round: Int = 0,
    val players: MutableList<Player>,
    val countPlayers: Int = players.size,
    val ai: MutableList<AI?>,
    val playSound: (Int) -> Unit,
    val backgrounds: MutableList<MutableList<Int>> = mutableListOf(),
    val listActors: ListActors = ListActors(width, height, players)
)