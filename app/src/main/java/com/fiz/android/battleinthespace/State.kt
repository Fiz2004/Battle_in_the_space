package com.fiz.android.battleinthespace

import com.fiz.android.battleinthespace.actor.Player
import java.io.Serializable

class State(var options: Options) : Serializable {
    lateinit var level: Level
    var round: Int = 1
    var status: String = "playing"
    var players: MutableList<Player> = MutableList(options.countPlayers) { Player() }

    init {
        newGame()
    }

    private fun newGame() {
        round = 0
        newRound()

        for (player in players)
            player.newGame()
        players[0].main = true
    }

    private fun newRound() {
        round += 1

        level = Level(10.0, 10.0, options.countPlayers, round, players)
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
