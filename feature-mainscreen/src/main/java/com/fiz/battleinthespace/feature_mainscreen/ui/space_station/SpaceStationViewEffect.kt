package com.fiz.battleinthespace.feature_mainscreen.ui.space_station

internal sealed class SpaceStationViewEffect {
    data class ShowErrorMessage(val message: String) : SpaceStationViewEffect()
}