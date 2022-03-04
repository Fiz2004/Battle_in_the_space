package com.fiz.android.battleinthespace.game.presentation

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.data.module.asPlayer
import com.fiz.android.battleinthespace.game.data.engine.Vec
import com.fiz.android.battleinthespace.game.domain.GameScope
import com.fiz.android.battleinthespace.game.domain.StateGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GameViewModel(extras: Bundle, context: Context) :
    ViewModel() {
    private val countPlayers = extras.getInt("countPlayers")

    private val players: MutableList<Player> = MutableList(countPlayers) { Player() }

    private var stategame: StateGame? = null

    init {
        for ((index, player) in PlayerRepository(context).getPlayers()!!.withIndex())
            if (index < countPlayers)
                players[index] = player.asPlayer()
        try {
            stategame = extras.getSerializable(StateGame::class.java.simpleName) as StateGame
        } catch (e: Exception) {

        }
    }

    var gameScope: GameScope? = null
    private var job: Job? = null

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

class GameViewModelFactory(private val extras: Bundle, private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(extras, context) as T
    }
}