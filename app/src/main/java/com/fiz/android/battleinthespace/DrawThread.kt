package com.fiz.android.battleinthespace

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.*
import android.view.SurfaceHolder
import android.widget.Button
import kotlin.math.min

private const val TIME_UPDATE_CONTROLLER = 80
private const val widthCanvas: Int = 13
private const val heightCanvas: Int = 25

data class Controller(
    var fire: Boolean = false,
    var up: Boolean = false,
    var left: Boolean = false,
    var right: Boolean = false,
    var down: Boolean = false,
    var timeLastFire: Int = 0
)

class DrawThread(
    private val surfaceHolder: SurfaceHolder,
    private val informationSurfaceHolder: SurfaceHolder,
    private val settings: SharedPreferences,
    resources: Resources,
    context: Context,
    pauseButton: Button
) : Thread() {
    private var prevTime = System.currentTimeMillis()
    private var deltaTime = 0
    private var ending = 1000

    var state = State(
        width = 16.0, height = 16.0, settings
    )

    private val display = Display(
        resources,
        context,
        pauseButton
    )

    val controller: Array<Controller> = Array(4) { Controller() }

    private var running = false

    fun setRunning(running: Boolean) {
        this.running = running
    }

    override fun run() {
        var canvas: Canvas?
        var informationCanvas: Canvas?
        while (running) {
            val now = System.currentTimeMillis()
            deltaTime += min(now - prevTime, 100).toInt()
            val tempDeltaTime = deltaTime
            canvas = null
            informationCanvas = null
            try {
                canvas = surfaceHolder.lockCanvas(null)
                informationCanvas = informationSurfaceHolder.lockCanvas(null)
                if (canvas == null) continue
                if (informationCanvas == null) continue
                synchronized(surfaceHolder) {
                    synchronized(informationSurfaceHolder) {
                        var status = true
                        if (state.status != "pause") {
                            if (ending == 1000) {
                                status = state.update(controller,deltaTime)
                            }
                            deltaTime = 0
                        }
                        display.render(state, canvas, informationCanvas)
                        if (!status || ending != 1000) {
                            ending -= tempDeltaTime
                        }
                        if (ending < 0 || state.status == "new game") {
                            state = State(
                                width = 16.0, height = 16.0, settings
                            )
                            ending = 1000
                            deltaTime = 0
                        }
                        prevTime = now
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                    informationSurfaceHolder.unlockCanvasAndPost(informationCanvas)
                }
            }
        }
    }
}