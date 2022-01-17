package com.fiz.android.battleinthespace.engine

import com.fiz.android.battleinthespace.EPSILON
import com.fiz.android.battleinthespace.MaxPolyVertexCount
import kotlin.math.min

class PolygonShape : Shape() {
    var m_vertexCount: Int=0
    var m_vertices: Array<Vec2> = emptyArray()//(MaxPolyVertexCount)
    var m_normals: Array<Vec2> = emptyArray()//(MaxPolyVertexCount)

    override fun Initialize() {
        ComputeMass(1.0)
    }

    override fun Clone(): PolygonShape {
        val poly = PolygonShape()
        poly.u = u
        for (i in 0 until m_vertexCount) {
            poly.m_vertices[i] = m_vertices[i]
            poly.m_normals[i] = m_normals[i]
        }
        poly.m_vertexCount = m_vertexCount
        return poly
    }

    override fun ComputeMass(density: Double) {
        // Вычислить центр тяжести и момент интериора
        // центроид
        var c = Vec2(0.0, 0.0)
        var area  = 0.0
        var I  = 0.0
        val k_inv3: Double = 1.0 / 3.0

        for (i1 in 0 until m_vertexCount) {
            //Вершины треугольника, третья вершина подразумевается как (0, 0)
            val p1: Vec2 = m_vertices[i1]
            val i2: Int = if (i1 + 1 < m_vertexCount) i1 + 1 else 0
            val p2: Vec2 = m_vertices[i2]

            val D: Double = Cross(p1, p2)
            val triangleArea: Double = 0.5f * D

            area += triangleArea

            // Используйте область для взвешивания среднего значения центроида, а не только положения вершины
            c += triangleArea * k_inv3 * (p1 + p2)

            val intx2: Double = p1.x * p1.x + p2.x * p1.x + p2.x * p2.x
            val inty2: Double = p1.y * p1.y + p2.y * p1.y + p2.y * p2.y
            I += (0.25f * k_inv3 * D) * (intx2 + inty2)
        }

        c *= 1.0f / area

        // Перевести вершины в центр тяжести (сделать центр тяжести (0, 0)
        // для многоугольника в пространстве модели)
        // На самом деле в этом нет необходимости, но мне все равно нравится это делать
        for ( i in 0 until m_vertexCount)
            m_vertices[i] -= c

        body.m = density * area
        body.im =  1.0 / body.m 
        body.I = I * density
        body.iI =  1.0 / body.I 
    }

    override fun SetOrient(radians: Double) {
        u.Set(radians)
    }

    override fun Draw() {
//        glColor3f(body.r, body.g, body.b)
//        glBegin(GL_LINE_LOOP)
        for (i in 0 until m_vertexCount)        {
            var v:Vec2 = body.position+u * m_vertices[i]
//            glVertex2f(v.x, v.y)
        }
//        glEnd()
    }

    override fun GetType(): Type {
        return Type.ePoly
    }

    //Половина ширины и половина высоты
    fun SetBox(hw: Double, hh: Double) {
        m_vertexCount = 4
        m_vertices[0].Set(-hw, -hh)
        m_vertices[1].Set(hw, -hh)
        m_vertices[2].Set(hw, hh)
        m_vertices[3].Set(-hw, hh)
        m_normals[0].Set(0.0, -1.0)
        m_normals[1].Set(1.0, 0.0)
        m_normals[2].Set(0.0, 1.0)
        m_normals[3].Set(-1.0, 0.0)
    }

    fun Set(vertices: Array<Vec2>, _count: Int) {
        // Нет корпусов с менее чем 3 вершинами (убедитесь, что полигон фактический)
        assert(_count > 2 && _count <= MaxPolyVertexCount)
        var count = min(_count.toInt(), MaxPolyVertexCount)

        // Находим самую правую точку на корпусе
        var rightMost:Int = 0
        var highestXCoord:Double = vertices[0].x
        for ( i in 1 until count)        {
            var x:Double = vertices[i].x
            if (x > highestXCoord) {
                highestXCoord = x
                rightMost = i
            }

            // Если соответствует x, то возьмите самый дальний отрицательный y
            else if (x == highestXCoord)
                if (vertices[i].y < vertices[rightMost].y)
                    rightMost = i
        }

        var hull:Array<Int> = emptyArray()// [MaxPolyVertexCount]
        var outCount:Int = 0
        var indexHull:Int = rightMost

        while (true) {
            hull[outCount] = indexHull

            // Поиск следующего индекса, который охватывает корпус
            // путем вычисления векторных произведений, чтобы найти наибольшее число против часовой стрелки
            // вершина в наборе, учитывая предыдущий индекс корпуса
            var nextHullIndex:Int = 0
            for (i in 1 until count.toInt())
            {
                // Пропустить, если та же координата, что и нам нужны три уникальных
                // точки в наборе для выполнения перекрестного произведения
                if (nextHullIndex == indexHull) {
                    nextHullIndex = i
                    continue
                }

                // Пересечь каждый набор из трех уникальных вершин
                // Запишите каждую третью вершину против часовой стрелки и добавьте
                // в выходную оболочку
                val e1:Vec2 = vertices [nextHullIndex] - vertices[hull[outCount]]
                val e2:Vec2 = vertices [i] - vertices[hull[outCount]]
                val c:Double = Cross (e1, e2)
                if (c < 0.0)
                    nextHullIndex = i

                // Перекрестное произведение равно нулю, тогда векторы e находятся на одной линии
                // // поэтому я хочу записать самую дальнюю вершину вдоль этой линии
                if (c == 0.0 && e2.LenSqr() > e1.LenSqr())
                    nextHullIndex = i
            }

            ++outCount
            indexHull = nextHullIndex

            // Завершение алгоритма при переходе
            if (nextHullIndex == rightMost) {
                m_vertexCount = outCount
                break
            }
        }

        // Копируем вершины в вершины фигуры
        for (i in 0 until m_vertexCount)
        m_vertices[i] = vertices[hull[i]]

        // Вычисляем нормали лица
        for ( i1 in 0 until m_vertexCount)        {
            val i2:Int = if (i1 +1 < m_vertexCount)  i1+1 else 0
            val face:Vec2 = m_vertices[i2] - m_vertices[i1]

            // Убедитесь, что нет ребер нулевой длины, потому что это плохо
            assert(face.LenSqr() > EPSILON * EPSILON)

            // Вычислить нормаль с помощью 2D-перекрестного произведения вектора и скаляра
            m_normals[i1] = Vec2(face.y, -face.x)
            m_normals[i1].Normalize()
        }
    }

    // Крайняя точка вдоль направления внутри многоугольника
    fun GetSupport(dir:Vec2):Vec2    {
        var bestProjection:Double = -Double.MAX_VALUE
        var bestVertex:Vec2=Vec2(0.0,0.0)

        for ( i in 0 until m_vertexCount)        {
            val v:Vec2 = m_vertices [i]
            val projection:Double = Dot (v, dir)

            if (projection > bestProjection) {
                bestVertex = v
                bestProjection = projection
            }
        }

        return bestVertex
    }
}

