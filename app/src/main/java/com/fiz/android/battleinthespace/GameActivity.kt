package com.fiz.android.battleinthespace

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private var drawThread: DrawThread? = null

    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button

    private lateinit var gameSurfaceView: SurfaceView
    private lateinit var informationSurfaceView: SurfaceView

    var lastX: Float = 0F
    var lastY: Float = 0F
    var touchDown: Boolean = false
    private val sensivityX: Float = 300F
    private val sensivityY: Float = 30F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        newGameButton = findViewById(R.id.new_game_game_button)
        pauseButton = findViewById(R.id.pause_game_button)
        exitButton = findViewById(R.id.exit_game_button)

        gameSurfaceView = findViewById(R.id.game_game_surfaceview)
        informationSurfaceView = findViewById(R.id.information_game_surfaceview)

        drawThread = DrawThread(
            gameSurfaceView.holder,
            informationSurfaceView.holder,
            getSharedPreferences("data", Context.MODE_PRIVATE),
            resources,
            this.applicationContext,
            pauseButton
        )

        drawThread!!.setRunning(true)
        drawThread!!.start()

        newGameButton.setOnClickListener {
            drawThread!!.state.status = "new game"
        }
        pauseButton.setOnClickListener {
            drawThread!!.state.clickPause()
        }
        exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawThread?.controller?.get(0)?.fire = true
                drawThread?.controller?.get(0)?.up = false
                drawThread?.controller?.get(0)?.down = false
                drawThread?.controller?.get(0)?.left = false
                drawThread?.controller?.get(0)?.right = false
                touchDown = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchDown) {
                    drawThread?.controller?.get(0)?.up = false
                    drawThread?.controller?.get(0)?.down = false
                    if (event.y < lastY-sensivityY) {
                        drawThread?.controller?.get(0)?.up = true
                    }
                    if (event.y > lastY+sensivityY) {
                        drawThread?.controller?.get(0)?.down = true
                    }

                    drawThread?.controller?.get(0)?.left = false
                    drawThread?.controller?.get(0)?.right = false
                    if (event.x < lastX-sensivityX) {
                        drawThread?.controller?.get(0)?.left = true
                    }
                    if (event.x > lastX+sensivityX) {
                        drawThread?.controller?.get(0)?.right = true
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                drawThread?.controller?.get(0)?.fire = false
                drawThread?.controller?.get(0)?.up = false
                drawThread?.controller?.get(0)?.down = false
                drawThread?.controller?.get(0)?.left = false
                drawThread?.controller?.get(0)?.right = false
                lastX = event?.x
                lastY = event?.y
                touchDown = false
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        var retry = true
        drawThread!!.setRunning(false)
        while (retry) {
            try {
                drawThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }


}



