package com.fiz.android.battleinthespace

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.*
import android.view.SurfaceHolder
import kotlin.math.min

private const val TIME_UPDATE_CONTROLLER = 80
private const val widthCanvas: Int = 13
private const val heightCanvas: Int = 25

data class Controller(
    var Vystr: Boolean = false,
    var Up: Boolean = false,
    var Left: Boolean = false,
    var Right: Boolean = false,
    var PauseVystr:Int=0
)

class DrawThread(
    private val surfaceHolder: SurfaceHolder,
    private val informationSurfaceHolder: SurfaceHolder,
    private val settings: SharedPreferences,
    resources: Resources,
    context:Context
) : Thread() {
    private var prevTime = System.currentTimeMillis()
    private var deltaTime = 0
    private var ending = 1000

    private val display = Display(
        resources,
        context
    )

    var state = State(
        widthCanvas, heightCanvas, settings
    )

    val controller:Array<Controller> = Array(4){Controller()}

    private var running = false

    fun setRunning(running: Boolean) {
        this.running = running
    }

    override fun run() {
        var canvas: Canvas?
        var nextFigureCanvas: Canvas?
        while (running) {
            val now = System.currentTimeMillis()
            deltaTime += min(now - prevTime, 100).toInt()
            val tempDeltaTime = deltaTime
            canvas = null
            nextFigureCanvas = null
            try {
                canvas = surfaceHolder.lockCanvas(null)
                nextFigureCanvas = informationSurfaceHolder.lockCanvas(null)
                if (canvas == null) continue
                if (nextFigureCanvas == null) continue
                synchronized(surfaceHolder) {
                    synchronized(informationSurfaceHolder) {
                        var status = true
                        if (state.status != "pause") {
                            if (deltaTime > TIME_UPDATE_CONTROLLER) {
                                if (ending == 1000) {
                                    status = state.update(deltaTime.toFloat(), controller)
                                }
                                deltaTime = 0
                            }
                        }
                        display.render(state, canvas, nextFigureCanvas)
                        if (!status || ending != 1000) {
                            ending -= tempDeltaTime
                        }
                        if (ending < 0 || state.status == "new game") {
                            state = State(
                                widthCanvas, heightCanvas, settings
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
                    informationSurfaceHolder.unlockCanvasAndPost(nextFigureCanvas)
                }
            }
        }
    }
}