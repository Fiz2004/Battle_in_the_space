//package com.fiz.android.battleinthespace.engine
//
//import com.fiz.android.battleinthespace.MaxPolyVertexCount
//
//class PolygonShape : Shape() {
//    val poly?: PolygonShape = null
//
//    var m_vertexCount: Int=0
//    var m_vertices: Array<Vec2>(MaxPolyVertexCount)
//    var m_normals: Array<Vec2>(MaxPolyVertexCount)
//
//    override fun Initialize() {
//        ComputeMass(1.0)
//    }
//
//    override fun Clone(): PolygonShape {
//        val poly = PolygonShape()
//        poly.u = u
//        for (i in 0 until m_vertexCount) {
//            poly.m_vertices[i] = m_vertices[i]
//            poly.m_normals[i] = m_normals[i]
//        }
//        poly.m_vertexCount = m_vertexCount
//        return poly
//    }
//
//    override fun ComputeMass(density: Double) {
//        // Вычислить центр тяжести и момент интериора
//        // центроид
//        var c: Vec2 = Vec2(0.0, 0.0)
//        var area: Double = 0.0
//        var I: Double = 0.0
//        val k_inv3: Double = 1.0 / 3.0
//
//        for (i1 in 0 until m_vertexCount) {
//            //Вершины треугольника, третья вершина подразумевается как (0, 0)
//            var p1: Vec2 = Vec2(m_vertices[i1])
//            var i2: Int = if (i1 + 1 < m_vertexCount) i1 + 1 else 0
//            var p2: Vec2 = Vec2(m_vertices[i2])
//
//            var D: Double = Cross(p1, p2)
//            var triangleArea: Double = 0.5f * D
//
//            area += triangleArea
//
//            // Используйте область для взвешивания среднего значения центроида, а не только положения вершины
//            c += triangleArea * k_inv3 * (p1 + p2)
//
//            var intx2: Double = p1.x * p1.x + p2.x * p1.x + p2.x * p2.x
//            var inty2: Double = p1.y * p1.y + p2.y * p1.y + p2.y * p2.y
//            I += (0.25f * k_inv3 * D) * (intx2 + inty2)
//        }
//
//        c *= 1.0f / area
//
//        // Перевести вершины в центр тяжести (сделать центр тяжести (0, 0)
//        // для многоугольника в пространстве модели)
//        // На самом деле в этом нет необходимости, но мне все равно нравится это делать
//        for ( i in 0 until m_vertexCount)
//            m_vertices[i] -= c
//
//        body.m = density * area;
//        body.im = if (body.m)  1.0 / body.m else 0.0
//        body.I = I * density
//        body.iI = if (body.I) 1.0 / body.I else 0.0
//    }
//
//    override fun SetOrient(radians: Double) {
//        u.Set(radians)
//    }
//
//    override fun Draw() {
//        glColor3f(body.r, body.g, body.b)
//        glBegin(GL_LINE_LOOP)
//        for (i in 0 until m_vertexCount)        {
//            мфк v:Vec2 = bodyюposition+u * m_vertices[i]
//            glVertex2f(v.x, v.y)
//        }
//        glEnd()
//    }
//
//    override fun GetType(): Type {
//        return Type.ePoly
//    }
//
//    //Половина ширины и половина высоты
//    fun SetBox(hw: Double, hh: Double) {
//        m_vertexCount = 4
//        m_vertices[0].Set(-hw, -hh)
//        m_vertices[1].Set(hw, -hh)
//        m_vertices[2].Set(hw, hh)
//        m_vertices[3].Set(-hw, hh)
//        m_normals[0].Set(0.0, -1.0)
//        m_normals[1].Set(1.0, 0.0)
//        m_normals[2].Set(0.0, 1.0)
//        m_normals[3].Set(-1.0, 0.0)
//    }
//
//    fun Set(vertices: Vec2, count: Int) {
//        // Нет корпусов с менее чем 3 вершинами (убедитесь, что полигон фактический)
//        assert(count > 2 && count <= MaxPolyVertexCount);
//        count = min(count.toInt(), MaxPolyVertexCount);
//
//        // Находим самую правую точку на корпусе
//        int32 rightMost = 0;
//        real highestXCoord = vertices [0].x;
//        for ( i in 1 until count)        {
//            real x = vertices [i].x;
//            if (x > highestXCoord) {
//                highestXCoord = x;
//                rightMost = i;
//            }
//
//            // If matching x then take farthest negative y
//            else if (x == highestXCoord)
//                if (vertices[i].y < vertices[rightMost].y)
//                    rightMost = i;
//        }
//
//        int32 hull [MaxPolyVertexCount];
//        var outCount:Int = 0
//        var indexHull:Int = rightMost
//
//        for (;;) {
//            hull[outCount] = indexHull;
//
//            // Поиск следующего индекса, который охватывает корпус
//            // путем вычисления векторных произведений, чтобы найти наибольшее число против часовой стрелки
//            // вершина в наборе, учитывая предыдущий индекс корпуса
//            int32 nextHullIndex = 0;
//            for (i in 1 until count.ToInt())
//            {
//                // Skip if same coordinate as we need three unique
//                // points in the set to perform a cross product
//                if (nextHullIndex == indexHull) {
//                    nextHullIndex = i;
//                    continue;
//                }
//
//                // Cross every set of three unique vertices
//                // Record each counter clockwise third vertex and add
//                // to the output hull
//                // See : http://www.oocities.org/pcgpe/math2d.html
//                Vec2 e1 = vertices [nextHullIndex] - vertices[hull[outCount]];
//                Vec2 e2 = vertices [i] - vertices[hull[outCount]];
//                real c = Cross (e1, e2);
//                if (c < 0.0f)
//                    nextHullIndex = i;
//
//                // Cross product is zero then e vectors are on same line
//                // therefor want to record vertex farthest along that line
//                if (c == 0.0f && e2.LenSqr() > e1.LenSqr())
//                    nextHullIndex = i;
//            }
//
//            ++outCount;
//            indexHull = nextHullIndex;
//
//            // Завершение алгоритма при переходе
//            if (nextHullIndex == rightMost) {
//                m_vertexCount = outCount;
//                break;
//            }
//        }
//
//        // Копируем вершины в вершины фигуры
//        for (i in 0 until m_vertexCount)
//        m_vertices[i] = vertices[hull[i]];
//
//        // Вычисляем нормали лица
//        for ( i1 in 0 until m_vertexCount)        {
//            var i2:Int = if (i1 +1 < m_vertexCount)  i1+1 else 0
//            var face:Vec2 = m_vertices[i2] - m_vertices[i1]
//
//            // Ensure no zero-length edges, because that's bad
//            assert(face.LenSqr() > EPSILON * EPSILON);
//
//            // Calculate normal with 2D cross product between vector and scalar
//            m_normals[i1] = Vec2(face.y, -face.x);
//            m_normals[i1].Normalize();
//        }
//    }
//
//    // Крайняя точка вдоль направления внутри многоугольника
//    fun GetSupport(dir:Vec2):Vec2    {
//        var bestProjection:Double = -Double.MAX_VALUE
//        var bestVertex:Vec2
//
//        for ( i in 0 until m_vertexCount)        {
//            var v:Vec2 = m_vertices [i]
//            var projection:Double = Dot (v, dir)
//
//            if (projection > bestProjection) {
//                bestVertex = v
//                bestProjection = projection
//            }
//        }
//
//        return bestVertex
//    }
//}
//
