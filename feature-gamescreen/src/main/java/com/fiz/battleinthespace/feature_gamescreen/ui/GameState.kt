package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.feature_gamescreen.domain.Controller
import com.fiz.battleinthespace.feature_gamescreen.domain.Level
import java.io.Serializable

private const val SecTimeForRestartForEndGame = 1.0

data class GameState(
    val controllers: List<Controller>,
    var level: Level,
    var round: Int,
    var status: StatusCurrentGame,
    val playSound: (Int) -> Unit,
    val changed: Boolean = false,
    private var timeToRestart: Double = SecTimeForRestartForEndGame
) : Serializable {

    fun newGame() {
        round = 0
        status = StatusCurrentGame.Playing
        newRound()

        level.newGame()
    }

    private fun newRound() {
        round += 1

        level = Level(20, 20, round, level.players, ai = level.ai, playSound = playSound)
    }


    fun update(deltaTime: Double): GameState {

        if (status == StatusCurrentGame.Pause)
            return this

        if (timeToRestart < 0 || this.status == StatusCurrentGame.NewGame) {
            newGame()
            timeToRestart = SecTimeForRestartForEndGame
            return this
        }

        val status = if (timeToRestart == SecTimeForRestartForEndGame)
            updateGame(deltaTime)
        else
            false


        if (!status)
            timeToRestart -= deltaTime


        return this
    }

    private fun updateGame(deltaTime: Double): Boolean {

        val levelStatus = level.update(controllers, deltaTime)

        if (!levelStatus)
            newRound()

        if (round == 11)
            return false

        return true
    }

    fun clickPause(): StatusCurrentGame {
        return if (status == StatusCurrentGame.Playing)
            StatusCurrentGame.Pause
        else
            StatusCurrentGame.Playing
    }

    companion object {
        enum class StatusCurrentGame : Serializable {
            Playing, Pause, NewGame
        }
    }
}