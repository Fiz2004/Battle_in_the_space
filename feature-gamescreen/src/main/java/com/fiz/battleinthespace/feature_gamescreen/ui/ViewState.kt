package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.feature_gamescreen.R
import java.io.Serializable

const val SecTimeForRestartForEndGame = 1.0

internal data class ViewState(
    val isFinish: Boolean = false,
    val isLoading: Boolean = true,
    val controllerState: ControllerState?,
    val gameState: GameState?,
) : Serializable {

    fun getResourceTextForPauseResumeButton(): Int {
        return if (gameState?.status == com.fiz.feature.game.Game.Companion.GlobalStatusGame.Pause)
            R.string.resume_game_button
        else
            R.string.pause_game_button
    }
}