package com.fiz.battleinthespace.feature_gamescreen.domain

import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.data.actor.ListActors
import com.fiz.battleinthespace.feature_gamescreen.data.actor.weapon.Weapon
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.NUMBER_BITMAP_BACKGROUND
import java.io.Serializable

class Level(
    val width: Double,
    val height: Double,
    private var countPlayers: Int = 4,
    private var countMeteorites: Int,
    var players: MutableList<Player>,
    playSound: (Int) -> Unit
) : Serializable {

    var backgrounds: MutableList<MutableList<Int>> = mutableListOf()

    var listActors = ListActors(width, height, players)

    init {
        Physics.createWorld(width, height)
        newRound(playSound)
    }

    private fun newRound(playSound: (Int) -> Unit) {
        createBackgrounds()
        createActors(playSound)
        updatePlayers()
    }

    private fun createBackgrounds() {
        for (n in 0 until height.toInt()) {
            val row: MutableList<Int> = mutableListOf()
            for (k in 0 until width.toInt())
                row += (0 until NUMBER_BITMAP_BACKGROUND).shuffled().first()
            backgrounds += row
        }
    }

    private fun createActors(playSound: (Int) -> Unit) {
        listActors.createSpaceShips(countPlayers)
        listActors.createMeteorites(countMeteorites, playSound)
    }

    private fun updatePlayers() {
        for (player in players)
            player.newRound()
    }

    fun update(
        controller: List<Controller>,
        deltaTime: Double,
        playSound: (Int) -> Unit
    ): Boolean {
        for ((index, spaceShip) in listActors.spaceShips.withIndex()) {
            spaceShip.moveRotate(deltaTime, controller[index].angle.toDouble())
            spaceShip.moveForward(deltaTime, controller[index])

            if (controller[index].isCanFire(deltaTime)) {
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

}