package com.fiz.battleinthespace.feature_gamescreen.ui

import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fiz.battleinthespace.feature_gamescreen.R
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.BitmapRepository
import com.fiz.battleinthespace.feature_gamescreen.databinding.ActivityGameBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    private val viewModel: GameViewModel by viewModels()

    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private var isGameSurfaceViewReady = false
    private var isInformationSurfaceViewReady = false

    lateinit var display: Display

    @Inject
    lateinit var bitmapRepository: BitmapRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val loadStateGame =
            savedInstanceState?.getSerializable(GameState::class.java.simpleName) as? GameState
        viewModel.loadState(loadStateGame)

        binding.gameGameSurfaceview.holder.addCallback(GameSurfaceView())
        binding.informationGameSurfaceview.holder.addCallback(InformationSurfaceView())

        binding.newGameGameButton.setOnClickListener {
            viewModel.clickNewGameButton()
        }

        binding.pauseGameButton.setOnClickListener {
            viewModel.clickPauseGameButton()
        }

        binding.exitGameButton.setOnClickListener {
            finish()
        }

        var prevTime = System.currentTimeMillis()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.gameState.collectLatest { gameState ->

                        val now = System.currentTimeMillis()
                        val fps = (1000 / (now - prevTime)).toInt()

                        binding.gameGameSurfaceview.holder.lockCanvas()?.let {
                            display.render(
                                gameState,
                                it,
                                gameState.controllers[0]
                            )
                            binding.gameGameSurfaceview.holder.unlockCanvasAndPost(it)
                        }

                        binding.informationGameSurfaceview.holder.lockCanvas()?.let {
                            display.renderInfo(gameState, it, fps)
                            binding.informationGameSurfaceview.holder.unlockCanvasAndPost(it)
                        }

                        binding.pauseGameButton.text =
                            if (gameState.status == GameState.Companion.StatusCurrentGame.Pause)
                                resources.getString(R.string.resume_game_button)
                            else
                                resources.getString(R.string.pause_game_button)

                        prevTime = now

                    }
                }

            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        viewModel.onTouch(
            event,
            binding.gameGameSurfaceview.left,
            binding.gameGameSurfaceview.top,
            binding.gameGameSurfaceview.width,
            binding.gameGameSurfaceview.height
        )
        return super.onTouchEvent(event)
    }

    override fun onStop() {
        super.onStop()
        isGameSurfaceViewReady = false
        isInformationSurfaceViewReady = false
        viewModel.gameStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(
            GameState::class.java.simpleName,
            viewModel.gameState.value
        )
        super.onSaveInstanceState(outState)
    }

    inner class GameSurfaceView : SurfaceHolder.Callback {
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            isGameSurfaceViewReady = true


            val a = IntArray(2)
            binding.gameGameSurfaceview.getLocationOnScreen(a)
            val left = a[0]
            val top = a[1]

            display = Display(
                binding.gameGameSurfaceview.width,
                binding.gameGameSurfaceview.height,
                viewModel.gameState.value,
                bitmapRepository,
                left,
                top
            )
            startGame()
        }

        override fun surfaceCreated(p0: SurfaceHolder) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) {}
    }

    inner class InformationSurfaceView : SurfaceHolder.Callback {
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            isInformationSurfaceViewReady = true
            startGame()
        }

        override fun surfaceCreated(p0: SurfaceHolder) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) {}
    }

    fun startGame() {
        if (isGameSurfaceViewReady && isInformationSurfaceViewReady)
            viewModel.startGame()

    }
}




