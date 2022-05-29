package com.fiz.battleinthespace.feature_gamescreen.ui

import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.models.DrawableAnimationDestroy
import com.fiz.battleinthespace.feature_gamescreen.game.models.Meteorite
import com.fiz.battleinthespace.feature_gamescreen.game.models.MoveableActor
import com.fiz.battleinthespace.feature_gamescreen.game.models.SpaceShip
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon

data class GameState(
    val width: Int,
    val height: Int,
    val round: Int,
    val players: MutableList<Player>,
    val countPlayers: Int = players.size,
    val backgrounds: List<List<Int>>,
    val actors: MutableList<MoveableActor>,
    val listAnimationDestroy: MutableList<DrawableAnimationDestroy>,
    var spaceShips: MutableList<SpaceShip>,
    var bullets: MutableList<Weapon>,
    var meteorites: MutableList<Meteorite>
) {
    companion object {
        fun getStateFromGame(game: Game): GameState {
            if (game.listActors.listAnimationDestroy.any { it.timeShow <= 0 }) {
                var a = 1
                a += 1
                println(a)
            }
            if (game.listActors.listAnimationDestroy.toMutableList().any { it.timeShow <= 0 }) {
                var a = 1
                a += 1
                println(a)
            }
            return GameState(
                game.width,
                game.height,
                game.round,
                game.players,
                game.countPlayers,
                game.backgrounds.toList(),
                game.listActors.actors.toMutableList(),
                game.listActors.listAnimationDestroy.toMutableList(),
                game.listActors.spaceShips.toMutableList(),
                game.listActors.bullets.toMutableList(),
                game.listActors.meteorites.toMutableList(),
            )
        }
    }
}