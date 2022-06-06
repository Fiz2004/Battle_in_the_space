package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.feature_gamescreen.R
import com.fiz.battleinthespace.feature_gamescreen.domain.Controller
import com.fiz.battleinthespace.feature_gamescreen.game.Game
import java.io.Serializable

const val SecTimeForRestartForEndGame = 1.0

data class ViewState(
    val controllers: List<Controller>,
    val controllerState: ControllerState?,
    val gameState: GameState,
    val playSound: (Int) -> Unit,
) : Serializable {

    fun getResourceTextForPauseResumeButton(): Int {
        return if (gameState.status == Game.Companion.StatusCurrentGame.Pause)
            R.string.resume_game_button
        else
            R.string.pause_game_button
    }
}