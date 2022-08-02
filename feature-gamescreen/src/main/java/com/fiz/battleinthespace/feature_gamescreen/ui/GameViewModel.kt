package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.Paint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.common.Vec
import com.fiz.battleinthespace.domain.models.Controller
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import com.fiz.battleinthespace.feature_gamescreen.domain.AI
import com.fiz.battleinthespace.feature_gamescreen.domain.GetControllerState
import com.fiz.battleinthespace.feature_gamescreen.domain.GetGameStateFromGame
import com.fiz.battleinthespace.feature_gamescreen.domain.SoundUseCase
import com.fiz.feature.game.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

const val WIDTH_WORLD = 20
const val HEIGHT_WORLD = 20

private const val DIVISION_BY_SCREEN = 11


@HiltViewModel
class GameViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val getGameStateFromGame: GetGameStateFromGame,
    private val soundUseCase: SoundUseCase
) : ViewModel() {

    private var isGameSurfaceViewReady = false

    private var isInformationSurfaceViewReady = false

    var getControllerState: GetControllerState =
        GetControllerState(0, 0, 0.0)
        private set

    var viewState = MutableStateFlow(
        ViewState(
            isLoading = true,
            controllers = MutableList(4) { Controller() },
            controllerState = getControllerState(Controller()),
            gameState = null,
            playSound = ::playSound,
        )
    )

    private lateinit var players: List<Player>
    private lateinit var controllers: MutableList<Controller>
    lateinit var game: Game
    private lateinit var ai: List<AI?>

    init {
        viewModelScope.launch {
            val countPlayers = settingsRepository.getCountPlayers()

            val uuid = settingsRepository.getUuid()
            players = playerRepository.getPlayers(uuid)
                .mapIndexedNotNull { index, player ->
                    if (index < countPlayers)
                        player.copy(number = index)
                    else
                        null
                }

            controllers = MutableList(players.size) { Controller() }

            ai = players.map {
                if (it.controllerPlayer)
                    null
                else
                    AI()
            }

            game = run {
                Game(WIDTH_WORLD, HEIGHT_WORLD, 0, players)
            }.apply {
                newGame(::playSound)
            }

            viewState.value = viewState.value.copy(
                isLoading = false,
                controllers = controllers,
                controllerState = getControllerState(controllers[0]),
                gameState = getGameStateFromGame(game),
                playSound = ::playSound,
            )
            ifAllSurfaceReadyWhenStartGame()
        }
    }


    private var job: Job? = null

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
        widthJoystick: Double
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
        getGameStateFromGame.setInfoScreen(
            infoWidth, infoHeight
        )

        isInformationSurfaceViewReady = true
        ifAllSurfaceReadyWhenStartGame()
    }

    private fun getMaxTextWidth(players: List<Player>, textSize: Float): Int {
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
        if (isGameSurfaceViewReady && isInformationSurfaceViewReady && !viewState.value.isLoading) {
            val minCharacteristic =
                min(getGameStateFromGame.infoWidth, getGameStateFromGame.infoHeight)
            val bmpLife = minCharacteristic / 3 / 3
            val baseTextSize = minCharacteristic / 6F
            val textSize = baseTextSize * 0.75F
            val maxTextNameWidth = getMaxTextWidth(game.players, textSize)

            getGameStateFromGame.setInfo(
                bmpLife, baseTextSize, maxTextNameWidth
            )
            startGame()
        }
    }

    private fun startGame() {
        viewModelScope.launch(Dispatchers.Default) {
            job?.cancelAndJoin()
            job = viewModelScope.launch(Dispatchers.Default) {
                while (isActive) {

                    for ((index, player) in players.withIndex()) {
                        if (ai[index] != null && !player.main) {
                            controllers[index] = ai[index]?.getNewController(
                                index,
                                game
                            ) ?: controllers[index]
                        }
                    }

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

        viewModelScope.launch {
            job?.cancelAndJoin()
            job = null
        }
        viewModelScope.launch {
            val countPlayers = settingsRepository.getFlowCountPlayers().first()

            val players = mutableListOf<Player>()
            val score = mutableListOf<Int>()

            val uuid = settingsRepository.getFlowUuid().first()

            val pl = playerRepository.getPlayers(uuid)
                .toMutableList()

            for ((index, player) in pl.withIndex())
                if (index < countPlayers) {
                    players.add(player.copy(number = index))
                    score.add(game.players[index].score)
                }


            for (index in playerRepository.getPlayers(uuid).indices)
                if (index < countPlayers) {
                    pl[index] = pl[index].let {
                        it.copy(money = it.money + score[index])
                    }
                }
            playerRepository.save(uuid, pl)

        }
    }

    fun clickNewGameButton() {
        game.clickNewGame()
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

    fun nextTouchDown(
        pointerId: Int,
        point: Vec,
        left: Int,
        top: Int,
        width: Int,
        height: Int
    ) {

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