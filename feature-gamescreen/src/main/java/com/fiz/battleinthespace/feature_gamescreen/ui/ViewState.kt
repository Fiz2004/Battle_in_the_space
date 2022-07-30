package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.domain.models.Controller
import com.fiz.battleinthespace.feature_gamescreen.R
import java.io.Serializable

const val SecTimeForRestartForEndGame = 1.0

data class ViewState(
    val isLoading: Boolean = true,
    val controllers: List<Controller>,
    val controllerState: ControllerState?,
    val gameState: GameState?,
    val playSound: (Int) -> Unit,
) : Serializable {

    fun getResourceTextForPauseResumeButton(): Int {
        return if (gameState?.status == com.fiz.feature.game.Game.Companion.GlobalStatusGame.Pause)
            R.string.resume_game_button
        else
            R.string.pause_game_button
    }
}