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
import javax.inject.Inject
import kotlin.math.min

private const val mSecFromFPS60 = ((1.0 / 60.0) * 1000.0).toLong()

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

        val ai = mutableListOf<AI?>()

        for (n in 0 until players.size)
            if (!players[n].controllerPlayer)
                ai.add(AI())
            else
                ai.add(null)


        val gameState = GameState(
            controllers = List(players.size) { Controller(scaledDensity = context.resources.displayMetrics.scaledDensity) },
            round = round,
            status = GameState.Companion.StatusCurrentGame.Playing,
            level = Level(20, 20, round, players, ai = ai, playSound = ::playSound),
            playSound = ::playSound,
        )
        gameState.newGame()
        gameState
    })
        private set

    private var job: Job? = null

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
        var lastTime = System.currentTimeMillis()

        while (isActive) {
            val now = System.currentTimeMillis()
            val deltaTime = min(now - lastTime, mSecFromFPS60).toInt() / 1000.0
            if (deltaTime == 0.0) continue

            gameState.value = gameState.value.update(deltaTime)
                .copy(changed = !gameState.value.changed)

            lastTime = now
        }
    }

    fun gameStop() {

        val countPlayers = playerRepository.getCountPlayers()

        val players = mutableListOf<Player>()
        val score = mutableListOf<Int>()

        for ((index, player) in playerRepository.getPlayers().withIndex())
            if (index < countPlayers) {
                players.add(player.copy(number = index))
                score.add(gameState.value.level.players[index].score)
            }

        val pl = playerRepository.getPlayers()

        viewModelScope.launch {
            for (index in playerRepository.getPlayers().indices)
                if (index < countPlayers) {
                    pl[index].let {
                        playerRepository.save(
                            it.copy(money = it.money + score[index])
                        )
                    }
                }

            viewModelScope.launch(Dispatchers.Default) {
                job?.cancelAndJoin()
            }
        }
    }

    fun clickNewGameButton() {
        gameState.value = gameState.value
            .copy(status = GameState.Companion.StatusCurrentGame.NewGame)
    }

    fun clickPauseGameButton() {
        gameState.value = gameState.value
            .copy(status = gameState.value.clickPause())

    }

    fun onTouch(event: MotionEvent, left: Int, top: Int, width: Int, height: Int) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        val point =
            Vec(event.getX(pointerIndex).toDouble(), event.getY(pointerIndex).toDouble())

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


}