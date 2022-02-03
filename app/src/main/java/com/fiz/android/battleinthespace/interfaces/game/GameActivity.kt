package com.fiz.android.battleinthespace.interfaces.game

import android.graphics.Rect
import android.media.SoundPool
import android.os.Bundle
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.databinding.ActivityGameBinding
import com.fiz.android.battleinthespace.engine.Vec
import com.fiz.android.battleinthespace.game.Display
import com.fiz.android.battleinthespace.game.GameThread
import com.fiz.android.battleinthespace.game.State
import com.fiz.android.battleinthespace.options.Mission
import com.fiz.android.battleinthespace.options.Records
import com.fiz.android.battleinthespace.options.Station

class GameActivity : AppCompatActivity(), Display.Companion.Listener {
    private lateinit var viewModel: GameViewModel
    private lateinit var viewModelFactory: GameViewModelFactory
    private lateinit var binding: ActivityGameBinding

    private var gameThread: GameThread? = null

    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button

    private lateinit var gameSurfaceView: SurfaceView
    private lateinit var informationSurfaceView: SurfaceView
    private var isGameSurfaceViewReady = false
    private var isInformationSurfaceViewReady = false

    private var countPlayers: Int = 4
    var name: MutableList<String> =
        MutableList(4) { i -> "" }
    var playerControllerPlayer: MutableList<Boolean> = mutableListOf(true, false, false, false)

    private lateinit var mission: Mission
    private lateinit var station: Station
    private lateinit var records: Records

    private lateinit var soundMap: SparseIntArray
    private lateinit var soundPool: SoundPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        newGameButton = findViewById(R.id.new_game_game_button)
        pauseButton = findViewById(R.id.pause_game_button)
        exitButton = findViewById(R.id.exit_game_button)

        gameSurfaceView = findViewById(R.id.game_game_surfaceview)
        informationSurfaceView = findViewById(R.id.information_game_surfaceview)

        val extras = intent.extras

        countPlayers = extras?.getInt("countPlayers") ?: 4
        name[0] = extras?.getString("name1") ?: ""
        name[1] = extras?.getString("name2") ?: ""
        name[2] = extras?.getString("name3") ?: ""
        name[3] = extras?.getString("name4") ?: ""
        playerControllerPlayer[0] = extras?.getBoolean("playerControllerPlayer1") ?: true
        playerControllerPlayer[1] = extras?.getBoolean("playerControllerPlayer2") ?: true
        playerControllerPlayer[2] = extras?.getBoolean("playerControllerPlayer3") ?: true
        playerControllerPlayer[3] = extras?.getBoolean("playerControllerPlayer4") ?: true

        mission = if (extras != null)
            extras.getSerializable(Mission::class.java.simpleName) as Mission
        else
            Mission()

        records = if (extras != null)
            extras.getSerializable(Records::class.java.simpleName) as Records
        else
            Records()

        station = if (extras != null)
            extras.getSerializable(Station::class.java.simpleName) as Station
        else {
            Station()
        }

        viewModelFactory = GameViewModelFactory(
            countPlayers,
            name,
            playerControllerPlayer,
            mission,
            station,
            records
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]

        newGameButton.setOnClickListener {
            gameThread?.state?.status = "new game"
        }
        pauseButton.setOnClickListener {
            gameThread?.state?.clickPause()
        }
        exitButton.setOnClickListener {
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

        gameSurfaceView.holder.addCallback(GameSurfaceView())
        informationSurfaceView.holder.addCallback(InformationSurfaceView())
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
        if (point.x > gameSurfaceView.left && point.x < gameSurfaceView.left + gameSurfaceView.width
                && point.y > gameSurfaceView.top && point.y < gameSurfaceView.top + gameSurfaceView.height
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
        outState.putSerializable(State::class.java.simpleName, gameThread?.state)
        super.onSaveInstanceState(outState)
    }

    override fun pauseButtonClick(status: String) {
        if (status == "pause")
            pauseButton.post { pauseButton.text = resources.getString(R.string.resume_game_button) }
        else
            pauseButton.post { pauseButton.text = resources.getString(R.string.pause_game_button) }
    }

    inner class GameSurfaceView : SurfaceHolder.Callback {
        override fun surfaceCreated(p0: SurfaceHolder) {
            isGameSurfaceViewReady = true
            gameThreadStart()
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {

        }
    }

    inner class InformationSurfaceView : SurfaceHolder.Callback {
        override fun surfaceCreated(p0: SurfaceHolder) {
            isInformationSurfaceViewReady = true
            gameThreadStart()
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {

        }
    }

    fun gameThreadStart() {
        if (isGameSurfaceViewReady && !isInformationSurfaceViewReady) {
            gameThread = GameThread(
                viewModel.countPlayers.value ?: 4,
                viewModel.name,
                viewModel.playerControllerPlayer,
                gameSurfaceView,
                informationSurfaceView,
                this, soundMap, soundPool
            )

            gameThread?.running = true
            gameThread?.start()
        }
    }
}




