//package com.fiz.android.battleinthespace.engine
//
//abstract class Shape {
//    abstract var body: Body
//
//    // Для круглой формы
//    abstract var radius: Double
//
//    // Для многоугольной формы
//    // Матрица ориентации от модели к миру
//    abstract var u: Mat2
//
//    enum class Type {
//        eCircle,
//        ePoly,
//        eCount
//    }
//
//    abstract fun Clone(): Shape
//    abstract fun Initialize()
//    abstract fun ComputeMass(density: Double)
//    abstract fun SetOrient(radians: Double)
//    abstract fun Draw()
//    abstract fun GetType(): Type
//}