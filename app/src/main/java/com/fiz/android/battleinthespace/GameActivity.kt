package com.fiz.android.battleinthespace

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.Window
import android.widget.Button
import com.fiz.android.battleinthespace.engine.Vec

class GameActivity : Activity(), Display.Companion.Listener {
    private var gameThread: GameThread? = null

    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button

    private lateinit var gameSurfaceView: SurfaceView
    private lateinit var informationSurfaceView: SurfaceView

    private lateinit var options: Options

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

        options = if (extras != null)
            extras.getSerializable(Options::class.java.simpleName) as Options
        else
            Options(applicationContext)

        gameThread = GameThread(
            gameSurfaceView,
            informationSurfaceView,
            options,
            this
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
            pauseButton.post { pauseButton.text = resources.getString(R.string.resume_game_button) }
        else
            pauseButton.post { pauseButton.text = resources.getString(R.string.pause_game_button) }
    }

}




