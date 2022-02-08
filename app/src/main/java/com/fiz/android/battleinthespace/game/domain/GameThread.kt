package com.fiz.android.battleinthespace.game.domain

import android.content.Context
import android.graphics.Canvas
import android.media.SoundPool
import android.util.SparseIntArray
import android.view.SurfaceView
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.game.data.actor.ListActors
import kotlin.math.min


class GameThread(
    players: List<Player>,
    context: Context,
    private val surface: SurfaceView,
    private val informationSurface: SurfaceView,
) : Thread(), ListActors.CallBacks {
    val controllers: Array<Controller> = Array(players.size) { Controller(context = context) }

    var stateGame: StateGame = StateGame(
        players.size,
        players.map { it.name } as MutableList<String>,
        controllers)

    init {
        stateGame.setCallBacks(this)
    }


    private var soundPool: SoundPool = SoundPool.Builder().build()
    private var soundMap: SparseIntArray = SparseIntArray(2)

    init {
        soundMap.put(
            0,
            soundPool.load(context, R.raw.fire, 1)
        )
        soundMap.put(
            1,
            soundPool.load(context, R.raw.collision, 1)
        )
    }


    private var ai: Array<AI?> = Array(4) { null }

    init {
        for (n in 0 until (players.size))
            if (!players[n].controllerPlayer)
                ai[n] = AI(stateGame)
    }

    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0

    private val display = Display(surface, context)

    var running = false
    var pause = false

    override fun playSound(numberSound: Int) {
        soundPool.play(soundMap.get(numberSound), 1F, 1F, 1, 0, 1F)
    }

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
        for ((index, player) in stateGame.playerGames.withIndex()) {
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
                display.render(stateGame, controllers[0], canvas)
            }

            informationCanvas = informationSurface.holder.lockCanvas()
            if (informationCanvas == null) return
            synchronized(informationSurface.holder) {
                display.renderInfo(stateGame, informationCanvas)
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

        if (stateGame.status != "pause") {
            var status = true
            if (ending == 1.0)
                status = stateGame.update(controllers, deltaTime)

            if (!status || ending != 1.0)
                ending -= deltaTime
        }

        if (ending < 0 || stateGame.status == "new game") {
            stateGame.newGame()
            ending = 1.0
        }

        prevTime = now
    }
}