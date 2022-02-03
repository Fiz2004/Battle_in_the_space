package com.fiz.android.battleinthespace.game

import android.media.SoundPool
import android.util.SparseIntArray
import com.fiz.android.battleinthespace.actor.Bullet
import com.fiz.android.battleinthespace.actor.ListActors
import com.fiz.android.battleinthespace.actor.PlayerGame
import com.fiz.android.battleinthespace.engine.Physics
import java.io.Serializable

class Level(
    val width: Double,
    val height: Double,
    private var countPlayers: Int = 4,
    private var countMeteorites: Int,
    var playerGames: MutableList<PlayerGame>
) : Serializable {
    var backgrounds: MutableList<MutableList<Int>> = mutableListOf()

    var listActors = ListActors(width, height, playerGames)

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
        for (player in playerGames)
            player.newRound()
    }

    fun update(
        controller: Array<Controller>,
        deltaTime: Double,
        soundMap: SparseIntArray,
        soundPool: SoundPool
    ): Boolean {
        for (spaceShip in listActors.spaceShips) {
            spaceShip.moveRotate(deltaTime, spaceShip.playerGame.controller.angle.toDouble())
            spaceShip.moveForward(deltaTime, spaceShip.playerGame.controller)

            if (spaceShip.playerGame.controller.isCanFire(deltaTime)) {
                if (spaceShip.inGame) {
                    listActors.bullets += Bullet.create(listActors.spaceShips, spaceShip.playerGame.number)
                    soundPool.play(soundMap.get(1), 1F, 1F, 1, 0, 1F)
                }
            }
        }

        listActors.update(deltaTime, soundMap, soundPool)

        listActors.listAnimationDestroy.forEach {
            it.timeShow -= deltaTime
        }
        listActors.listAnimationDestroy = listActors.listAnimationDestroy.filter {
            it.timeShow > 0
        }.toMutableList()

        if (playerGames.none { it.life > 0 } || listActors.meteorites.isEmpty())
            return false

        return true
    }

}