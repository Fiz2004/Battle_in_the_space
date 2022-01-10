package com.fiz.android.battleinthespace

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private var drawThread: DrawThread? = null

    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button

    private lateinit var gameSurfaceView: SurfaceView
    private lateinit var informationSurfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        newGameButton=findViewById(R.id.new_game_game_button)
        pauseButton=findViewById(R.id.pause_game_button)
        exitButton=findViewById(R.id.exit_game_button)

        gameSurfaceView=findViewById(R.id.game_game_surfaceview)
        informationSurfaceView=findViewById(R.id.information_game_surfaceview)

        drawThread = DrawThread(gameSurfaceView.holder,
            informationSurfaceView.holder,
            getSharedPreferences("data", Context.MODE_PRIVATE),
            resources,
            this.applicationContext)

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
                drawThread!!.controller[0].Vystr = true
                drawThread!!.controller[0].Up = false
                drawThread!!.controller[0].Left = false
                drawThread!!.controller[0].Right = false
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                drawThread!!.controller[0].Vystr = false
                drawThread!!.controller[0].Up = false
                drawThread!!.controller[0].Left = false
                drawThread!!.controller[0].Right = false
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



