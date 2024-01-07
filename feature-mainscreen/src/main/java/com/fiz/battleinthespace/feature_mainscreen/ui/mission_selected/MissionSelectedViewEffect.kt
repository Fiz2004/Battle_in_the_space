package com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected

internal sealed class MissionSelectedViewEffect {
    data class ShowErrorMessage(val message: String) : MissionSelectedViewEffect()
}