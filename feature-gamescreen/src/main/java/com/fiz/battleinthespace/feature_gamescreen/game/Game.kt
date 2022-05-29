package com.fiz.battleinthespace.feature_gamescreen.game

import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.NUMBER_BITMAP_BACKGROUND
import com.fiz.battleinthespace.feature_gamescreen.domain.AI
import com.fiz.battleinthespace.feature_gamescreen.domain.Controller
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.models.ListActors
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon
import java.io.Serializable

class Game(
    val width: Int,
    val height: Int,
    var round: Int = 0,
    var players: MutableList<Player>,
    var countPlayers: Int = players.size,
    var ai: MutableList<AI?>,
    val playSound: (Int) -> Unit,
    var backgrounds: MutableList<MutableList<Int>> = mutableListOf(),
    var listActors: ListActors = ListActors(width, height, players)
) : Serializable {

    var currentStatus: Boolean = true

    init {
        Physics.createWorld(width, height)
        newGame()
        newRound()
    }

    fun update(
        controllers: List<Controller>,
        deltaTime: Double,
    ) {

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

        if (players.none { it.life > 0 } || listActors.meteorites.isEmpty()) {
            if (round + 1 == 11)
                currentStatus = false

            newRound()
        }

        currentStatus = true
    }

    fun newRound() {
        round += 1
        val countMeteorites = round

        createBackgrounds()
        createActors(countMeteorites)
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

    private fun createActors(countMeteorites: Int) {
        listActors.createSpaceShips(countPlayers)
        listActors.createMeteorites(countMeteorites, playSound)
    }

    private fun updatePlayers() {
        for (player in players)
            player.newRound()
    }

    fun newGame() {
        round = 0
        for (player in players)
            player.newGame()
        players[0].main = true
    }

    fun getStatus(): Boolean {
        return currentStatus
    }

}