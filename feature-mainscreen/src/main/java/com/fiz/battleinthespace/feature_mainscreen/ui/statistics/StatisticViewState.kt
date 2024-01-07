package com.fiz.battleinthespace.feature_mainscreen.ui.statistics

internal data class StatisticViewState(
    val isLoading: Boolean = true,
    val items: List<StatisticItemUi> = emptyList()
)