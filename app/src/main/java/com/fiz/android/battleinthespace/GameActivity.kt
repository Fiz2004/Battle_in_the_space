package com.fiz.android.battleinthespace

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Button
import kotlin.math.abs
import kotlin.math.sqrt

class GameActivity : Activity() {
    private var gameThread: GameThread? = null

    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button

    private lateinit var gameSurfaceView: SurfaceView
    private lateinit var informationSurfaceView: SurfaceView

    var lastX: Float = 0F
    var lastY: Float = 0F
    var touchDown: Boolean = false
    private val sensivityX: Float = 0F
    private val sensivityY: Float = 0F

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
        val x = event?.x ?: 0F
        val y = event?.y ?: 0F
        val widthJoystick = 50F * resources.displayMetrics.scaledDensity

        val type: String
        if (x < resources.displayMetrics.widthPixels / 2)
            type = "left"
        else
            type = "right"

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (type == "right")
                    gameThread?.controller?.get(0)?.fire = true
                gameThread?.controller?.get(0)?.up = false
                gameThread?.controller?.get(0)?.down = false
                gameThread?.controller?.get(0)?.left = false
                gameThread?.controller?.get(0)?.right = false
                if (type == "left") {
                    lastX = x
                    lastY = y
                    touchDown = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchDown) {
                    gameThread?.controller?.get(0)?.left = false
                    gameThread?.controller?.get(0)?.right = false
                    if (x - lastX < -sensivityX) {
                        gameThread?.controller?.get(0)?.left = true
                    }
                    if (x - lastX > +sensivityX) {
                        gameThread?.controller?.get(0)?.right = true
                    }
                    val deltaX = if (abs(x - lastX) < sensivityX) 0F else x - lastX
                    gameThread?.controller?.get(0)?.deltaX = deltaX
                    Log.d ("onTouchEvent","dx=${deltaX}")

                    gameThread?.controller?.get(0)?.up = false
                    gameThread?.controller?.get(0)?.down = false
                    if (y - lastY < -sensivityY) {
                        gameThread?.controller?.get(0)?.up = true
                    }
                    if (y - lastY > +sensivityY) {
                        gameThread?.controller?.get(0)?.down = true
                    }
                    val deltaY = if (abs(y - lastY) < sensivityY) 0F else y - lastY
                    gameThread?.controller?.get(0)?.deltaY = deltaY
                    Log.d ("onTouchEvent","dy=${deltaY}")

                    var power = sqrt(deltaX * deltaX + deltaY * deltaY)/widthJoystick
                    power =if (power>1) 1F else power
                    gameThread?.controller?.get(0)?.power = power
                    Log.d ("onTouchEvent","power=${power}")

                    val angle=Math.atan2(deltaY.toDouble(),deltaX.toDouble())*180/Math.PI
                    gameThread?.controller?.get(0)?.angle = angle.toFloat()
                    Log.d ("onTouchEvent","angle=${gameThread?.controller?.get(0)?.angle}")
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (type == "right")
                    gameThread?.controller?.get(0)?.fire = false
                gameThread?.controller?.get(0)?.up = false
                gameThread?.controller?.get(0)?.down = false
                gameThread?.controller?.get(0)?.left = false
                gameThread?.controller?.get(0)?.right = false
                if (type == "left") {
                    touchDown = false
                    gameThread?.controller?.get(0)?.power=0F
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



