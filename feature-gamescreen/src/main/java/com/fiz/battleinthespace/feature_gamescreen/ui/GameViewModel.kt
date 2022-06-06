package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.Paint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.feature_gamescreen.domain.*
import com.fiz.battleinthespace.feature_gamescreen.game.Game
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

const val WIDTH_WORLD = 20
const val HEIGHT_WORLD = 20

private const val DIVISION_BY_SCREEN = 11


@HiltViewModel
class GameViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getGameStateFromGame: GetGameStateFromGame,
    private val soundUseCase: SoundUseCase
) : ViewModel() {

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

    private var job: Job? = null

    var getControllerState: GetControllerState =
        GetControllerState(0, 0, 0f)
        private set

    var game: Game = run {
        val ai = mutableListOf<AI?>()

        for (n in 0 until players.size)
            if (!players[n].controllerPlayer)
                ai.add(AI())
            else
                ai.add(null)

        Game(WIDTH_WORLD, HEIGHT_WORLD, 0, players, ai = ai)
    }
        private set

    init {
        game.newGame(::playSound)
    }

    var viewState = MutableStateFlow(
        ViewState(
            controllers = controllers,
            controllerState = getControllerState(controllers[0]),
            gameState = getGameStateFromGame(game),
            playSound = ::playSound,
        )
    )
        private set

    fun loadState(game: Game?) {
        this.game = game ?: return
        viewState.value = viewState.value.copy(
            gameState = getGameStateFromGame(game)
        )
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
            WIDTH_WORLD,
            HEIGHT_WORLD,
            sizeUnit
        )

        getControllerState = GetControllerState(
            leftLocationOnScreen,
            topLocationOnScreen,
            widthJoystick
        )

        isGameSurfaceViewReady = true
        ifAllSurfaceReadyWhenStartGame()
    }

    fun informationSurfaceChanged(
        infoWidth: Int,
        infoHeight: Int,
    ) {
        val minCharacteristic = min(infoWidth, infoHeight)
        val bmpLife = minCharacteristic / 3 / 3
        val baseTextSize = minCharacteristic / 6F
        val textSize = baseTextSize * 0.75F
        val maxTextNameWidth = getMaxTextWidth(game.players, textSize)

        getGameStateFromGame.setInfo(
            infoWidth, infoHeight, bmpLife, baseTextSize, maxTextNameWidth
        )

        isInformationSurfaceViewReady = true
        ifAllSurfaceReadyWhenStartGame()
    }

    private fun getMaxTextWidth(players: MutableList<Player>, textSize: Float): Int {
        var result = 0
        val paint = Paint()
        paint.textSize = textSize
        for (namePlayer in players) {
            val textWidth = paint.measureText(namePlayer.name).toInt()
            result = max(textWidth, result)
        }
        return result
    }

    private fun ifAllSurfaceReadyWhenStartGame() {
        if (isGameSurfaceViewReady && isInformationSurfaceViewReady)
            startGame()
    }

    private fun startGame() {
        viewModelScope.launch(Dispatchers.Default) {
            job?.cancelAndJoin()
            job = viewModelScope.launch(Dispatchers.Default) {
                while (isActive) {
                    game.update(controllers, ::playSound)
                    viewState.value = viewState.value
                        .copy(
                            gameState = getGameStateFromGame(game),
                        )
                }
            }
        }
    }

    fun gameStop() {
        isGameSurfaceViewReady = false
        isInformationSurfaceViewReady = false

        val countPlayers = playerRepository.getCountPlayers()

        val players = mutableListOf<Player>()
        val score = mutableListOf<Int>()

        val pl = playerRepository.getPlayers()

        for ((index, player) in pl.withIndex())
            if (index < countPlayers) {
                players.add(player.copy(number = index))
                score.add(game.players[index].score)
            }

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
        game.newGame(::playSound)
        viewState.value = viewState.value
            .copy(
                gameState = getGameStateFromGame(game)
            )
    }

    fun clickPauseGameButton() {
        game.changeStatusPauseOrPlaying()
        viewState.value = viewState.value
            .copy(gameState = getGameStateFromGame(game))

    }

    fun firstTouchDown(pointerId: Int, point: Vec, left: Int, top: Int, width: Int, height: Int) {
        val touchMoveSide = point.x > left && point.x < left + width
                && point.y > top && point.y < top + height

        controllers[0]
            .down(touchMoveSide, point, pointerId)

        viewState.value = viewState.value
            .copy(
                controllerState = getControllerState(controllers[0])
            )
    }

    fun nextTouchDown(pointerId: Int, point: Vec, left: Int, top: Int, width: Int, height: Int) {

        val touchMoveSide = point.x > left && point.x < left + width
                && point.y > top && point.y < top + height

        controllers[0]
            .pointerDown(touchMoveSide, point, pointerId)

        viewState.value = viewState.value
            .copy(
                controllerState = getControllerState(controllers[0])
            )
    }

    fun lastTouchUp() {
        controllers[0].up()

        viewState.value = viewState.value
            .copy(
                controllerState = getControllerState(controllers[0])
            )
    }

    fun beforeTouchUp(pointIndex: Int) {
        controllers[0].powerUp(pointIndex)

        viewState.value = viewState.value
            .copy(
                controllerState = getControllerState(controllers[0])
            )
    }

    fun moveTouch(pointUp: Vec) {
        controllers[0].move(pointUp)

        viewState.value = viewState.value
            .copy(
                controllerState = getControllerState(controllers[0])
            )
    }

    private fun playSound(numberSound: Int) {
        soundUseCase.play(numberSound)
    }

}