package com.fiz.android.battleinthespace.engine

import com.fiz.android.battleinthespace.Dispatch
import com.fiz.android.battleinthespace.EPSILON
import com.fiz.android.battleinthespace.dt
import com.fiz.android.battleinthespace.gravity
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Manifold(var A: Body, var B: Body) {
    // Глубина проникновения от столкновения
    var penetration: Double = 0.0

    // Из A в B
    var normal: Vec2 = Vec2(0.0, 0.0)

    // Точки соприкосновения во время столкновения
    var contacts: Array<Vec2> = arrayOf(Vec2(0.0, 0.0), Vec2(0.0, 0.0))

    // Количество контактов, произошедших во время столкновения
    var contact_count: Int = 0

    // Смешанная реституция
    var e: Double = 0.0

    // Смешанное динамическое трение
    var df: Double = 0.0

    // Смешанное статическое трение
    var sf: Double = 0.0

    // Генерировать контактную информацию
    fun Solve() {
        Dispatch(A, B)
    }

    // Предварительные расчеты для импульсного решения
    fun Initialize() {
        // Рассчитать среднюю реституцию
        e = min(A.restitution, B.restitution);

        // Рассчитать статическое и динамическое трение
        sf = sqrt(A.staticFriction * A.staticFriction);
        df = sqrt(A.dynamicFriction * A.dynamicFriction);

        for (i in 0 until contact_count) {
            // Рассчитать радиусы от COM до контакта
            val ra: Vec2 = contacts[i] - A.position
            val rb: Vec2 = contacts[i] - B.position

            val rv: Vec2 = B.velocity + Cross(B.angularVelocity, rb) - A.velocity - Cross(
                A.angularVelocity,
                ra
            );

            // Определите, следует ли нам выполнять столкновение в состоянии покоя или нет
            // Идея в том, что если единственное, что движет этим объектом, - это гравитация,
            // то столкновение должно быть выполнено без какой-либо компенсации
            if (rv.LenSqr() < (dt * gravity).LenSqr() + EPSILON)
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
            val ra: Vec2 = contacts[i] - A.position
            val rb: Vec2 = contacts[i] - B.position

            // Относительная скорость
            var rv: Vec2 = B.velocity + Cross(B.angularVelocity, rb) - A.velocity - Cross(
                A.angularVelocity,
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
            val impulse: Vec2 = normal * j
            A.ApplyImpulse(-impulse, ra)
            B.ApplyImpulse(impulse, rb)

            // Импульс трения
            rv = B.velocity + Cross(B.angularVelocity, rb) - A.velocity - Cross(
                A.angularVelocity,
                ra)

            val t: Vec2 = rv - (normal * Dot(rv, normal));
            t.Normalize();

            // j тангенс величины
            var jt: Double = -Dot(rv, t);
            jt /= invMassSum;
            jt /= contact_count.toDouble()

            // Не применяйте крошечные импульсы трения
            if (Equal(jt, 0.0))
                return;

            // Закон Кулона
            var tangentImpulse: Vec2
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
        val correction: Vec2 = (max(penetration - k_slop, 0.0) / (A.im + B.im)) * normal * percent
        A.position -= correction * A.im
        B.position += correction * B.im
    }

    fun InfiniteMassCorrection() {
        A.velocity.Set(0.0, 0.0)
        B.velocity.Set(0.0, 0.0)
    }
}