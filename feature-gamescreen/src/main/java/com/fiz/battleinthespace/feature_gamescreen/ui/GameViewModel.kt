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
import com.fiz.battleinthespace.feature_gamescreen.domain.Game
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
    private val countPlayers = playerRepository.getCountPlayers()

    private val players = run {

        val result = mutableListOf<Player>()

        for ((index, player) in playerRepository.getPlayers().withIndex())
            if (index < countPlayers)
                result.add(player.copy(number = index))
        result
    }

    private val controllers: List<Controller> =
        List(players.size) { Controller(scaledDensity = context.resources.displayMetrics.scaledDensity) }


    private val game: Game = run {
        val ai = mutableListOf<AI?>()

        for (n in 0 until players.size)
            if (!players[n].controllerPlayer)
                ai.add(AI())
            else
                ai.add(null)

        Game(20, 20, 0, players, ai = ai, playSound = ::playSound)
    }

    var viewState = MutableStateFlow(
        ViewState(
            controllers = controllers,
            gameState = game.getState(),
            status = ViewState.Companion.StatusCurrentGame.Playing,
            playSound = ::playSound,
        )
    )
        private set

    private var job: Job? = null

    fun loadState(viewState: ViewState?) {
        this.viewState.value = viewState ?: return
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

            game.update(viewState.value.controllers, deltaTime)

            viewState.value = viewState.value.update(deltaTime, game)
                .copy(changed = !viewState.value.changed)

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
                score.add(game.players[index].score)
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
        viewState.value = viewState.value
            .copy(status = ViewState.Companion.StatusCurrentGame.NewGame)
    }

    fun clickPauseGameButton() {
        viewState.value = viewState.value
            .copy(status = viewState.value.clickPause())

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
            MotionEvent.ACTION_DOWN -> controllers[0]
                .down(touchLeftSide, point, pointerId)
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> controllers[0]
                .pointerDown(touchLeftSide, point, pointerId)
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> controllers[0].up()
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> controllers[0].powerUp(event)
            // движение
            MotionEvent.ACTION_MOVE -> controllers[0].move(event)
        }
        viewState.value = viewState.value
            .copy(
                changed = !viewState.value.changed
            )
    }

    private fun playSound(numberSound: Int) {
        soundUseCase.play(numberSound)
    }


}