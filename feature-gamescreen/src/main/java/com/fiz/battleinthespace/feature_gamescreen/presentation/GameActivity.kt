package com.fiz.battleinthespace.feature_gamescreen.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fiz.battleinthespace.core.App
import com.fiz.battleinthespace.feature_gamescreen.R
import com.fiz.battleinthespace.feature_gamescreen.databinding.ActivityGameBinding
import com.fiz.battleinthespace.feature_gamescreen.domain.Display
import com.fiz.battleinthespace.feature_gamescreen.domain.StateGame

class GameActivity : AppCompatActivity(), Display.Companion.Listener {
    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory((application as App).playerRepository, extras)
    }

    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private var isGameSurfaceViewReady = false
    private var isInformationSurfaceViewReady = false

    private lateinit var extras: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        extras = intent.extras ?: return

        binding.newGameGameButton.setOnClickListener {
            viewModel.clickNewGameButton()
        }
        binding.pauseGameButton.setOnClickListener {
            viewModel.clickPauseGameButton()
        }
        binding.exitGameButton.setOnClickListener {
            finishActivity()
        }

        binding.gameGameSurfaceview.holder.addCallback(GameSurfaceView())
        binding.informationGameSurfaceview.holder.addCallback(InformationSurfaceView())

        viewModel.pl.observe(this) {
            viewModel.initpl(it)
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

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing)
            if (!viewModel.gameScope?.running!!) {
                finishActivity()
            }
    }

    private fun finishActivity() {
        val data = Intent()
        val score = viewModel.gameScope?.stateGame?.playerGames?.get(0)?.score
        data.putExtra("score", score)
        setResult(RESULT_OK, data)
        viewModel.gameThreadStop()
        finish()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(StateGame::class.java.simpleName, viewModel.gameScope?.stateGame)
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
        if (isGameSurfaceViewReady && isInformationSurfaceViewReady)
            viewModel.gameThreadStart(
                this,
                binding.gameGameSurfaceview,
                binding.informationGameSurfaceview
            )

    }
}




