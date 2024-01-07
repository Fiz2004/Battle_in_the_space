package com.fiz.battleinthespace.feature_mainscreen.ui.statistics

internal sealed class StatisticViewEffect {
    data class ShowErrorMessage(val message: String) : StatisticViewEffect()
}