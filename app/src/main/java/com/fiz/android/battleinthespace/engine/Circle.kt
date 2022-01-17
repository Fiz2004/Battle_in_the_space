package com.fiz.android.battleinthespace.engine

import kotlin.math.cos
import kotlin.math.sin

class Circle(r: Double) : Shape() {

    override var radius: Double = r

    override fun Clone(): Circle {
        return Circle(radius)
    }

    override fun Initialize() {
        ComputeMass(1.0)
    }

    override fun ComputeMass(density: Double) {
        body.m = Math.PI * radius * radius * density
        body.im =  1.0 / body.m
        body.I = body.m * radius * radius
        body.iI =  1.0 / body.I
    }

    override fun SetOrient(radians: Double) {
    }

    override fun Draw() {
        var k_segments: Int = 20

        // Визуализировать круг с кучей линий
//        glColor3f(body.r, body.g, body.b)
//        glBegin(GL_LINE_LOOP)
        var theta: Double = body.orient
        var inc: Double = Math.PI * 2.0 / k_segments.toDouble()
        for (i in 0 until k_segments) {
            theta += inc;
            var p:Vec2 =Vec2(cos(theta), sin(theta))
            p *= radius
            p += body.position
//            glVertex2f(p.x, p.y)
        }
//        glEnd()

        // Визуализировать линию внутри круга, чтобы была видна ориентация
//        glBegin(GL_LINE_STRIP)
        var r:Vec2=Vec2 (0.0, 1.0)
        var c:Double = cos (body.orient)
        var s:Double = sin (body.orient)
        r.Set(r.x * c - r.y * s, r.x * s + r.y * c)
        r *= radius
        r = r + body.position
//        glVertex2f(body.position.x, body.position.y)
//        glVertex2f(r.x, r.y)
//        glEnd()
    }

    override fun GetType(): Type {
        return Type.eCircle
    }
}