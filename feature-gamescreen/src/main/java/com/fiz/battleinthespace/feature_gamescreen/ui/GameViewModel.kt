package com.fiz.battleinthespace.feature_gamescreen.ui

import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.BitmapRepository
import com.fiz.battleinthespace.feature_gamescreen.domain.AI
import com.fiz.battleinthespace.feature_gamescreen.domain.Controller
import com.fiz.battleinthespace.feature_gamescreen.domain.SoundUseCase
import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.min

private const val mSecFromFPS60 = ((1.0 / 60.0) * 1000.0).toLong()

val widthWorld = 20
val heightWorld = 20

private const val DIVISION_BY_SCREEN = 11


@HiltViewModel
class GameViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val bitmapRepository: BitmapRepository,
    private val soundUseCase: SoundUseCase
) : ViewModel() {

    var getGameStateFromGame: GetGameStateFromGame =
        GetGameStateFromGame(0, 0, 0, 0, 0f, bitmapRepository)

    var getControllerState: GetControllerState =
        GetControllerState(0, 0, 0f)

    private var isGameSurfaceViewReady = false
    private var isInformationSurfaceViewReady = false

    private val countPlayers = playerRepository.getCountPlayers()

    private val players = run {

        val result = mutableListOf<Player>()

        for ((index, player) in playerRepository.getPlayers().withIndex())
            if (index < countPlayers)
                result.add(player.copy(number = index))
        result
    }

    private val controllers: List<Controller> =
        List(players.size) { Controller() }


    private val game: Game = run {
        val ai = mutableListOf<AI?>()

        for (n in 0 until players.size)
            if (!players[n].controllerPlayer)
                ai.add(AI())
            else
                ai.add(null)

        Game(widthWorld, heightWorld, 0, players, ai = ai, playSound = ::playSound)
    }

    var viewState = MutableStateFlow(
        ViewState(
            controllers = controllers,
            controllerState = getControllerState(controllers[0]),
            gameState = getGameStateFromGame(game),
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
            job = null
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
                .copy(
                    gameState = getGameStateFromGame(game),
                    changed = !viewState.value.changed
                )

            lastTime = now
        }
    }

    fun gameStop() {
        isGameSurfaceViewReady = false
        isInformationSurfaceViewReady = false

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
                job = null
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
                controllerState = getControllerState(controllers[0]),
                changed = !viewState.value.changed
            )
    }

    private fun playSound(numberSound: Int) {
        soundUseCase.play(numberSound)
    }

    fun gameSurfaceChanged(
        surfaceWidth: Int,
        surfaceHeight: Int,
        leftLocationOnScreen: Int,
        topLocationOnScreen: Int,
        widthJoystick: Float
    ) {

        val sizeUnit: Float = min(surfaceWidth, surfaceHeight).toFloat() / DIVISION_BY_SCREEN

        getGameStateFromGame.setViewport(
            surfaceWidth,
            surfaceHeight,
            widthWorld,
            heightWorld,
            sizeUnit
        )

        getControllerState = GetControllerState(
            leftLocationOnScreen,
            topLocationOnScreen,
            widthJoystick
        )

        isGameSurfaceViewReady = true
        ifCanStartGameWhenStartGame()
    }

    fun informationSurfaceChanged() {
        isInformationSurfaceViewReady = true
        ifCanStartGameWhenStartGame()
    }

    private fun ifCanStartGameWhenStartGame() {
        if (isGameSurfaceViewReady && isInformationSurfaceViewReady)
            startGame()
    }

}