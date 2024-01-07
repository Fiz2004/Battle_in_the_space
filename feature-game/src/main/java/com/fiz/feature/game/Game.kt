package com.fiz.feature.game

import com.fiz.battleinthespace.domain.models.Controller
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.feature.game.engine.Physics
import com.fiz.feature.game.models.ListActors
import com.fiz.feature.game.models.weapon.Weapon
import java.io.Serializable
import java.util.LinkedList
import kotlin.math.min
import kotlin.random.Random

private const val mSEC_FOR_FPS_60 = ((1.0 / 60.0) * 1000.0).toLong()
private const val NUMBER_BITMAP_BACKGROUND = 8
private const val SEC_MIN_TIME_BETWEEN_FIRE = 0.250

data class SoundEvent(
    val type: SoundType,
    val x: Double,
    val y: Double
)

enum class SoundType {
    Overlap, Fire
}

class Game(
    val width: Int,
    val height: Int,
    var round: Int = 0,
    var players: List<Player>,
    private var countPlayers: Int = players.size,
    var backgrounds: List<List<Int>> = mutableListOf(),
    private val queueSound: LinkedList<SoundEvent> = LinkedList<SoundEvent>(),
    val listActors: ListActors = ListActors(width, height, players, queueSound),
    var status: GlobalStatusGame = GlobalStatusGame.Playing,
) : Serializable {

    private var lastTime = System.currentTimeMillis()
    private var loopStatus: LoopStatusGame = LoopStatusGame.Continue

    private var timeLastFire: MutableList<Double> = MutableList(players.size) { 0.0 }

    init {
        Physics.createWorld(width, height)
    }

    fun update(
        controllers: List<Controller>,
    ) {
        val deltaTime = getDeltaTime()
        if (deltaTime == 0.0) return

        when (status) {
            GlobalStatusGame.NewGame -> newGame()
            GlobalStatusGame.Playing -> gameLoop(deltaTime, controllers)
            GlobalStatusGame.Pause -> return
            GlobalStatusGame.Finish -> return
        }
    }

    fun poolQueueSound(): SoundEvent? {
        return queueSound.poll()
    }

    private fun getDeltaTime(): Double {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - lastTime, mSEC_FOR_FPS_60).toInt() / 1000.0
        lastTime = now
        return deltaTime
    }

    private fun gameLoop(
        deltaTime: Double,
        controllers: List<Controller>
    ) {
        when (loopStatus) {
            LoopStatusGame.Continue -> {
                actorsUpdate(controllers, deltaTime)
            }

            is LoopStatusGame.End -> {
                loopStatus.timeToRestart -= deltaTime
                if (loopStatus.timeToRestart < 0)
                    newGame()
            }

            is LoopStatusGame.Finish -> {
                loopStatus.timeToRestart -= deltaTime
                if (loopStatus.timeToRestart < 0)
                    exitGame()
            }
        }
    }

    private fun exitGame() {
        status = GlobalStatusGame.Finish
    }

    private fun actorsUpdate(
        controllers: List<Controller>,
        deltaTime: Double,
    ) {
        for ((index, spaceShip) in listActors.spaceShips.withIndex()) {
            spaceShip.moveRotate(deltaTime, controllers[index].angle)
            spaceShip.moveForward(deltaTime, controllers[index])

            if (isCanFire(controllers[index], index, deltaTime)) {
                if (spaceShip.inGame) {
                    listActors.weapons += Weapon.create(
                        listActors.spaceShips,
                        spaceShip.number,
                        spaceShip.weapon
                    )
                    queueSound.add(
                        SoundEvent(
                            type = SoundType.Fire,
                            x = spaceShip.center.x,
                            y = spaceShip.center.y
                        )
                    )
                }
            }
        }

        listActors.update(deltaTime)

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

        if (listActors.spaceShips.none { it.life > 0 } || listActors.meteorites.isEmpty()) {
            if (round + 1 == 11)
                loopStatus = LoopStatusGame.End()

            newRound()
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

    private fun newRound() {
        round += 1
        val countMeteorites = round

        createBackgrounds()
        createActors(countMeteorites)
        updatePlayers()
    }

    private fun createBackgrounds() {
        val getRow = { (0 until width).map { Random.nextInt(NUMBER_BITMAP_BACKGROUND) } }
        backgrounds = (0 until height).map { getRow() }
    }

    private fun createActors(countMeteorites: Int) {
        listActors.createSpaceShips(countPlayers)
        listActors.createMeteorites(countMeteorites)
    }

    private fun updatePlayers() {
        listActors.spaceShips.forEach { spaceShip -> spaceShip.life = 3 }
    }

    fun newGame() {
        round = 0
        listActors.spaceShips.forEach { spaceShip -> spaceShip.score = 0 }
        status = GlobalStatusGame.Playing
        loopStatus = LoopStatusGame.Continue
        newRound()
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
            Playing, Pause, NewGame, Finish
        }

        sealed class LoopStatusGame(var timeToRestart: Double = SEC_TIME_FOR_RESTART_FOR_END_GAME) :
            Serializable {
            object Continue : LoopStatusGame()
            class End : LoopStatusGame(timeToRestart = SEC_TIME_FOR_RESTART_FOR_END_GAME)
            class Finish : LoopStatusGame(timeToRestart = SEC_TIME_FOR_RESTART_FOR_END_GAME)
        }
    }
}