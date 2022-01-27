package com.fiz.android.battleinthespace.game

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceView
import com.fiz.android.battleinthespace.options.Options
import kotlin.math.min

class GameThread(
    private val surface: SurfaceView,
    private val informationSurface: SurfaceView,
    options: Options,
    context: Context,
) : Thread() {
    lateinit var state: com.fiz.android.battleinthespace.game.State
    val controllers: Array<Controller> = Array(options.countPlayers) { Controller(context = context) }

    init {
        createState(State(options, controllers))
    }

    var ai: Array<AI> = Array(options.countPlayers) { AI(state) }

    init {
        for (n in 0 until options.countPlayers)
            if (!options.playerControllerPlayer[n])
                ai += AI(state)
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
        for ((index, player) in state.players.withIndex()) {
            if (!player.main) {
                ai[index].update(controllers[index])
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