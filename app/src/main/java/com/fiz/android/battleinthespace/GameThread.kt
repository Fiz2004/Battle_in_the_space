package com.fiz.android.battleinthespace

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceView
import kotlin.math.min

class GameThread(
    private val surface: SurfaceView,
    private val informationSurface: SurfaceView,
    private val options: Options,
    context: Context,
) : Thread() {
    var state = State(options)
    val controller: Array<Controller> = Array(4) { Controller(context = context) }

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
                stateUpdate()
                displayUpdate()
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
                display.render(state, controller[0], canvas)
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
                status = state.update(controller, deltaTime)

            if (!status || ending != 1.0)
                ending -= deltaTime
        }

        if (ending < 0 || state.status == "new game") {
            state = State(options)
            ending = 1.0
        }

        prevTime = now
    }
}