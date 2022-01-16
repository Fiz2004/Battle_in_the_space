//package com.fiz.android.battleinthespace.engine
//
//import com.fiz.android.battleinthespace.EPSILON
//import kotlin.math.*
//
//class Mat2 {
//    var m00: Double = 0.0
//    var m01: Double = 0.0
//    var m10: Double = 0.0
//    var m11: Double = 0.0
//
//    var m: Array<Array<Double>> = arrayOf(arrayOf(0.0, 0.0), arrayOf(0.0, 0.0))
//    var v: Array<Double> = arrayOf(0.0, 0.0, 0.0, 0.0)
//
//    constructor (radians: Double) {
//        val c: Double = cos(radians)
//        val s: Double = sin(radians)
//
//        m00 = c; m01 = -s;
//        m10 = s; m11 = c;
//    }
//
//    constructor (a: Double, b: Double, c: Double, d: Double) {
//        m00 = a
//        m01 = b
//        m10 = c
//        m11 = d
//    }
//
//    fun Set(radians: Double) {
//        val c: Double = cos(radians)
//        val s: Double = sin(radians)
//
//        m00 = c
//        m01 = -s
//        m10 = s
//        m11 = c
//    }
//
//    fun Abs(): Mat2 {
//        return Mat2(abs(m00), abs(m01), abs(m10), abs(m11))
//    }
//
//    fun AxisX(): Vec2 {
//        return Vec2(m00, m10)
//    }
//
//    fun AxisY(): Vec2 {
//        return Vec2(m01, m11)
//    }
//
//    fun Transpose(): Mat2 {
//        return Mat2(m00, m10, m01, m11)
//    }
//
//    operator fun times(rhs: Vec2): Vec2 {
//        return Vec2(m00 * rhs.x + m01 * rhs.y, m10 * rhs.x + m11 * rhs.y)
//    }
//
//    operator fun times(rhs: Mat2): Mat2 {
//        // [00 01]  [00 01]
//        // [10 11]  [10 11]
//        return Mat2(
//            m[0][0] * rhs.m[0][0] + m[0][1] * rhs.m[1][0],
//            m[0][0] * rhs.m[0][1] + m[0][1] * rhs.m[1][1],
//            m[1][0] * rhs.m[0][0] + m[1][1] * rhs.m[1][0],
//            m[1][0] * rhs.m[0][1] + m[1][1] * rhs.m[1][1]
//        )
//    }
//}
//
//inline operator fun Double.times(v: Vec2): Vec2 {
//    return Vec2(this * v.x, this * v.y)
//}
//
//
//inline fun Min(a: Vec2, b: Vec2): Vec2 {
//    return Vec2(min(a.x, b.x), min(a.y, b.y))
//}
//
//inline fun Max(a: Vec2, b: Vec2): Vec2 {
//    return Vec2(max(a.x, b.x), max(a.y, b.y))
//}
//
//inline fun Dot(a: Vec2, b: Vec2): Double {
//    return a.x * b.x + a.y * b.y
//}
//
//inline fun DistSqr(a: Vec2, b: Vec2): Double {
//    val c: Vec2 = a - b
//    return Dot(c, c)
//}
//
//inline fun Cross(v: Vec2, a: Double): Vec2 {
//    return Vec2(a * v.y, -a * v.x)
//}
//
//inline fun Cross(a: Double, v: Vec2): Vec2 {
//    return Vec2(-a * v.y, a * v.x)
//}
//
//inline fun Cross(a: Vec2, b: Vec2): Double {
//    return a.x * b.y - a.y * b.x;
//}
//
//// Сравнение с переносимостью EPSILON
//inline fun Equal(a: Double, b: Double): Boolean {
//    // <= вместо < для безопасности сравнения NaN
//    return abs(a - b) <= EPSILON
//}
//
//inline fun Sqr(a: Double): Double {
//    return a * a
//}
//
//inline fun Clamp(min: Double, max: Double, a: Double): Double {
//    if (a < min) return min
//    if (a > max) return max
//    return a
//}
//
//inline fun Round(a: Double): Int {
//    return (a + 0.5f).toInt()
//}
//
//inline fun Random(l: Double, h: Double): Double {
//    return kotlin.random.Random.nextDouble(l, h)
//}
//
//inline fun BiasGreaterThan(a: Double, b: Double): Boolean {
//    val k_biasRelative: Double = 0.95
//    val k_biasAbsolute: Double = 0.01
//    return a >= b * k_biasRelative + a * k_biasAbsolute
//}