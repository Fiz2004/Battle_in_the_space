package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.NUMBER_BITMAP_METEORITE_OPTION
import com.fiz.android.battleinthespace.engine.Vec
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

private const val SPEED_MAX: Double = 1.0

class Meteorite(
    center: Vec,

    speedX: Double = 0.0,
    speedY: Double = 0.0,

    angle: Double,

    size: Double,

    var viewSize: Int,

    var view: Int
) : Actor(
    center, speedX, speedY, angle, size, SPEED_MAX
) {

    fun createMeteorite(angleDestroy: Double, angleCreate: Double): Meteorite {
        val speed =
            sqrt(speedX * speedX + speedY * speedY)

        return Meteorite(
            Vec(
                center.x + (size / 2) * sign(
                    cos((angleDestroy - angleCreate) / 180.0 * Math.PI)
                ),
                center.y + (size / 2) * sign(
                    sin((angleDestroy - angleCreate) / 180.0 * Math.PI)
                )),
            angle = angleDestroy - angleCreate,
            speedX = 1.2 * speed * cos((angleDestroy - angleCreate) / 180.0 * Math.PI),
            speedY = 1.2 * speed * sin((angleDestroy - angleCreate) / 180.0 * Math.PI),
            size = size - 0.2,
            viewSize = viewSize + 1,
            view = view,
        )
    }

    companion object {
        fun createNew(x: Double, y: Double): Meteorite {
            val angle = (0..360).shuffled().first()
            return Meteorite(
                center= Vec(x,y),
                speedX = 0.4 * SPEED_MAX * cos(angle / 180.0 * Math.PI),
                speedY = 0.4 * SPEED_MAX * sin(angle / 180.0 * Math.PI),
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
                "speedX = $speedX\n" +
                "speedY = $speedY\n" +
                "angle = $angle\n" +
                "size = $size\n" +
                "viewSize = $viewSize\n" +
                "view = $view"
    }
}