package com.fiz.battleinthespace.feature_gamescreen.ui

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.database.ItemsDatabase
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.feature_gamescreen.data.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.domain.GameScope
import com.fiz.battleinthespace.feature_gamescreen.domain.StateGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val playerRepository: PlayerRepository) :
    ViewModel() {
    private val countPlayers = playerRepository.getCountPlayers()

    private val players: MutableList<Player> =
        MutableList(countPlayers) { Player(items = ItemsDatabase.getStartItems()) }

    private var stategame: StateGame? = null

    val pl = playerRepository.getPlayers()

    fun initpl(play: List<Player>?) {
        for ((index, player) in play?.withIndex()!!)
            if (index < countPlayers)
                players[index] = player
    }

    var gameScope: GameScope? = null
    private var job: Job? = null

    fun loadState(extras: Bundle?) {
        try {
            stategame = extras?.getSerializable(StateGame::class.java.simpleName) as StateGame
        } catch (e: Exception) {

        }
    }

    fun gameThreadStart(
        context: Context,
        gameSurfaceview: SurfaceView,
        informationGameSurfaceview: SurfaceView
    ) {

        job = CoroutineScope(Dispatchers.Default).launch {
            if (gameScope == null) {
                gameScope = GameScope(
                    players, context,
                    gameSurfaceview,
                    informationGameSurfaceview
                )
                gameScope?.running = true
                gameScope?.run()
            } else {
                gameScope?.surface = gameSurfaceview
                gameScope?.informationSurface = informationGameSurfaceview
                gameScope?.display?.surface = gameSurfaceview
                gameScope?.display?.viewPortUpdate()
                gameScope?.running = true
            }

            if (stategame != null)
                gameScope?.stateGame = stategame!!
        }
    }

    fun gameThreadStop() {
        val score1 = gameScope?.stateGame?.playerGames?.get(0)?.score ?: 0
        val score2 = gameScope?.stateGame?.playerGames?.get(1)?.score ?: 0
        val score3 = gameScope?.stateGame?.playerGames?.get(2)?.score ?: 0
        val score4 = gameScope?.stateGame?.playerGames?.get(3)?.score ?: 0

        viewModelScope.launch {
            pl.value?.get(0)?.let {
                playerRepository.save(
                    it.copy(money = it.money + score1)
                )
            }
            pl.value?.get(1)?.let {
                playerRepository.save(
                    it.copy(money = it.money + score2)
                )
            }
            pl.value?.get(2)?.let {
                playerRepository.save(
                    it.copy(money = it.money + score3)
                )
            }
            pl.value?.get(3)?.let {
                playerRepository.save(
                    it.copy(money = it.money + score4)
                )
            }
        }
        var retry = true
        gameScope?.running = false
        while (retry) {
            try {
                viewModelScope.launch {
                    job?.join()
                }
                retry = false
            } catch (e: InterruptedException) { /* for Lint */
            }
        }
    }

    fun clickNewGameButton() {
        gameScope?.stateGame?.status = "new game"
    }

    fun clickPauseGameButton() {
        gameScope?.stateGame?.clickPause()
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
            MotionEvent.ACTION_DOWN -> gameScope?.controllers?.get(0)
                ?.down(touchLeftSide, point, pointerId)
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> gameScope?.controllers?.get(0)
                ?.pointerDown(touchLeftSide, point, pointerId)
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> gameScope?.controllers?.get(0)?.up()
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> gameScope?.controllers?.get(0)?.powerUp(event)
            // движение
            MotionEvent.ACTION_MOVE -> gameScope?.controllers?.get(0)?.move(event)
        }
    }
}