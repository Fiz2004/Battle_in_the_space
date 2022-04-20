package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.database.models.Player
import com.fiz.battleinthespace.feature_gamescreen.data.actor.ListActors
import com.fiz.battleinthespace.feature_gamescreen.data.actor.PlayerGame
import java.io.Serializable

class StateGame(
    players: List<Player>,
    controllers: Array<Controller>
) : Serializable, ListActors.CallBacks {
    lateinit var level: Level
    var countPlayers = players.size
    var name = players.map { it.name } as MutableList<String>
    var round: Int = 1
    var status: String = "playing"
    var playerGames: MutableList<PlayerGame> =
        MutableList(countPlayers) { index -> PlayerGame(index, controllers[index], players[index]) }

    private lateinit var callbacks: ListActors.CallBacks

    init {
        newGame()
    }

    fun setCallBacks(callbacks: ListActors.CallBacks) {
        this.callbacks = callbacks
    }

    override fun playSound(numberSound: Int) {
        callbacks.playSound(numberSound)
    }

    fun newGame() {
        round = 0
        status = "playing"
        newRound()

        for (player in playerGames)
            player.newGame()
        playerGames[0].main = true
    }

    private fun newRound() {
        round += 1

        level = Level(20.0, 20.0, countPlayers, round, playerGames)
        level.setCallBacks(this)
    }

    fun update(controller: Array<Controller>, deltaTime: Double): Boolean {
        val levelStatus = level.update(controller, deltaTime)

        if (!levelStatus)
            newRound()

        if (round == 11)
            return false

        return true
    }

    fun clickPause() {
        status = if (status == "playing")
            "pause"
        else
            "playing"
    }
}
