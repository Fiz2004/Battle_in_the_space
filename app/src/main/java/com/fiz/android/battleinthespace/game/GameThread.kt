package com.fiz.android.battleinthespace.game

import android.content.Context
import android.graphics.Canvas
import android.media.SoundPool
import android.util.SparseIntArray
import android.view.SurfaceView
import com.fiz.android.battleinthespace.interfaces.game.GameViewModel
import kotlin.math.min

class GameThread(
    viewModel: GameViewModel,
    private val surface: SurfaceView,
    private val informationSurface: SurfaceView,
    context: Context,
    val soundMap: SparseIntArray,
    val soundPool: SoundPool,
) : Thread() {
    lateinit var state: com.fiz.android.battleinthespace.game.State
    val controllers: Array<Controller> = Array(viewModel.countPlayers.value ?: 4) { Controller(context = context) }

    init {
        createState(
            State(
                viewModel.countPlayers.value ?: 4,
                viewModel.name.value ?: mutableListOf(),
                controllers,
                soundMap,
                soundPool))
    }

    private var ai: Array<AI?> = Array<AI?>(4) { null }

    init {
        for (n in 0 until (viewModel.countPlayers.value ?: 4))
            if (viewModel.playerControllerPlayer.value?.get(n) == false)
                ai[n] = AI(state)
    }

    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0

    private val display = Display(
        surface,
        context
    )

    var running = false
    var pause = false

    override fun run() {
        while (running) {
            if (!pause) {
                AIUpdate()
                stateUpdate()
                displayUpdate()
            }
        }
    }

    private fun AIUpdate() {
        for ((index, player) in state.playerGames.withIndex()) {
            if (ai[index] != null && !player.main) {
                ai[index]?.update(controllers[index])
            }
        }
    }

    private fun displayUpdate(
    ) {
        var canvas: Canvas? = null
        var informationCanvas: Canvas? = null
        try {
            canvas = surface.holder.lockCanvas()
            if (canvas == null) return
            synchronized(surface.holder) {
                display.render(state, controllers[0], canvas)
            }

            informationCanvas = informationSurface.holder.lockCanvas()
            if (informationCanvas == null) return
            synchronized(informationSurface.holder) {
                display.renderInfo(state, informationCanvas)
            }
        } finally {
            if (canvas != null)
                surface.holder.unlockCanvasAndPost(canvas)
            if (informationCanvas != null)
                informationSurface.holder.unlockCanvasAndPost(informationCanvas)

        }
    }

    private fun stateUpdate() {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - prevTime, 100).toInt() / 1000.0

        if (state.status != "pause") {
            var status = true
            if (ending == 1.0)
                status = state.update(controllers, deltaTime)

            if (!status || ending != 1.0)
                ending -= deltaTime
        }

        if (ending < 0 || state.status == "new game") {
            state.newGame()
            ending = 1.0
        }

        prevTime = now
    }

    fun createState(state: com.fiz.android.battleinthespace.game.State) {
        this.state = state
    }
}