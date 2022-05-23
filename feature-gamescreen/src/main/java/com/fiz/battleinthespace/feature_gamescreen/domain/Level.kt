package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.data.actor.ListActors
import com.fiz.battleinthespace.feature_gamescreen.data.actor.weapon.Weapon
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.NUMBER_BITMAP_BACKGROUND
import java.io.Serializable

class Level(
    val width: Int,
    val height: Int,
    private var countMeteorites: Int,
    var players: MutableList<Player>,
    private var countPlayers: Int = players.size,
    var ai: MutableList<AI?>,
    val playSound: (Int) -> Unit,
    var backgrounds: MutableList<MutableList<Int>> = mutableListOf(),
    var listActors: ListActors = ListActors(width, height, players)
) : Serializable {

    init {
        Physics.createWorld(width, height)
        newRound()
    }

    fun update(
        controllers: List<Controller>,
        deltaTime: Double,
    ): Boolean {

        for ((index, player) in players.withIndex()) {
            if (ai[index] != null && !player.main) {
                ai[index]?.update(
                    index,
                    controllers[index],
                    this
                )
            }
        }

        for ((index, spaceShip) in listActors.spaceShips.withIndex()) {
            spaceShip.moveRotate(deltaTime, controllers[index].angle.toDouble())
            spaceShip.moveForward(deltaTime, controllers[index])

            if (controllers[index].isCanFire(deltaTime)) {
                if (spaceShip.inGame) {
                    listActors.bullets += Weapon.create(
                        listActors.spaceShips,
                        spaceShip.player.number,
                        spaceShip.player.weapon
                    )
                    playSound(1)
                }
            }
        }

        listActors.update(deltaTime, playSound)

        listActors.listAnimationDestroy.forEach {
            it.timeShow -= deltaTime
        }
        listActors.listAnimationDestroy = listActors.listAnimationDestroy.filter {
            it.timeShow > 0
        }.toMutableList()

        if (players.none { it.life > 0 } || listActors.meteorites.isEmpty())
            return false

        return true
    }

    private fun newRound() {
        createBackgrounds()
        createActors()
        updatePlayers()
    }

    private fun createBackgrounds() {
        for (n in 0 until height) {
            val row: MutableList<Int> = mutableListOf()
            for (k in 0 until width)
                row += (0 until NUMBER_BITMAP_BACKGROUND).shuffled().first()
            backgrounds += row
        }
    }

    private fun createActors() {
        listActors.createSpaceShips(countPlayers)
        listActors.createMeteorites(countMeteorites, playSound)
    }

    private fun updatePlayers() {
        for (player in players)
            player.newRound()
    }

    fun newGame() {
        for (player in players)
            player.newGame()
        players[0].main = true
    }

}