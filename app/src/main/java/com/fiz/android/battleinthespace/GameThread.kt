package com.fiz.android.battleinthespace

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.SurfaceView
import android.widget.Button
import kotlin.math.min

class GameThread(
    private val surface: SurfaceView,
    private val informationSurface: SurfaceView,
    resources: Resources,
    context: Context,
    pauseButton: Button,
    val countPlayers:Int,
    val namePlayers:List<String>
) : Thread() {
    var state = State(countPlayers,namePlayers)
    val controller: Array<Controller> = Array(4) { Controller() }

    private var prevTime = System.currentTimeMillis()
    private var deltaTime = 0
    private var ending = 1000

    private val display = Display(
        resources,
        context,
        pauseButton,
        surface
    )

    var running = false

    override fun run() {
        while (running) {
            stateUpdate()
            displayUpdate()
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
                display.render(state, canvas)
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
        deltaTime = min(now - prevTime, 100).toInt()


        if (state.status != "pause") {
            var status = true
            if (ending == 1000)
                status = state.update(controller, deltaTime)

            if (!status || ending != 1000)
                ending -= deltaTime
        }

        if (ending < 0 || state.status == "new game") {
            state = State(countPlayers,namePlayers)
            ending = 1000
        }

        deltaTime = 0
        prevTime = now
    }
}