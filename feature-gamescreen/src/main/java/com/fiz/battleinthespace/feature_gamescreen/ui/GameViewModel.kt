package com.fiz.battleinthespace.feature_gamescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.common.Vec
import com.fiz.battleinthespace.common.getMaxTextWidth
import com.fiz.battleinthespace.domain.models.Controller
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import com.fiz.battleinthespace.feature_gamescreen.domain.AI
import com.fiz.battleinthespace.feature_gamescreen.domain.GetControllerState
import com.fiz.battleinthespace.feature_gamescreen.domain.GetGameStateFromGame
import com.fiz.battleinthespace.feature_gamescreen.domain.SoundUseCase
import com.fiz.feature.game.Game
import com.fiz.feature.game.engine.Physics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
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
            controllerState = getControllerState(Controller()),
            gameState = null,
        )
    )

    private lateinit var controllers: List<Controller>
    lateinit var game: Game
    private lateinit var ai: List<AI?>

    init {
        viewModelScope.launch {
            val countPlayers = settingsRepository.getCountPlayers()
            val uuid = settingsRepository.getUuid()

            val players = playerRepository.getPlayers(uuid)
                .mapIndexedNotNull { index, player ->
                    if (index < countPlayers)
                        player
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
                newGame()
            }

            viewState.value = viewState.value.copy(
                isLoading = false,
                controllerState = getControllerState(controllers[0]),
                gameState = getGameStateFromGame(game),
            )
            ifAllSurfaceReadyWhenStartGame()
        }
    }

    private var job: Job? = null

    fun loadState(game: Game?) {
        this.game = game ?: return
        viewState.value = viewState.value
            .copy(gameState = getGameStateFromGame(game))
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


    private fun ifAllSurfaceReadyWhenStartGame() {

        val isGameReadyStart =
            isGameSurfaceViewReady && isInformationSurfaceViewReady && !viewState.value.isLoading
        if (!isGameReadyStart) return

        val isGorisontal = 2 * getGameStateFromGame.infoHeight > getGameStateFromGame.infoWidth

        val minSizeInfoScreen =
            min(getGameStateFromGame.infoWidth, getGameStateFromGame.infoHeight)
        val bmpLife = if (isGorisontal) minSizeInfoScreen / 3 / 3 else minSizeInfoScreen / 5
        val baseTextSize = if (isGorisontal) minSizeInfoScreen / 6F else minSizeInfoScreen / 4F
        val textSize = baseTextSize * 0.75F
        val namesPlayers = game.players.map { it.name }
        val maxTextNameWidth = getMaxTextWidth(namesPlayers, textSize)

        getGameStateFromGame.setInfo(
            bmpLife, baseTextSize, maxTextNameWidth
        )
        startGame()
    }

    private fun startGame() {
        viewModelScope.launch(Dispatchers.Default) {
            job?.cancelAndJoin()
            job = viewModelScope.launch(Dispatchers.Default) {
                while (isActive) {

                    controllers = ai.mapIndexed { index, ai ->
                        ai?.getNewController(index, game) ?: controllers[index]
                    }

                    game.update(controllers)

                    if (game.status==Game.Companion.GlobalStatusGame.Finish){
                        viewState.value= viewState.value
                            .copy(isFinish = true)
                    }

                    game.poolQueueSound()?.let {
                        //TODO переделать для озвучки только событий в пределах экрана
                        if (Physics.overlap(
                                game.listActors.spaceShips.first().center,
                                5.0,
                                Vec(it.x, it.y),
                                5.0
                            )
                        )
                            soundUseCase.play(it)
                    }

                    viewState.value = viewState.value
                        .copy(gameState = getGameStateFromGame(game))
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
            val countPlayers = settingsRepository.getCountPlayers()
            val uuid = settingsRepository.getUuid()

            val players = playerRepository.getPlayers(uuid)
                .mapIndexed { index, player ->
                    if (index < countPlayers)
                        player.copy(
                            money = player.money + game.listActors.spaceShips[index].score
                        )
                    else
                        player
                }

            playerRepository.save(uuid, players)

        }
    }

    fun clickNewGameButton() {
        game.clickNewGame()
        viewState.value = viewState.value
            .copy(gameState = getGameStateFromGame(game))
    }

    fun clickPauseGameButton() {
        game.changeStatusPauseOrPlaying()
        viewState.value = viewState.value
            .copy(gameState = getGameStateFromGame(game))

    }

    fun firstTouchDown(pointerId: Int, point: Vec, left: Int, top: Int, width: Int, height: Int) {
        val touchMoveSide = point.x > left && point.x < left + width
                && point.y > top && point.y < top + height

        controllers.first()
            .down(touchMoveSide, point, pointerId)

        viewState.value = viewState.value
            .copy(controllerState = getControllerState(controllers.first()))
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

        controllers.first()
            .pointerDown(touchMoveSide, point, pointerId)

        viewState.value = viewState.value
            .copy(controllerState = getControllerState(controllers.first()))
    }

    fun lastTouchUp() {
        controllers.first().up()

        viewState.value = viewState.value
            .copy(controllerState = getControllerState(controllers.first()))
    }

    fun beforeTouchUp(pointIndex: Int) {
        controllers.first().powerUp(pointIndex)

        viewState.value = viewState.value
            .copy(controllerState = getControllerState(controllers.first()))
    }

    fun moveTouch(pointUp: Vec) {
        controllers.first().move(pointUp)

        viewState.value = viewState.value
            .copy(controllerState = getControllerState(controllers.first()))
    }

}