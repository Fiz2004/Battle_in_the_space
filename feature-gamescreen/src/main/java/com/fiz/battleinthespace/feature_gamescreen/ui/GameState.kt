package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.feature_gamescreen.ui.models.*

data class GameState(
    val round: Int,
    val status: com.fiz.feature.game.Game.Companion.GlobalStatusGame,
    val isMainPlayerInGame: Boolean = true,
    val isWaitRespawn: Boolean = false,
    val backgroundsUi: List<BackgroundUi>,
    val spaceshipsUi: List<SpriteUi>,
    val spaceshipsFlyUi: List<SpriteUi>,
    val bulletsUi: List<SpriteUi>,
    val meteoritesUi: List<MeteoriteSpriteUi>,
    val bulletsAnimationsDestroyUi: List<SpriteUi>,
    val spaceShipsAnimationsDestroyUi: List<SpriteUi>,
    val helpersPlayerUi: List<HelperPlayerUi>,
    val helpersMeteoriteUi: List<HelperMeteoritesUi>,
    val textsInfoUi: List<TextInfoUi>,
    val textRoundInfoUi: TextInfoUi,
    val infoUi: List<InfoUi>,
) : java.io.Serializable

