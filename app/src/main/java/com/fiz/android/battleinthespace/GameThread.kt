package com.fiz.android.battleinthespace

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.*
import android.view.SurfaceHolder
import android.widget.Button
import kotlin.math.min

class Controller(
    var fire: Boolean = false,
    var timeLastFire: Int = 0,
    _angle:Float=0F,
    var power:Float=0F
){
    var angle:Float=_angle
    set(value) {
        field = value
        if (value > 360)
            field = value - 360
        if (value < 0)
            field = value + 360
    }
}

class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val informationSurfaceHolder: SurfaceHolder,
    resources: Resources,
    context: Context,
    pauseButton: Button
) : Thread() {
    var state = State(16.0, 16.0)
    val controller: Array<Controller> = Array(4) { Controller() }

    private var prevTime = System.currentTimeMillis()
    private var deltaTime = 0
    private var ending = 1000

    private val display = Display(
        resources,
        context,
        pauseButton
    )

    private var running = false

    fun setRunning(running: Boolean) {
        this.running = running
    }

    override fun run() {
        while (running) {
            stateUpdate()
            displayUpdate()
        }
    }

    private fun displayUpdate(
    ) {
        var canvas: Canvas?= null
        var informationCanvas: Canvas?= null
        try {
            canvas = surfaceHolder.lockCanvas()
            if (canvas == null) return
            synchronized(surfaceHolder) {
                display.render(state, canvas)
            }

            informationCanvas = informationSurfaceHolder.lockCanvas()
            if (informationCanvas == null) return
            synchronized(informationSurfaceHolder) {
                display.renderInfo(state, informationCanvas)
            }
        } finally {
            if (canvas!=null)
                surfaceHolder.unlockCanvasAndPost(canvas)
            if (informationCanvas!=null)
                informationSurfaceHolder.unlockCanvasAndPost(informationCanvas)

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
            state = State(16.0, 16.0)
            ending = 1000
        }

        deltaTime = 0
        prevTime = now
    }
}