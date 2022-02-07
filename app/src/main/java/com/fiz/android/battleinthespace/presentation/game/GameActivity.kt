package com.fiz.android.battleinthespace.presentation.game

import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.databinding.ActivityGameBinding
import com.fiz.android.battleinthespace.game.Display
import com.fiz.android.battleinthespace.game.State

class GameActivity : AppCompatActivity(), Display.Companion.Listener {
    private lateinit var viewModel: GameViewModel
    private lateinit var viewModelFactory: GameViewModelFactory

    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    var start = false

    private var isGameSurfaceViewReady = false
    private var isInformationSurfaceViewReady = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val extras = intent.extras ?: return

        viewModelFactory = GameViewModelFactory(extras)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]

        binding.newGameGameButton.setOnClickListener {
            viewModel.clickNewGameButton()
        }
        binding.pauseGameButton.setOnClickListener {
            viewModel.clickPauseGameButton()
        }
        binding.exitGameButton.setOnClickListener {
            finish()
        }

        viewModel.createSound(this)

        binding.gameGameSurfaceview.holder.addCallback(GameSurfaceView())
        binding.informationGameSurfaceview.holder.addCallback(InformationSurfaceView())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        viewModel.onTouch(
            event,
            binding.gameGameSurfaceview.left,
            binding.gameGameSurfaceview.top,
            binding.gameGameSurfaceview.width,
            binding.gameGameSurfaceview.height)
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroySound()

        if (start)
            viewModel.gameThreadStop()
    }



    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(State::class.java.simpleName, viewModel.gameThread.state)
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
            viewModel.gameThreadStart(this, binding.gameGameSurfaceview, binding.informationGameSurfaceview)
            start = true
        }
    }
}




