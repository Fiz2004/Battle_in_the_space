package com.fiz.battleinthespace.feature_mainscreen.ui.options

internal sealed class OptionsViewEffect {
    data class ShowErrorMessage(val message: String) : OptionsViewEffect()
}