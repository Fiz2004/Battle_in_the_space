package com.fiz.battleinthespace.feature_gamescreen.ui

import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.fiz.battleinthespace.common.MeasureFPS
import com.fiz.battleinthespace.common.Vec
import com.fiz.battleinthespace.common.launchAndRepeatWithViewLifecycle
import com.fiz.battleinthespace.common.serializable
import com.fiz.battleinthespace.domain.models.WIDTH_JOYSTICK_DEFAULT
import com.fiz.battleinthespace.feature_gamescreen.databinding.ActivityGameBinding
import com.fiz.feature.game.Game
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()

    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var display: Display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init(savedInstanceState)
        bindListeners()
        collectFlows()
    }

    private fun init(savedInstanceState: Bundle?) {
        val loadGame =
            savedInstanceState?.serializable(Game::class.java.simpleName, Game::class.java)
        viewModel.loadState(loadGame)
    }

    private fun bindListeners() {
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
    }

    private fun collectFlows() {
        val measureFPS = MeasureFPS()
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collectLatest { viewState ->
                measureFPS {
                    updateScreenState(viewState)
                }
            }
        }
    }

    private fun updateScreenState(viewState: ViewState) {
        if (viewState.isFinish) finish()

        binding.progressBar.isVisible = viewState.isLoading

        binding.gameGameSurfaceview.holder.lockCanvas()?.let {
            display.render(viewState, it)
            binding.gameGameSurfaceview.holder.unlockCanvasAndPost(it)
        }

        binding.informationGameSurfaceview.holder.lockCanvas()?.let {
            display.renderInfo(viewState, it)
            binding.informationGameSurfaceview.holder.unlockCanvasAndPost(it)
        }

        binding.pauseGameButton.text =
            getString(viewState.getResourceTextForPauseResumeButton())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false

        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        val point =
            Vec(
                event.getX(pointerIndex).toDouble(),
                event.getY(pointerIndex).toDouble()
            )

        val pointIndex = (event.findPointerIndex(pointerId))

        val pointUp = Vec(
            event.getX(event.findPointerIndex(pointerId)).toDouble(),
            event.getY(event.findPointerIndex(pointerId)).toDouble()
        )

        when (event.actionMasked) {
            // первое касание
            MotionEvent.ACTION_DOWN -> viewModel.firstTouchDown(
                pointerId,
                point,
                binding.gameGameSurfaceview.left,
                binding.gameGameSurfaceview.top,
                binding.gameGameSurfaceview.width,
                binding.gameGameSurfaceview.height
            )
            // последующие касания
            MotionEvent.ACTION_POINTER_DOWN -> viewModel.nextTouchDown(
                pointerId,
                point,
                binding.gameGameSurfaceview.left,
                binding.gameGameSurfaceview.top,
                binding.gameGameSurfaceview.width,
                binding.gameGameSurfaceview.height
            )
            // прерывание последнего касания
            MotionEvent.ACTION_UP -> viewModel.lastTouchUp()
            // прерывания касаний
            MotionEvent.ACTION_POINTER_UP -> viewModel.beforeTouchUp(pointIndex)
            // движение
            MotionEvent.ACTION_MOVE -> viewModel.moveTouch(pointUp)
        }

        return super.onTouchEvent(event)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Game::class.java.simpleName, viewModel.game)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        viewModel.gameStop()
    }

    inner class GameSurfaceView : SurfaceHolder.Callback {
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            val a = IntArray(2)
            binding.gameGameSurfaceview.getLocationOnScreen(a)
            val leftLocationOnScreen = a[0]
            val topLocationOnScreen = a[1]

            val widthJoystick = WIDTH_JOYSTICK_DEFAULT * resources.displayMetrics.scaledDensity

            viewModel.gameSurfaceChanged(
                binding.gameGameSurfaceview.width,
                binding.gameGameSurfaceview.height,
                leftLocationOnScreen,
                topLocationOnScreen,
                widthJoystick
            )
        }

        override fun surfaceCreated(p0: SurfaceHolder) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) {}
    }

    inner class InformationSurfaceView : SurfaceHolder.Callback {
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            viewModel.informationSurfaceChanged(
                binding.informationGameSurfaceview.width,
                binding.informationGameSurfaceview.height
            )
        }

        override fun surfaceCreated(p0: SurfaceHolder) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) {}
    }
}




