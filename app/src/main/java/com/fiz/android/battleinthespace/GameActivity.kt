package com.fiz.android.battleinthespace

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.Window
import android.widget.Button
import com.fiz.android.battleinthespace.engine.Vec
import kotlin.math.abs
import kotlin.math.atan2

class GameActivity : Activity() {
    private var gameThread: GameThread? = null

    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button

    private lateinit var gameSurfaceView: SurfaceView
    private lateinit var informationSurfaceView: SurfaceView

    class Options {
        var countPlayers = 4
        var namePlayer: MutableList<String> =
            mutableListOf("Player 1", "Player 2", "Player 3", "Player 4")
    }

    val options = Options()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_game)

        newGameButton = findViewById(R.id.new_game_game_button)
        pauseButton = findViewById(R.id.pause_game_button)
        exitButton = findViewById(R.id.exit_game_button)

        gameSurfaceView = findViewById(R.id.game_game_surfaceview)
        informationSurfaceView = findViewById(R.id.information_game_surfaceview)

        val extras = intent.extras

        if (extras != null)
            options.countPlayers = extras.getInt("countPlayers")

        for (n in 0 until options.countPlayers)
            options.namePlayer[n] = extras?.getString("namePlayer${n + 1}") ?: "Player ${n + 1}"

        gameThread = GameThread(
            gameSurfaceView,
            informationSurfaceView,
            resources,
            applicationContext,
            pauseButton,
            options.countPlayers,
            options.namePlayer.toList(),
        )

        gameThread?.running = true
        gameThread?.start()

        newGameButton.setOnClickListener {
            gameThread?.state?.status = "new game"
        }
        pauseButton.setOnClickListener {
            gameThread?.state?.clickPause()
        }
        exitButton.setOnClickListener {
            finish()
        }
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
        if (point.x < gameSurfaceView.width)
            touchLeftSide = true

        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)

        when (event.actionMasked) {
            // первое касание
            MotionEvent.ACTION_DOWN -> {
                gameThread?.controller?.get(0)?.ACTION_DOWN(touchLeftSide, point, pointerId)
            }
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> {
                gameThread?.controller?.get(0)?.ACTION_POINTER_DOWN(touchLeftSide, point, pointerId)
            }
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> {
                gameThread?.controller?.get(0)?.ACTION_UP()
            }
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> {
                gameThread?.controller?.get(0)?.ACTION_POINTER_UP(event)
            }
            // движение
            MotionEvent.ACTION_MOVE -> {
                gameThread?.controller?.get(0)?.ACTION_MOVE(event)
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameThreadStop()
    }

    fun gameThreadStop() {
        var retry = true
        gameThread?.running = false
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }

}




