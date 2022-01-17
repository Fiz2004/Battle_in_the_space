package com.fiz.android.battleinthespace.engine

import com.fiz.android.battleinthespace.EPSILON
import com.fiz.android.battleinthespace.actor.MoveableActor
import com.fiz.android.battleinthespace.dt
import com.fiz.android.battleinthespace.gravity
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Manifold(var A: MoveableActor, var B: MoveableActor) {
    // Глубина проникновения от столкновения
    var penetration: Double = 0.0

    // Из A в B
    var normal: Vec = Vec(0.0, 0.0)

    // Точки соприкосновения во время столкновения
    var contacts: Array<Vec> = arrayOf(Vec(0.0, 0.0), Vec(0.0, 0.0))

    // Количество контактов, произошедших во время столкновения
    var contact_count: Int = 0

    // Смешанная реституция
    var e: Double = 0.0

    // Смешанное динамическое трение
    var df: Double = 0.0

    // Смешанное статическое трение
    var sf: Double = 0.0

    // Генерировать контактную информацию
//    fun Solve() {
//        Dispatch(A, B)
//    }

    // Предварительные расчеты для импульсного решения
    fun Initialize() {
        // Рассчитать среднюю реституцию
        e = min(A.restitution, B.restitution);

        // Рассчитать статическое и динамическое трение
        sf = sqrt(A.staticFriction * A.staticFriction);
        df = sqrt(A.dynamicFriction * A.dynamicFriction);

        for (i in 0 until contact_count) {
            // Рассчитать радиусы от COM до контакта
            val ra: Vec = contacts[i] - A.center
            val rb: Vec = contacts[i] - B.center

            val rv: Vec = B.speed + Cross(B.angleSpeed, rb) - A.speed - Cross(
                A.angleSpeed,
                ra
            );

            // Определите, следует ли нам выполнять столкновение в состоянии покоя или нет
            // Идея в том, что если единственное, что движет этим объектом, - это гравитация,
            // то столкновение должно быть выполнено без какой-либо компенсации
            if (rv.sumPow2() < (dt * gravity).LenSqr() + EPSILON)
                e = 0.0;
        }
    }

    // Решите импульс и примените
    fun ApplyImpulse() {
        // Ранний выход и правильное позиционирование, если оба объекта имеют бесконечную массу
        if (Equal(A.im + B.im, 0.0)) {
            InfiniteMassCorrection();
            return;
        }

        for (i in 0 until contact_count) {
            //Рассчитать радиусы от COM до контакта
            val ra: Vec = contacts[i] - A.center
            val rb: Vec = contacts[i] - B.center

            // Относительная скорость
            var rv: Vec = B.speed + Cross(B.angleSpeed, rb) - A.speed - Cross(
                A.angleSpeed,
                ra
            )

            //Относительная скорость по нормали
            val contactVel: Double = Dot(rv, normal)

            // Не разрешайте, если скорости разделяются
            if (contactVel > 0)
                return

            val raCrossN: Double = Cross(ra, normal)
            val rbCrossN: Double = Cross(rb, normal)
            val invMassSum: Double = A.im + B.im + Sqr(raCrossN) * A.iI + Sqr(rbCrossN) * B.iI

            // Вычислить скалярный импульс
            var j: Double = -(1.0f + e) * contactVel
            j /= invMassSum
            j /= contact_count.toDouble()

            // Применить импульс
            val impulse: Vec = normal * j
            A.ApplyImpulse(-impulse, ra)
            B.ApplyImpulse(impulse, rb)

            // Импульс трения
            rv = B.speed + Cross(B.angleSpeed, rb) - A.speed - Cross(
                A.angleSpeed,
                ra)

            val t: Vec = rv - (normal * Dot(rv, normal));
            t.Normalize();

            // j тангенс величины
            var jt: Double = -Dot(rv, t);
            jt /= invMassSum;
            jt /= contact_count.toDouble()

            // Не применяйте крошечные импульсы трения
            if (Equal(jt, 0.0))
                return;

            // Закон Кулона
            var tangentImpulse: Vec
            if (abs(jt) < j * sf)
                tangentImpulse = t * jt;
            else
                tangentImpulse = t * -j * df;

            // Применить импульс трения
            A.ApplyImpulse(-tangentImpulse, ra);
            B.ApplyImpulse(tangentImpulse, rb);
        }
    }

    // Наивная коррекция позиционного проникновения
    fun PositionalCorrection() {
        //Надбавка за проникновение
        val k_slop: Double = 0.05
        //процент проникновения, чтобы исправить
        val percent: Double = 0.4
        val correction: Vec = (max(penetration - k_slop, 0.0) / (A.im + B.im)) * normal * percent
        A.center -= correction * A.im
        B.center += correction * B.im
    }

    fun InfiniteMassCorrection() {
        A.speed.Set(0.0, 0.0)
        B.speed.Set(0.0, 0.0)
    }
}