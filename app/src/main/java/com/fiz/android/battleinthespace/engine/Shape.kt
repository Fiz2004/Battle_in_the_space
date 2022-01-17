package com.fiz.android.battleinthespace.engine

open class Shape {
    open lateinit var body: Body

    // Для круглой формы
    open var radius: Double = 0.0

    // Для многоугольной формы
    // Матрица ориентации от модели к миру
    open var u: Mat2 = Mat2(0.0)

    enum class Type {
        eCircle,
        ePoly,
        eCount
    }

    open fun Clone(): Shape{
        return Shape()
    }
    open fun Initialize(){}
    open fun ComputeMass(density: Double){}
    open fun SetOrient(radians: Double){}
    open fun Draw(){}
    open fun GetType(): Type{
        return Type.eCount
    }
}