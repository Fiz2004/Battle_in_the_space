package com.fiz.android.battleinthespace.game.domain

import com.fiz.android.battleinthespace.game.data.actor.ListActors
import com.fiz.android.battleinthespace.game.data.actor.PlayerGame
import java.io.Serializable

class StateGame(
    var countPlayers: Int,
    var name: MutableList<String>,
    controllers: Array<Controller>
) : Serializable, ListActors.CallBacks {
    lateinit var level: Level
    var round: Int = 1
    var status: String = "playing"
    var playerGames: MutableList<PlayerGame> =
        MutableList(countPlayers) { index -> PlayerGame(index, controllers[index]) }

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
