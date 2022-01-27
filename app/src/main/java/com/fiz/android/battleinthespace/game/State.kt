package com.fiz.android.battleinthespace.game

import com.fiz.android.battleinthespace.actor.Player
import com.fiz.android.battleinthespace.options.Options
import java.io.Serializable

class State(var options: Options, controllers: Array<Controller>) : Serializable {
    lateinit var level: Level
    var round: Int = 1
    var status: String = "playing"
    var players: MutableList<Player> = MutableList(options.countPlayers) { index -> Player(index, controllers[index]) }

    init {
        newGame()
    }

    fun newGame() {
        round = 0
        status = "playing"
        newRound()

        for (player in players)
            player.newGame()
        players[0].main = true
    }

    private fun newRound() {
        round += 1

        level = Level(20.0, 20.0, options.countPlayers, round, players)
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
