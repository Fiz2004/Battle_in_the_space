package com.fiz.battleinthespace.feature_gamescreen.ui

import android.content.Context
import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.domain.AI
import com.fiz.battleinthespace.feature_gamescreen.domain.Controller
import com.fiz.battleinthespace.feature_gamescreen.domain.Level
import com.fiz.battleinthespace.feature_gamescreen.domain.SoundUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable
import javax.inject.Inject
import kotlin.math.min

data class GameState(
    val controllers: List<Controller>,
    val players: MutableList<Player>,
    var level: Level,
    var round: Int,
    var status: String,
    var ai: MutableList<AI?>,
    val playSound: (Int) -> Unit,
    val changed: Boolean = false
) : Serializable {

    fun newGame() {
        round = 0
        status = "playing"
        newRound()

        for (player in players)
            player.newGame()
        players[0].main = true
    }

    private fun newRound() {
        round += 1

        level = Level(20.0, 20.0, players.size, round, players, playSound)
    }

    fun update(controller: List<Controller>, deltaTime: Double): Boolean {
        val levelStatus = level.update(controller, deltaTime, playSound)

        if (!levelStatus)
            newRound()

        if (round == 11)
            return false

        return true
    }

    fun clickPause() {
        status = if (status == "playing")
            "pause"
        else
            "playing"
    }
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val soundUseCase: SoundUseCase,
    @ApplicationContext context: Context
) : ViewModel() {

    var gameState = MutableStateFlow(run {
        val countPlayers = playerRepository.getCountPlayers()

        val players = mutableListOf<Player>()

        for ((index, player) in playerRepository.getPlayers().withIndex())
            if (index < countPlayers)
                players.add(player.copy(number = index))

        val round = 1
        val gameState = GameState(
            controllers = List(players.size) { Controller(scaledDensity = context.resources.displayMetrics.scaledDensity) },
            players = players,
            round = round,
            status = "playing",
            level = Level(20.0, 20.0, countPlayers, round, players, ::playSound),
            playSound = ::playSound,
            ai = MutableList(4) { null }
        )
        gameState.newGame()
        gameState
    })
        private set

    private var job: Job? = null

    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0

    fun loadState(gameState: GameState?) {
        this.gameState.value = gameState ?: return
    }

    fun startGame() {
        viewModelScope.launch(Dispatchers.Default) {
            job?.cancelAndJoin()
            job = viewModelScope.launch(Dispatchers.Default, block = gameLoop())
        }
    }

    private fun gameLoop(): suspend CoroutineScope.() -> Unit = {
        for (n in 0 until (gameState.value.players.size))
            if (!gameState.value.players[n].controllerPlayer)
                gameState.value.ai[n] = AI(gameState.value)
        while (isActive) {
            stateUpdate()
            gameState.value = gameState.value
                .copy(changed = !gameState.value.changed)
        }
    }

    fun gameStop() {
        val score1 = gameState.value.players[0].score
        val score2 = gameState.value.players[1].score
        val score3 = gameState.value.players[2].score
        val score4 = gameState.value.players[3].score

        val pl = playerRepository.getPlayers()

        viewModelScope.launch {
            pl[0].let {
                playerRepository.save(
                    it.copy(money = it.money + score1)
                )
            }
            pl[1].let {
                playerRepository.save(
                    it.copy(money = it.money + score2)
                )
            }
            pl[2].let {
                playerRepository.save(
                    it.copy(money = it.money + score3)
                )
            }
            pl[3].let {
                playerRepository.save(
                    it.copy(money = it.money + score4)
                )
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            job?.cancelAndJoin()
        }
    }

    fun clickNewGameButton() {
        gameState.value.status = "new game"
    }

    fun clickPauseGameButton() {
        gameState.value.clickPause()
    }

    fun onTouch(event: MotionEvent, left: Int, top: Int, width: Int, height: Int) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        val point = Vec(event.getX(pointerIndex).toDouble(), event.getY(pointerIndex).toDouble())

        var touchLeftSide = false
        if (point.x > left && point.x < left + width
            && point.y > top && point.y < top + height
        )
            touchLeftSide = true


        when (event.actionMasked) {
            // первое касание
            MotionEvent.ACTION_DOWN -> gameState.value.controllers[0]
                .down(touchLeftSide, point, pointerId)
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> gameState.value.controllers[0]
                .pointerDown(touchLeftSide, point, pointerId)
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> gameState.value.controllers[0].up()
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> gameState.value.controllers[0].powerUp(event)
            // движение
            MotionEvent.ACTION_MOVE -> gameState.value.controllers[0].move(event)
        }
        gameState.value = gameState.value
            .copy(changed = !gameState.value.changed)
    }

    private fun playSound(numberSound: Int) {
        soundUseCase.play(numberSound)
    }


    private fun AIUpdate() {
        for ((index, player) in gameState.value.players.withIndex()) {
            if (gameState.value.ai[index] != null && !player.main) {
                gameState.value.ai[index]?.update(index, gameState.value.controllers[index])
            }
        }
    }

    private fun stateUpdate() {
        AIUpdate()

        val now = System.currentTimeMillis()
        val deltaTime = min(now - prevTime, ((1.0 / 60.0) * 1000.0).toLong()).toInt() / 1000.0
        if (deltaTime == 0.0) return

        if (gameState.value.status != "pause") {
            var status = true
            if (ending == 1.0)
                status =
                    gameState.value.update(
                        gameState.value.controllers,
                        deltaTime
                    )

            if (!status || ending != 1.0)
                ending -= deltaTime
        }

        if (ending < 0 || gameState.value.status == "new game") {
            gameState.value.newGame()
            ending = 1.0
        }

        prevTime = now
    }
}