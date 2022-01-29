package com.fiz.android.battleinthespace.interfaces

import android.app.Activity
import android.graphics.Rect
import android.media.SoundPool
import android.os.Bundle
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.databinding.ActivityGameBinding
import com.fiz.android.battleinthespace.engine.Vec
import com.fiz.android.battleinthespace.game.Display
import com.fiz.android.battleinthespace.game.GameThread
import com.fiz.android.battleinthespace.game.State
import com.fiz.android.battleinthespace.options.Options

class GameActivity : Activity(), Display.Companion.Listener, SurfaceHolder.Callback {
    private var gameThread: GameThread? = null

    private lateinit var options: Options
    private lateinit var soundMap: SparseIntArray
    private lateinit var soundPool: SoundPool

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras

        options = if (extras != null)
            extras.getSerializable(Options::class.java.simpleName) as Options
        else
            Options(applicationContext)

        binding.newGameGameButton.setOnClickListener {
            gameThread?.state?.status = "new game"
        }
        binding.pauseGameButton.setOnClickListener {
            gameThread?.state?.clickPause()
        }
        binding.exitGameButton.setOnClickListener {
            finish()
        }

        soundPool = SoundPool.Builder().build()

        soundMap = SparseIntArray(2)

        soundMap.put(
            0,
            soundPool.load(applicationContext, R.raw.fire, 1)
        )

        soundMap.put(
            1,
            soundPool.load(applicationContext, R.raw.collision, 1)
        )


        binding.gameGameSurfaceview.holder.addCallback(this)

    }

    override fun onResume() {
        super.onResume()
        if (gameThread?.pause == true)
            gameThread?.pause = false
    }

    override fun onPause() {
        super.onPause()
        gameThread?.pause = true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false

        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        val point = Vec(event.getX(pointerIndex).toDouble(), event.getY(pointerIndex).toDouble())

        var touchLeftSide = false
        if (point.x > binding.gameGameSurfaceview.left && point.x < binding.gameGameSurfaceview.left + binding.gameGameSurfaceview.width
            && point.y > binding.gameGameSurfaceview.top && point.y < binding.gameGameSurfaceview.top + binding.gameGameSurfaceview.height
        )
            touchLeftSide = true

        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)

        when (event.actionMasked) {
            // первое касание
            MotionEvent.ACTION_DOWN -> gameThread?.controllers?.get(0)?.down(touchLeftSide, point, pointerId)
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> gameThread?.controllers?.get(0)
                ?.pointerDown(touchLeftSide, point, pointerId)
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> gameThread?.controllers?.get(0)?.up()
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> gameThread?.controllers?.get(0)?.powerUp(event)
            // движение
            MotionEvent.ACTION_MOVE -> gameThread?.controllers?.get(0)?.move(event)
        }

        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        gameThreadStop()
    }

    private fun gameThreadStop() {
        var retry = true
        gameThread?.running = false
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) { /* for Lint */
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Options::class.java.simpleName, options)
        outState.putSerializable(State::class.java.simpleName, gameThread?.state)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        options = savedInstanceState.getSerializable(Options::class.java.simpleName) as Options
        gameThread?.createState(savedInstanceState.getSerializable(State::class.java.simpleName) as State)
    }

    override fun pauseButtonClick(status: String) {
        if (status == "pause")
            binding.pauseGameButton.post {
                binding.pauseGameButton.text = resources.getString(R.string.resume_game_button)
            }
        else
            binding.pauseGameButton.post {
                binding.pauseGameButton.text = resources.getString(R.string.pause_game_button)
            }
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        gameThread = GameThread(
            binding.gameGameSurfaceview,
            binding.informationGameSurfaceview,
            options,
            this, soundMap, soundPool
        )

        gameThread?.running = true
        gameThread?.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }

}




