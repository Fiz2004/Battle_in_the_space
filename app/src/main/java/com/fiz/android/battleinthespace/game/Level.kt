package com.fiz.android.battleinthespace.game

import com.fiz.android.battleinthespace.actor.Bullet
import com.fiz.android.battleinthespace.actor.ListActors
import com.fiz.android.battleinthespace.actor.Player
import com.fiz.android.battleinthespace.engine.Physics

class Level(
    val width: Double,
    val height: Double,
    private var countPlayers: Int = 4,
    private var countMeteorites: Int,
    var players: MutableList<Player>
) {
    var backgrounds: MutableList<MutableList<Int>> = mutableListOf()

    var listActors = ListActors(width, height, players)

    init {
        Physics.createWorld(width, height)
        newRound()
    }

    private fun newRound() {
        createBackgrounds()
        createActors()
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

    private fun createActors() {
        listActors.createSpaceShips(countPlayers)
        listActors.createMeteorites(countMeteorites)
    }

    private fun updatePlayers() {
        for (player in players)
            player.newRound()
    }

    fun update(controller: Array<Controller>, deltaTime: Double): Boolean {
        for (spaceShip in listActors.spaceShips) {
            spaceShip.moveRotate(deltaTime, spaceShip.player.controller.angle.toDouble())
            spaceShip.moveForward(deltaTime, spaceShip.player.controller)

            if (spaceShip.player.controller.isCanFire(deltaTime)) {
                if (spaceShip.inGame)
                    listActors.bullets += Bullet.create(listActors.spaceShips, spaceShip.player.number)
            }
        }

        listActors.update(deltaTime)

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