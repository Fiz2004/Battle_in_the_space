package com.fiz.android.battleinthespace.interfaces.game

import android.graphics.Rect
import android.media.SoundPool
import android.os.Bundle
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.databinding.ActivityGameBinding
import com.fiz.android.battleinthespace.engine.Vec
import com.fiz.android.battleinthespace.game.Display
import com.fiz.android.battleinthespace.game.GameThread
import com.fiz.android.battleinthespace.game.State
import com.fiz.android.battleinthespace.options.Records
import com.fiz.android.battleinthespace.options.StateProduct

class GameActivity : AppCompatActivity(), Display.Companion.Listener {
    private lateinit var viewModel: GameViewModel
    private lateinit var viewModelFactory: GameViewModelFactory

    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private lateinit var gameThread: GameThread
    var start = false

    private var isGameSurfaceViewReady = false
    private var isInformationSurfaceViewReady = false

    private var countPlayers: Int = 4
    var name: MutableList<String> =
        MutableList(4) { i -> "" }
    var playerControllerPlayer: MutableList<Boolean> = mutableListOf(true, false, false, false)

    private var mission = 0
    var items: HashMap<Int, StateProduct> = hashMapOf()
    private lateinit var records: Records

    private lateinit var soundMap: SparseIntArray
    private lateinit var soundPool: SoundPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val extras = intent.extras

        countPlayers = extras?.getInt("countPlayers") ?: 4

        for (n in 0 until 4) {
            name[n] = extras?.getString("name$n") ?: ""
            playerControllerPlayer[n] = extras?.getBoolean("playerControllerPlayer$n") ?: true
        }
        mission = extras?.getInt("mission0") ?: 0
        items = (extras?.getSerializable("items0") ?: hashMapOf<Int, StateProduct>()) as HashMap<Int, StateProduct>

        records = if (extras != null)
            extras.getSerializable(Records::class.java.simpleName) as Records
        else
            Records()

        viewModelFactory = GameViewModelFactory(
            countPlayers,
            name,
            playerControllerPlayer,
            mission,
            records
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]

        binding.newGameGameButton.setOnClickListener {
            gameThread.state.status = "new game"
        }
        binding.pauseGameButton.setOnClickListener {
            gameThread.state.clickPause()
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

        binding.gameGameSurfaceview.holder.addCallback(GameSurfaceView())
        binding.informationGameSurfaceview.holder.addCallback(InformationSurfaceView())
    }

    override fun onResume() {
        super.onResume()
        if (start)
            if (gameThread.pause == true)
                gameThread.pause = false
    }

    override fun onPause() {
        super.onPause()
        if (start)
            gameThread.pause = true
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
            MotionEvent.ACTION_DOWN -> gameThread.controllers.get(0).down(touchLeftSide, point, pointerId)
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> gameThread.controllers.get(0)
                .pointerDown(touchLeftSide, point, pointerId)
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> gameThread.controllers.get(0).up()
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> gameThread.controllers.get(0).powerUp(event)
            // движение
            MotionEvent.ACTION_MOVE -> gameThread.controllers.get(0).move(event)
        }

        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        if (start)
            gameThreadStop()
    }

    private fun gameThreadStop() {
        var retry = true
        gameThread.running = false
        while (retry) {
            try {
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) { /* for Lint */
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(State::class.java.simpleName, gameThread.state)
        super.onSaveInstanceState(outState)
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
        if (isGameSurfaceViewReady && isInformationSurfaceViewReady) {
            gameThread = GameThread(
                viewModel.countPlayers.value ?: 4,
                viewModel.name,
                viewModel.playerControllerPlayer,
                binding.gameGameSurfaceview,
                binding.informationGameSurfaceview,
                this, soundMap, soundPool
            )

            gameThread.running = true
            gameThread.start()
            start = true
        }
    }
}




