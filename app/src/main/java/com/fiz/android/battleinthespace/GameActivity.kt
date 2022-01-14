package com.fiz.android.battleinthespace

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Button
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class GameActivity : Activity() {
    private var gameThread: GameThread? = null

    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button

    private lateinit var gameSurfaceView: SurfaceView
    private lateinit var informationSurfaceView: SurfaceView

    private val sensivityX: Float = 0F
    private val sensivityY: Float = 0F
    private val leftSide = object {
        var x: Float = 0F
        var y: Float = 0F
        var touch: Boolean = false
        var ID: Int = 0
    }
    private val rightSide = object {
        var x: Float = 0F
        var y: Float = 0F
        var touch: Boolean = false
        var ID: Int = 0
    }

    class Options {
        var countPlayers = 4
        var namePlayer: MutableList<String> = mutableListOf("Player 1", "Player 2", "Player 3", "Player 4")
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
            options.namePlayer[n]=extras?.getString("namePlayer${n+1}")?:"Player ${n+1}"


        gameThread = GameThread(
            gameSurfaceView.holder,
            informationSurfaceView.holder,
            resources,
            this.applicationContext,
            pauseButton,
            options.countPlayers,
            options.namePlayer.toList()
        )

        gameThread!!.setRunning(true)
        gameThread!!.start()

        newGameButton.setOnClickListener {
            gameThread!!.state.status = "new game"
        }
        pauseButton.setOnClickListener {
            gameThread!!.state.clickPause()
        }
        exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // событие
        val actionMask: Int = event?.actionMasked ?: 0
        // индекс касания
        val pointerIndex = event?.actionIndex ?: 0
        // ID касания
        val pointerId = event?.getPointerId(pointerIndex)
        // число касаний
        // val pointerCount: Int = event?.pointerCount ?: 0

        var x = event?.getX(pointerIndex) ?: 0F
        var y = event?.getY(pointerIndex) ?: 0F

        val widthJoystick = 50F * resources.displayMetrics.scaledDensity

        var touchLeftSide = false
        if (x < resources.displayMetrics.widthPixels / 2)
            touchLeftSide = true


        when (actionMask) {
            // первое касание
            MotionEvent.ACTION_DOWN -> {
                if (touchLeftSide) {
                    leftSide.x = x
                    leftSide.y = y
                    leftSide.touch = true
                    leftSide.ID = pointerId ?: 0
                } else {
                    gameThread?.controller?.get(0)?.fire = true
                    rightSide.touch = true
                    rightSide.ID = pointerId ?: 0
                }
            }
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!leftSide.touch && touchLeftSide) {
                    leftSide.x = x
                    leftSide.y = y
                    leftSide.touch = true
                    leftSide.ID = pointerId ?: 0
                }
                if (!rightSide.touch && !touchLeftSide) {
                    gameThread?.controller?.get(0)?.fire = true
                    rightSide.touch = true
                    rightSide.ID = pointerId ?: 0
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
                val isLeftSide = event?.findPointerIndex(pointerIndex) == leftSide.ID
                val isRightSide = event?.findPointerIndex(pointerIndex) == rightSide.ID
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
            //TODO Найти касание которое слева и с ним работать
            MotionEvent.ACTION_MOVE -> {
                val isLeftSide = event?.findPointerIndex(pointerIndex) == leftSide.ID
                val isRightSide = event?.findPointerIndex(pointerIndex) == rightSide.ID
                if (leftSide.touch && isLeftSide) {
                    x = event?.getX(event.findPointerIndex(leftSide.ID)) ?: 0F
                    y = event?.getY(event.findPointerIndex(leftSide.ID)) ?: 0F
                    val deltaX = if (abs(x - leftSide.x) < sensivityX) 0F else x - leftSide.x
                    val deltaY = if (abs(y - leftSide.y) < sensivityY) 0F else y - leftSide.y
                    var power = sqrt(deltaX * deltaX + deltaY * deltaY) / widthJoystick
                    power = if (power > 1) 1F else power
                    gameThread?.controller?.get(0)?.power = power

                    val angle = atan2(deltaY.toDouble(), deltaX.toDouble()) * 180 / Math.PI
                    gameThread?.controller?.get(0)?.angle = angle.toFloat()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        var retry = true
        gameThread!!.setRunning(false)
        while (retry) {
            try {
                gameThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }


}




