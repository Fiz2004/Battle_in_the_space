package com.fiz.android.battleinthespace.game.presentation

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.database.PlayerTypeConverters
import com.fiz.android.battleinthespace.game.data.engine.Vec
import com.fiz.android.battleinthespace.game.domain.GameThread

class GameViewModel(extras: Bundle) :
    ViewModel() {
    val countPlayers = extras.getInt("countPlayers")

    val players: List<Player> = List(countPlayers) { Player() }

    init {
        for (n in 0 until countPlayers) {
            players[n].name = extras.getString("name$n").toString()
            players[n].controllerPlayer = extras.getBoolean("playerControllerPlayer$n")
            players[n].mission = extras.getInt("mission$n")
            players[n].items = PlayerTypeConverters().toItems(extras.getString("items$n"))
        }
    }


    lateinit var gameThread: GameThread

    fun gameThreadStart(context: Context, gameGameSurfaceview: SurfaceView, informationGameSurfaceview: SurfaceView) {
        gameThread = GameThread(
            players, context,
            gameGameSurfaceview,
            informationGameSurfaceview
        )

        gameThread.running = true
        gameThread.start()
    }

    fun gameThreadStop() {
        var retry = true
        gameThread.running = false
        while (retry) {
            try {
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) { /* for Lint */
            }
        }
    }

    fun clickNewGameButton() {
        gameThread.stateGame.status = "new game"
    }

    fun clickPauseGameButton() {
        gameThread.stateGame.clickPause()
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
            MotionEvent.ACTION_DOWN -> gameThread.controllers.get(0).down(touchLeftSide, point, pointerId)
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> gameThread.controllers.get(0)
                .pointerDown(touchLeftSide, point, pointerId)
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> gameThread.controllers.get(0).up()
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> gameThread.controllers.get(0).powerUp(event)
            // движение
            MotionEvent.ACTION_MOVE -> gameThread.controllers.get(0).move(event)
        }
    }

}

class GameViewModelFactory(private val extras: Bundle) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(extras) as T
    }
}