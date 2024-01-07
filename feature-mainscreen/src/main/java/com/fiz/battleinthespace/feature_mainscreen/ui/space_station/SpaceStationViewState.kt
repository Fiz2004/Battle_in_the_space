package com.fiz.battleinthespace.feature_mainscreen.ui.space_station

internal data class SpaceStationViewState(
    val isLoading: Boolean = true,
    val money: Int = 0,
    val category: SpaceStationItemUi.CategorySpaceStationItem? = null,
    val items: List<SpaceStationItemUi> = emptyList(),
)