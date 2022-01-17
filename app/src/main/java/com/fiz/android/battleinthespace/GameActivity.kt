package com.fiz.android.battleinthespace

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
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

    private val sensivity: Vec = Vec(0.0, 0.0)

    class Side {
        var point: Vec = Vec(0.0, 0.0)
        var touch: Boolean = false
        var ID: Int = 0
    }

    private val leftSide = Side()
    private val rightSide = Side()


    class Options {
        var countPlayers = 4
        var namePlayer: MutableList<String> =
            mutableListOf("Player 1", "Player 2", "Player 3", "Player 4")
    }

    val options = Options()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            this.applicationContext,
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false

        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        var point = Vec(event.getX(pointerIndex).toDouble(), event.getY(pointerIndex).toDouble())

        val widthJoystick = 50F * resources.displayMetrics.scaledDensity

        var touchLeftSide = false
        if (point.x < resources.displayMetrics.widthPixels / 2)
            touchLeftSide = true


        when (event.actionMasked) {
            // первое касание
            MotionEvent.ACTION_DOWN -> {
                if (touchLeftSide) {
                    leftSide.point = point.copy()
                    leftSide.touch = true
                    leftSide.ID = pointerId
                } else {
                    gameThread?.controller?.get(0)?.fire = true
                    rightSide.touch = true
                    rightSide.ID = pointerId
                }
            }
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!leftSide.touch && touchLeftSide) {
                    leftSide.point = point.copy()
                    leftSide.touch = true
                    leftSide.ID = pointerId
                }
                if (!rightSide.touch && !touchLeftSide) {
                    gameThread?.controller?.get(0)?.fire = true
                    rightSide.touch = true
                    rightSide.ID = pointerId
                }
            }
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> {
                leftSide.touch = false
                gameThread?.controller?.get(0)?.power = 0F

                rightSide.touch = false
                gameThread?.controller?.get(0)?.fire = false
            }
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> {
                val isLeftSide = event.findPointerIndex(pointerIndex) == leftSide.ID
                val isRightSide = event.findPointerIndex(pointerIndex) == rightSide.ID
                if (isLeftSide) {
                    leftSide.touch = false
                    gameThread?.controller?.get(0)?.power = 0F
                }
                if (isRightSide) {
                    rightSide.touch = false
                    gameThread?.controller?.get(0)?.fire = false
                }

            }
            // движение
            MotionEvent.ACTION_MOVE -> {
                if (leftSide.touch) {
                    point = Vec(
                        event.getX(event.findPointerIndex(leftSide.ID)).toDouble(),
                        event.getY(event.findPointerIndex(leftSide.ID)).toDouble())

                    val delta = Vec(
                        if (abs(point.x - leftSide.point.x) > sensivity.x) point.x - leftSide.point.x else 0.0,
                        if (abs(point.y - leftSide.point.y) > sensivity.y) point.y - leftSide.point.y else 0.0)

                    var power = delta.lengthSqrt() / widthJoystick
                    power = if (power > 1) 1.0 else power
                    gameThread?.controller?.get(0)?.power = power.toFloat()

                    val angle = atan2(delta.y, delta.x) * 180 / Math.PI
                    gameThread?.controller?.get(0)?.angle = angle.toFloat()
                }
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




