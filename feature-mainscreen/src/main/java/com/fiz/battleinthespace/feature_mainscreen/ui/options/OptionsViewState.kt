package com.fiz.battleinthespace.feature_mainscreen.ui.options

import com.fiz.battleinthespace.domain.models.Player

internal data class OptionsViewState(
    val isLoading: Boolean = true,
    val players: List<Player> = emptyList(),
    val countPlayer: Int = 4,
    val email: String? = null,
)