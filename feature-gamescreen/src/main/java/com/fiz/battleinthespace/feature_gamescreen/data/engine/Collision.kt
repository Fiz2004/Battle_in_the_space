package com.fiz.battleinthespace.feature_gamescreen.data.engine

import com.fiz.battleinthespace.feature_gamescreen.data.actor.MoveableActor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Collision(private var actor1: MoveableActor, private var actor2: MoveableActor) {
    // Вычислить вектор поступательного движения, который является нормальным
    private val normalVec: Vec = actor2.center - actor1.center
    private val distSqr: Double = normalVec.sumPow2()
    private val radius: Double = actor1.halfSize + actor2.halfSize
    private val distance: Double = sqrt(distSqr)

    // Глубина проникновения от столкновения
    private var penetration: Double = radius - distance

    // Из A в B
    private var normal: Vec = normalVec / distance

    // Точки соприкосновения во время столкновения
    private var contact: Vec = normal * actor1.halfSize + actor1.center

    // Смешанная реституция
    // Рассчитать среднюю реституцию
    private var e: Double = min(actor1.restitution, actor2.restitution)

    // Решите импульс и примените
    fun applyImpulse() {
        // Рассчитать радиус от центра тяжести до контакта
        val ra: Vec = contact - actor1.center
        val rb: Vec = contact - actor2.center

        // Относительная скорость
        val rv: Vec = actor2.speed + cross(actor2.angleSpeed, rb) - actor1.speed
        -cross(actor1.angleSpeed, ra)

        //Относительная скорость по нормали
        val contactVel: Double = dot(rv, normal)

        // Не разрешайте, если скорости разделяются
        if (contactVel > 0)
            return

        val raCrossN: Double = cross(ra, normal)
        val rbCrossN: Double = cross(rb, normal)
        val invMassSum: Double =
            actor1.inverseWeight + actor2.inverseWeight + sqr(raCrossN) * actor1.inverseMomentInertia + sqr(
                rbCrossN) * actor2.inverseMomentInertia

        // Вычислить скалярный импульс
        var j: Double = -(1.0f + e) * contactVel
        j /= invMassSum

        // Применить импульс
        val impulse: Vec = normal * j
        actor1.applyImpulse(-impulse, ra)
        actor2.applyImpulse(impulse, rb)
    }

    // Коррекция позиционного проникновения
    fun positionalCorrection() {
        //Надбавка за проникновение
        val kSlop = 0.05
        //процент проникновения, чтобы исправить
        val percent = 0.4
        val correction: Vec =
            (max(penetration - kSlop, 0.0) / (actor1.size + actor2.size)) * normal * percent
        actor1.center -= correction * actor1.size
        actor2.center += correction * actor2.size
    }
}