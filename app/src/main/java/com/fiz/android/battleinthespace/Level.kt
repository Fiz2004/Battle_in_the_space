package com.fiz.android.battleinthespace

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
        for (player in 0 until countPlayers) {
            if (controller[player].power != 0F) {
                listActors.spaceShips[player].isFly = true
                updateMoveRotate(player, deltaTime, controller[player])
                updateMoveForward(player, deltaTime, controller[player])
            } else {
                listActors.spaceShips[player].isFly = false
            }
            if (controller[player].fire && controller[player].isCanFire(deltaTime)) {
                updatePressFire(player, deltaTime)
                // Сделано чтобы хотя бы один раз был выстрел, надо перенести это в контроллер
                if (!controller[player].rightSide.touch)
                    controller[player].fire = false
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

    private fun updateMoveRotate(numberPlayer: Int, deltaTime: Double, controller: Controller) {
        listActors.spaceShips[numberPlayer].moveRotate(deltaTime, controller)
    }

    private fun updateMoveForward(numberPlayer: Int, deltaTime: Double, controller: Controller) {
        listActors.spaceShips[numberPlayer].moveForward(deltaTime, controller)
    }

    private fun updatePressFire(numberPlayer: Int, deltaTime: Double) {
        if (listActors.spaceShips[numberPlayer].inGame)
            listActors.bullets += Bullet.create(listActors.spaceShips, numberPlayer)
    }
}