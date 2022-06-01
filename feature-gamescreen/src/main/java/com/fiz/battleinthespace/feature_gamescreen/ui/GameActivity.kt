package com.fiz.battleinthespace.feature_gamescreen.ui

import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.BitmapRepository
import com.fiz.battleinthespace.feature_gamescreen.databinding.ActivityGameBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    private val viewModel: GameViewModel by viewModels()

    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    lateinit var display: Display

    @Inject
    lateinit var bitmapRepository: BitmapRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val loadStateGame =
                savedInstanceState?.getSerializable(ViewState::class.java.simpleName) as? ViewState
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

        var lastTime = System.currentTimeMillis()
        var fps = 60

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                withContext(Dispatchers.Main) {
                    viewModel.viewState.collectLatest { viewState ->

                        val now = System.currentTimeMillis()
                        val deltaTime = now - lastTime
                        fps = ((fps + (1000 / deltaTime)) / 2).toInt()

                        binding.gameGameSurfaceview.holder.lockCanvas()?.let {
                            display.render(
                                    viewState,
                                    it,
                                    viewState.controllers[0]
                            )
                            binding.gameGameSurfaceview.holder.unlockCanvasAndPost(it)
                        }

                        binding.informationGameSurfaceview.holder.lockCanvas()?.let {
                            display.renderInfo(viewState, it, fps)
                            binding.informationGameSurfaceview.holder.unlockCanvasAndPost(it)
                        }

                        binding.pauseGameButton.text = getString(viewState.getResourceTextForPauseResumeButton())

                        lastTime = now

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
        viewModel.gameStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(
                ViewState::class.java.simpleName,
                viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    inner class GameSurfaceView : SurfaceHolder.Callback {
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            val a = IntArray(2)
            binding.gameGameSurfaceview.getLocationOnScreen(a)
            val left = a[0]
            val top = a[1]

            display = Display(
                    binding.gameGameSurfaceview.width,
                    binding.gameGameSurfaceview.height,
                    viewModel.viewState.value,
                    bitmapRepository,
                    left,
                    top
            )

            viewModel.setDisplay(display)

            viewModel.gameSurfaceChanged()
        }

        override fun surfaceCreated(p0: SurfaceHolder) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) {}
    }

    inner class InformationSurfaceView : SurfaceHolder.Callback {
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            viewModel.informationSurfaceChanged()
        }

        override fun surfaceCreated(p0: SurfaceHolder) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) {}
    }
}




