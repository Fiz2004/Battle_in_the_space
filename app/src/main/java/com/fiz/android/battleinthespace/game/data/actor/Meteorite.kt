package com.fiz.android.battleinthespace.game.data.actor

import android.graphics.Bitmap
import com.fiz.android.battleinthespace.game.data.engine.Vec
import com.fiz.android.battleinthespace.game.data.engine.times
import com.fiz.android.battleinthespace.game.domain.Display
import com.fiz.android.battleinthespace.game.domain.NUMBER_BITMAP_METEORITE_OPTION
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

private const val SPEED_MAX: Double = 1.0

class Meteorite(
    center: Vec,

    speed: Vec = Vec(0.0, 0.0),

    angle: Double,

    size: Double,
    inGame: Boolean = true,
    var viewSize: Int,

    var view: Int
) : MoveableActor(
    center, speed, angle, size, inGame, SPEED_MAX
) {

    fun createMeteorite(angleDestroy: Double, angleCreate: Double): Meteorite {
        val currentSpeed = speed.length()
        val currentAngleToRadians = (angleDestroy - angleCreate) / 180.0 * Math.PI
        val vec = Vec(cos(currentAngleToRadians), sin(currentAngleToRadians))

        return Meteorite(
            center + halfSize * Vec(sign(vec.x), sign(vec.y)),
            angle = angleDestroy - angleCreate,
            speed = 1.2 * currentSpeed * vec,
            size = size - 0.2,
            viewSize = viewSize + 1,
            view = view,
        )
    }

    companion object {
        fun createNew(x: Double, y: Double): Meteorite {
            val angle = (0..360).shuffled().first()
            val angleToRadians = angle / 180.0 * Math.PI
            val vec = Vec(cos(angleToRadians), sin(angleToRadians))
            return Meteorite(
                center = Vec(x, y),
                speed = 0.4 * SPEED_MAX * vec,
                angle = angle.toDouble(),
                size = 1.0,
                viewSize = 0,
                view = (0 until NUMBER_BITMAP_METEORITE_OPTION).shuffled().first()
            )
        }
    }

    override fun toString(): String {
        return "center.x= $center.x\n" +
                "center.y = $center.y\n" +
                "speed = ${speed.x},${speed.y}\n" +
                "angle = $angle\n" +
                "size = $size\n" +
                "viewSize = $viewSize\n" +
                "view = $view"
    }

    override fun getBitmap(display: Display): Bitmap {
        return display.bmpMeteorites[view][viewSize]
    }
}
