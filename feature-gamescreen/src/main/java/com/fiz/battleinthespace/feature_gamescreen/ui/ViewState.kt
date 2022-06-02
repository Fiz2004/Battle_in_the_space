package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.feature_gamescreen.R
import com.fiz.battleinthespace.feature_gamescreen.domain.Controller
import java.io.Serializable

const val SecTimeForRestartForEndGame = 1.0

data class ViewState(
    val controllers: List<Controller>,
    val controllerState: ControllerState?,
    val gameState: GameState,
    val status: StatusCurrentGame = StatusCurrentGame.Playing,
    val playSound: (Int) -> Unit,
    var timeToRestart: Double = SecTimeForRestartForEndGame
) : Serializable {

    fun getResourceTextForPauseResumeButton(): Int {
        return if (status == StatusCurrentGame.Pause)
            R.string.resume_game_button
        else
            R.string.pause_game_button
    }

    fun getStatusPauseOrPlaying(): StatusCurrentGame {
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