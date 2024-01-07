package com.fiz.battleinthespace.feature_mainscreen.ui.statistics

internal sealed class StatisticItemUi {

    data class StatisticItem(
        val name: String = "",
        val money: String = "",
    ) : StatisticItemUi()

    data class HeaderItem(
        val text: String = "",
    ) : StatisticItemUi()
}