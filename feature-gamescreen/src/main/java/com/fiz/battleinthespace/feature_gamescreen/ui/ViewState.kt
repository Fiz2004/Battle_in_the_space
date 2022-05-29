package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.feature_gamescreen.domain.Controller
import com.fiz.battleinthespace.feature_gamescreen.domain.Game
import java.io.Serializable

private const val SecTimeForRestartForEndGame = 1.0

data class ViewState(
    val controllers: List<Controller>,
    val gameState: GameState,
    val status: StatusCurrentGame = StatusCurrentGame.Playing,
    val playSound: (Int) -> Unit,
    val changed: Boolean = false,
    private var timeToRestart: Double = SecTimeForRestartForEndGame
) : Serializable {

    fun update(deltaTime: Double, game: Game): ViewState {

        if (status == StatusCurrentGame.Pause)
            return this

        if (timeToRestart < 0 || this.status == StatusCurrentGame.NewGame) {
            game.newGame()
            timeToRestart = SecTimeForRestartForEndGame
            return this
        }

        val status = if (timeToRestart == SecTimeForRestartForEndGame)
            game.getStatus()
        else
            false


        if (!status)
            timeToRestart -= deltaTime


        return this
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