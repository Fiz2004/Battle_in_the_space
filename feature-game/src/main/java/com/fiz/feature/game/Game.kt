package com.fiz.feature.game

import com.fiz.battleinthespace.domain.models.Controller
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.feature.game.engine.Physics
import com.fiz.feature.game.models.ListActors
import com.fiz.feature.game.models.weapon.Weapon
import java.io.Serializable
import kotlin.math.min

private const val mSEC_FOR_FPS_60 = ((1.0 / 60.0) * 1000.0).toLong()
const val NUMBER_BITMAP_BACKGROUND = 8

class Game(
    val width: Int,
    val height: Int,
    var round: Int = 0,
    var players: List<Player>,
    var countPlayers: Int = players.size,
    var backgrounds: MutableList<MutableList<Int>> = mutableListOf(),
    var listActors: ListActors = ListActors(width, height, players),
    var status: GlobalStatusGame = GlobalStatusGame.Playing,
) : Serializable {

    private var lastTime = System.currentTimeMillis()
    private var loopStatus: LoopStatusGame = LoopStatusGame.Continue

    private var timeLastFire: MutableList<Double> = MutableList(players.size) { 0.0 }
    private val SEC_MIN_TIME_BETWEEN_FIRE = 0.250

    init {
        Physics.createWorld(width, height)
    }

    fun update(
        controllers: List<Controller>,
        playSound: (Int) -> Unit,
    ) {
        val deltaTime = getDeltaTime()
        if (deltaTime == 0.0) return

        when (status) {
            GlobalStatusGame.NewGame -> newGame(playSound)
            GlobalStatusGame.Playing -> gameLoop(deltaTime, controllers, playSound)
            GlobalStatusGame.Pause -> return
        }
    }

    private fun getDeltaTime(): Double {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - lastTime, mSEC_FOR_FPS_60).toInt() / 1000.0
        lastTime = now
        return deltaTime
    }

    private fun gameLoop(
        deltaTime: Double,
        controllers: List<Controller>,
        playSound: (Int) -> Unit,
    ) {
        when (loopStatus) {
            LoopStatusGame.Continue -> {
                actorsUpdate(controllers, deltaTime, playSound)
            }

            is LoopStatusGame.End -> {
                loopStatus.timeToRestart -= deltaTime
                if (loopStatus.timeToRestart < 0)
                    newGame(playSound)
            }
        }
    }

    private fun actorsUpdate(
        controllers: List<Controller>,
        deltaTime: Double,
        playSound: (Int) -> Unit,
    ) {
        for ((index, spaceShip) in listActors.spaceShips.withIndex()) {
            spaceShip.moveRotate(deltaTime, controllers[index].angle)
            spaceShip.moveForward(deltaTime, controllers[index])

            if (isCanFire(controllers[index], index, deltaTime)) {
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

        listActors.bulletsAnimationDestroy.forEach {
            it.timeShow -= deltaTime
        }
        listActors.bulletsAnimationDestroy = listActors.bulletsAnimationDestroy.filter {
            it.timeShow > 0
        }.toMutableList()

        listActors.spaceShipsAnimationDestroy.forEach {
            it.timeShow -= deltaTime
        }
        listActors.spaceShipsAnimationDestroy = listActors.spaceShipsAnimationDestroy.filter {
            it.timeShow > 0
        }.toMutableList()

        if (players.none { it.life > 0 } || listActors.meteorites.isEmpty()) {
            if (round + 1 == 11)
                loopStatus = LoopStatusGame.End()

            newRound(playSound)
        }

        loopStatus = LoopStatusGame.Continue
    }


    private fun isCanFire(controller: Controller, index: Int, deltaTime: Double): Boolean {
        if (!controller.fire) return false

        if (timeLastFire[index] == 0.0) {
            timeLastFire[index] = SEC_MIN_TIME_BETWEEN_FIRE
            return true
        }

        timeLastFire[index] -= deltaTime

        if (timeLastFire[index] < 0)
            timeLastFire[index] = 0.0

        return false
    }

    private fun newRound(playSound: (Int) -> Unit) {
        round += 1
        val countMeteorites = round

        createBackgrounds()
        createActors(countMeteorites, playSound)
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

    private fun createActors(countMeteorites: Int, playSound: (Int) -> Unit) {
        listActors.createSpaceShips(countPlayers)
        listActors.createMeteorites(countMeteorites, playSound)
    }

    private fun updatePlayers() {
        for (player in players)
            player.newRound()
    }

    fun newGame(playSound: (Int) -> Unit) {
        round = 0
        for (player in players)
            player.newGame()
        players[0].main = true
        status = GlobalStatusGame.Playing
        loopStatus = LoopStatusGame.Continue
        newRound(playSound)
    }

    fun changeStatusPauseOrPlaying() {
        if (status == GlobalStatusGame.NewGame) return
        status = if (status == GlobalStatusGame.Playing)
            GlobalStatusGame.Pause
        else
            GlobalStatusGame.Playing
    }

    fun clickNewGame() {
        status = GlobalStatusGame.NewGame
    }

    companion object {

        private const val SEC_TIME_FOR_RESTART_FOR_END_GAME = 1.0

        enum class GlobalStatusGame : Serializable {
            Playing, Pause, NewGame
        }

        sealed class LoopStatusGame(var timeToRestart: Double = SEC_TIME_FOR_RESTART_FOR_END_GAME) :
            Serializable {
            object Continue : LoopStatusGame()
            class End : LoopStatusGame(timeToRestart = SEC_TIME_FOR_RESTART_FOR_END_GAME)
        }
    }
}