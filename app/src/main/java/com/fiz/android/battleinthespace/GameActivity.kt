package com.fiz.android.battleinthespace

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Button
import kotlin.math.abs
import kotlin.math.sqrt
import android.widget.TextView

import android.view.View.OnTouchListener
import java.lang.StringBuilder

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
    }
    private val rightSide = object {
        var x: Float = 0F
        var y: Float = 0F
        var touch: Boolean = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        newGameButton = findViewById(R.id.new_game_game_button)
        pauseButton = findViewById(R.id.pause_game_button)
        exitButton = findViewById(R.id.exit_game_button)

        gameSurfaceView = findViewById(R.id.game_game_surfaceview)
        informationSurfaceView = findViewById(R.id.information_game_surfaceview)

        gameThread = GameThread(
            gameSurfaceView.holder,
            informationSurfaceView.holder,
            resources,
            this.applicationContext,
            pauseButton
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
        val pointerIndex = event?.actionIndex?: 0
        // ID касания
        val pointerId = event?.getPointerId(pointerIndex)
        // число касаний
        val pointerCount: Int = event?.pointerCount ?: 0

        val x = event?.getX(pointerIndex) ?: 0F
        val y = event?.getY(pointerIndex) ?: 0F

        val widthJoystick = 50F * resources.displayMetrics.scaledDensity

        var touchLeftSide: Boolean = false
        if (x < resources.displayMetrics.widthPixels / 2)
            touchLeftSide = true


        when (actionMask) {
            // первое касание
            MotionEvent.ACTION_DOWN -> {
                if (touchLeftSide) {
                    leftSide.x = x
                    leftSide.y = y
                    leftSide.touch = true
                } else {
                    gameThread?.controller?.get(0)?.fire = true
                    rightSide.touch = true
                }
            }
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (touchLeftSide) {
                    leftSide.x = x
                    leftSide.y = y
                    leftSide.touch = true
                } else {
                    gameThread?.controller?.get(0)?.fire = true
                    rightSide.touch = true
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
                var isLeftSide=false
                var isRightSide=false
                for (i in 0 until pointerCount) {
                    val currentX: Float = event?.getX(i) ?: 0F
                    val currentY: Float = event?.getY(i) ?: 0F
                    if (x==currentX && y==currentY) continue
                    if (currentX < resources.displayMetrics.widthPixels / 2)
                        isLeftSide=true
                    else
                        isRightSide=true
                }
                if (isLeftSide==false) {
                    leftSide.touch = false
                    gameThread?.controller?.get(0)?.power = 0F
                }
                if (isRightSide==false) {
                    rightSide.touch = false
                    gameThread?.controller?.get(0)?.fire = false
                }

            }
            // движение
            //TODO Найти касание которое слева и с ним работать
            MotionEvent.ACTION_MOVE -> {
                if (leftSide.touch) {
                    val deltaX = if (abs(x - leftSide.x) < sensivityX) 0F else x - leftSide.x
                    val deltaY = if (abs(y - leftSide.y) < sensivityY) 0F else y - leftSide.y
                    var power = sqrt(deltaX * deltaX + deltaY * deltaY) / widthJoystick
                    power = if (power > 1) 1F else power
                    gameThread?.controller?.get(0)?.power = power

                    val angle = Math.atan2(deltaY.toDouble(), deltaX.toDouble()) * 180 / Math.PI
                    gameThread?.controller?.get(0)?.angle = angle.toFloat()
                }
            }
        }

        //Для отладки на компьютере
//        gameThread?.controller?.get(0)?.fire=true

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




