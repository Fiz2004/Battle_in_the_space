//package com.fiz.android.battleinthespace.engine
//
//class Body(shape: Shape, x:Int, y:Int): Shape(shape.clone()) {
//    var position: Vec2=Vec2(x.toDouble(), y.toDouble())
//    var velocity: Vec2=Vec2(0.0, 0.0)
//
//    var angularVelocity: Double=0.0
//    var torque: Double=0.0
//
//    //радианы
//    var orient: Double= Random(-Math.PI, Math.PI)
//
//    var force: Vec2=Vec2(0.0, 0.0)
//
//    // Устанавливается по форме
//    // момент инерции
//    var I: Double?=null
//
//    // обратная инерция
//    var iI: Double?=null
//
//    // масса
//    var m: Double?=null
//
//    // обратный масса
//    var im: Double?=null
//
//    var staticFriction: Double= 0.5
//    var dynamicFriction: Double= 0.3
//    var restitution: Double= 0.2
//
//    // Интерфейс формы
//    var shape: Shape?=null
//
//    // Сохранение цвета в формате RGB
//    var r: Double= Random(0.2, 1.0)
//    var g: Double= Random(0.2, 1.0)
//    var b: Double= Random(0.2, 1.0)
//
//    init {
//        shape.body = this;
//        shape.Initialize()
//    }
//
//    fun ApplyForce( f:Vec2 )    {
//        force += f
//    }
//
//    fun ApplyImpulse( impulse:Vec2, contactVector:Vec2 )    {
//        velocity += im * impulse
//        angularVelocity += iI * Cross(contactVector, impulse)
//    }
//
//    fun SetStatic() {
//        I = 0.0
//        iI = 0.0
//        m = 0.0
//        im = 0.0
//    }
//
//    fun SetOrient(radians: Double) {
//        orient = radians
//        shape.SetOrient(radians)
//    }
//}