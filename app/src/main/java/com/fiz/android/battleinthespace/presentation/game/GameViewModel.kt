package com.fiz.android.battleinthespace.presentation.game

import android.content.Context
import android.media.SoundPool
import android.os.Bundle
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.data.StateProduct
import com.fiz.android.battleinthespace.engine.Vec
import com.fiz.android.battleinthespace.game.GameThread

class GameViewModel(extras: Bundle) :
    ViewModel() {
    private val _countPlayers = MutableLiveData(extras.getInt("countPlayers"))
    val countPlayers: LiveData<Int>
        get() = _countPlayers

    private var _name = MutableLiveData(MutableList<String>(4) { "" })

    init {
        for (n in 0 until 4)
            _name.value?.set(n, extras.getString("name$n").toString())
    }

    val name: MutableLiveData<MutableList<String>>
        get() = _name

    private val _playerControllerPlayer = MutableLiveData(MutableList(4) { true })

    init {
        for (n in 0 until 4)
            _playerControllerPlayer.value?.set(n, extras.getBoolean("playerControllerPlayer$n"))
    }

    val playerControllerPlayer: MutableLiveData<MutableList<Boolean>>
        get() = _playerControllerPlayer

    private val _mission = MutableLiveData(extras.getInt("mission0"))
    val mission: LiveData<Int>
        get() = _countPlayers

    private val _items = MutableLiveData(extras.getSerializable("items0") as HashMap<Int, StateProduct>)
    val items: LiveData<HashMap<Int, StateProduct>>
        get() = _items

    lateinit var gameThread: GameThread
    private lateinit var soundMap: SparseIntArray
    private lateinit var soundPool: SoundPool

    fun createSound(context: Context) {
        soundPool = SoundPool.Builder().build()

        soundMap = SparseIntArray(2)

        soundMap.put(
            0,
            soundPool.load(context, R.raw.fire, 1)
        )

        soundMap.put(
            1,
            soundPool.load(context, R.raw.collision, 1)
        )
    }

    fun gameThreadStart(context: Context, gameGameSurfaceview: SurfaceView, informationGameSurfaceview: SurfaceView) {
        gameThread = GameThread(
            this,
            gameGameSurfaceview,
            informationGameSurfaceview,
            context, soundMap, soundPool
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

    fun destroySound() {
        soundPool.release()
    }

    fun clickNewGameButton() {
        gameThread.state.status = "new game"
    }

    fun clickPauseGameButton() {
        gameThread.state.clickPause()
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