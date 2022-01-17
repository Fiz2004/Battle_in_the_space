package com.fiz.android.battleinthespace

import com.fiz.android.battleinthespace.engine.*
import kotlin.math.min
import kotlin.math.sqrt

const val EPSILON: Double = 0.0001
val MaxPolyVertexCount: Int = 64


const val gravityScale: Double = 5.0
val gravity: Vec2 = Vec2(0.0, 10.0f * gravityScale)
const val dt: Double = 1.0 / 60.0

fun Dispatch(a: Shape, b: Shape): (Manifold, Body, Body) -> Unit {
    if (a is Circle && b is Circle)
        return ::CircletoCircle
    if (a is Circle && b is PolygonShape)
        return ::CircletoPolygon
    if (a is PolygonShape && b is Circle)
        return ::PolygontoCircle
    if (a is PolygonShape && b is PolygonShape)
        return ::PolygontoPolygon
    return ::CircletoCircle
}

fun CircletoCircle(m: Manifold, a: Body, b: Body) {
    val A: Circle = a as Circle
    val B: Circle = b as Circle

    // Вычислить вектор поступательного движения, который является нормальным
    val normal: Vec2 = b.position - a.position

    val dist_sqr: Double = normal.LenSqr()
    val radius: Double = A.radius + B.radius

    // Не в контакте
    if (dist_sqr >= radius * radius) {
        m.contact_count = 0
        return
    }

    val distance: Double = sqrt(dist_sqr)

    m.contact_count = 1

    if (distance == 0.0) {
        m.penetration = A.radius
        m.normal = Vec2(1.0, 0.0)
        m.contacts[0] = a.position
    } else {
        m.penetration = radius - distance
        // Быстрее, чем при использовании нормализованного, так как мы уже выполнили sqrt
        m.normal = normal / distance
        m.contacts[0] = m.normal * A.radius + a.position
    }
}

fun CircletoPolygon(m: Manifold, a: Body, b: Body) {
    var A: Circle = a.shape as Circle
    var B: PolygonShape = b.shape as PolygonShape

    m.contact_count = 0

    // Преобразуйте центр круга в пространство полигональной модели
    var center: Vec2 = a.position
    center = B.u.Transpose() * (center - b.position)

    // Найти край с минимальным проникновением
    // Точная концепция использования опорных точек в полигоне против полигона
    var separation: Double = -Double.MAX_VALUE
    var faceNormal: Int = 0
    for (i in 0 until B.m_vertexCount) {
        var s: Double = Dot(B.m_normals[i], center - B.m_vertices[i])

        if (s > A.radius)
            return

        if (s > separation) {
            separation = s
            faceNormal = i
        }
    }

    // Захватите вершины грани
    var v1: Vec2 = B.m_vertices[faceNormal]
    var i2: Int = if (faceNormal + 1 < B.m_vertexCount) faceNormal + 1 else 0
    var v2: Vec2 = B.m_vertices[i2]

    // Проверьте, находится ли центр внутри многоугольника
    if (separation < EPSILON) {
        m.contact_count = 1
        m.normal = -(B.u * B.m_normals[faceNormal])
        m.contacts[0] = m.normal * A.radius + a.position
        m.penetration = A.radius
        return
    }

    // Определите, какая область вороного центра края окружности находится внутри
    var dot1: Double = Dot(center - v1, v2 - v1)
    var dot2: Double = Dot(center - v2, v1 - v2)
    m.penetration = A.radius - separation

    // Ближайший к v1
    if (dot1 <= 0.0) {
        if (DistSqr(center, v1) > A.radius * A.radius)
            return

        m.contact_count = 1
        var n: Vec2 = v1 - center
        n = B.u * n
        n.Normalize()
        m.normal = n
        v1 = B.u * v1 + b.position
        m.contacts[0] = v1
    }

    // Ближе всего к v2
    else if (dot2 <= 0.0) {
        if (DistSqr(center, v2) > A.radius * A.radius)
            return

        m.contact_count = 1
        var n: Vec2 = v2 - center
        v2 = B.u * v2 + b.position
        m.contacts[0] = v2
        n = B.u * n
        n.Normalize()
        m.normal = n
    }

    // Ближе всего к лицу
    else {
        var n: Vec2 = B.m_normals[faceNormal]
        if (Dot(center - v1, n) > A.radius)
            return

        n = B.u * n
        m.normal = -n
        m.contacts[0] = m.normal * A.radius + a.position
        m.contact_count = 1
    }
}

fun PolygontoCircle(m: Manifold, a: Body, b: Body) {
    CircletoPolygon(m, b, a)
    m.normal = -m.normal
}

fun FindAxisLeastPenetration(A: PolygonShape, B: PolygonShape): Array<Double> {
    var bestDistance: Double = -Double.MAX_VALUE
    var bestIndex: Int = 0

    for (i in 0 until A.m_vertexCount) {
        // Получить нормаль лица из A
        var n: Vec2 = A.m_normals[i]
        var nw: Vec2 = A.u * n

        // Преобразование нормали к лицу в пространство модели B
        var buT: Mat2 = B.u.Transpose()
        n = buT * nw

        // Извлеките точку опоры из B вдоль -n
        var s: Vec2 = B.GetSupport(-n)

        // Извлеките вершину на грани из A, преобразуйте в
        // Модельное пространство  B
        var v: Vec2 = A.m_vertices[i]
        v = A.u * v + A.body.position
        v -= B.body.position
        v = buT * v

        // Вычислить расстояние проникновения (в пространстве модели B)
        var d: Double = Dot(n, s - v)

        // Хранить наибольшее расстояние
        if (d > bestDistance) {
            bestDistance = d
            bestIndex = i
        }
    }

    var _faceIndex = bestIndex.toDouble()
    return arrayOf(bestDistance, _faceIndex)
}

fun FindIncidentFace(
    v: Array<Vec2>,
    RefPoly: PolygonShape,
    IncPoly: PolygonShape,
    referenceIndex: Int) {
    var referenceNormal: Vec2 = RefPoly.m_normals[referenceIndex]

    // Вычислить норму в системе отсчета инцидентов
    //В мировое пространство
    referenceNormal = RefPoly.u * referenceNormal
    //К пространству модели инцидентов
    referenceNormal = IncPoly.u.Transpose() * referenceNormal

    // Найдите наиболее антинормальную грань на инцидентном полигоне
    var incidentFace: Int = 0
    var minDot: Double = Double.MAX_VALUE
    for (i in 0 until IncPoly.m_vertexCount) {
        var dot: Double = Dot(referenceNormal, IncPoly.m_normals[i])
        if (dot < minDot) {
            minDot = dot
            incidentFace = i
        }
    }

    // Назначить вершины грани для падающей грани
    v[0] = IncPoly.u * IncPoly.m_vertices[incidentFace] + IncPoly.body.position
    incidentFace = if (incidentFace + 1 >= IncPoly.m_vertexCount) 0 else incidentFace + 1
    v[1] = IncPoly.u * IncPoly.m_vertices[incidentFace] + IncPoly.body.position
}

fun Clip(n: Vec2, c: Double, face: Array<Vec2>): Int {
    var sp: Int = 0
    var out: Array<Vec2> = arrayOf(face[0], face[1])

    // Получение расстояний от каждой конечной точки до линии
    // d = ax + by - c
    var d1: Double = Dot(n, face[0]) - c
    var d2: Double = Dot(n, face[1]) - c

    // Если отрицательный (за плоскостью) зажим
    if (d1 <= 0.0f) out[sp++] = face[0]
    if (d2 <= 0.0f) out[sp++] = face[1]

    // Если точки находятся по разные стороны плоскости
    //меньше, чем игнорировать -0,0
    if (d1 * d2 < 0.0) {
        // Точка пересечения толчка
        var alpha: Double = d1 / (d1 - d2)
        out[sp] = face[0] + alpha * (face[1] - face[0])
        ++sp
    }

    // Назначьте наши новые преобразованные значения
    face[0] = out[0]
    face[1] = out[1]

    assert(sp != 3)

    return sp
}

fun PolygontoPolygon(m: Manifold, a: Body, b: Body) {
    var A: PolygonShape = a.shape as PolygonShape
    var B: PolygonShape = (b.shape as PolygonShape)

    m.contact_count = 0

    // Проверьте разделяющую ось с плоскостями лица A
    var (penetrationA: Double, _faceA: Double) = FindAxisLeastPenetration(A, B)
    var faceA = _faceA.toInt()
    if (penetrationA >= 0.0f)
        return

    // Проверьте разделяющую ось с плоскостями лица B
    var (penetrationB: Double, _faceB: Double) = FindAxisLeastPenetration(B, A)
    var faceB = _faceB.toInt()
    if (penetrationB >= 0.0f)
        return

    var referenceIndex: Int
    //Всегда указывайте от а до в
    var flip: Boolean

    //Ссылка
    var RefPoly: PolygonShape
    //Инцидент
    var IncPoly: PolygonShape

    // Определите, какая фигура содержит опорную грань
    if (BiasGreaterThan(penetrationA, penetrationB)) {
        RefPoly = A
        IncPoly = B
        referenceIndex = faceA
        flip = false
    } else {
        RefPoly = B
        IncPoly = A
        referenceIndex = faceB
        flip = true
    }

    // Мировой космический инцидент лицом к лицу
    var incidentFace: Array<Vec2> = arrayOf(Vec2(0.0, 0.0), Vec2(0.0, 0.0))
    FindIncidentFace(incidentFace, RefPoly, IncPoly, referenceIndex)

    //        y
    //        ^  ->n       ^
    //      +---c ------posPlane--
    //  x < | i |\
    //      +---+ c-----negPlane--
    //             \       v
    //              r
    //
    //  r : опорная грань
    //  i : инцидент поли
    //  c : обрезанная точка
    //  n : инцидент нормальный

    // Настройка вершин опорных граней
    var v1: Vec2 = RefPoly.m_vertices[referenceIndex]
    referenceIndex = if (referenceIndex + 1 == RefPoly.m_vertexCount) 0 else referenceIndex + 1
    var v2: Vec2 = RefPoly.m_vertices[referenceIndex]

    // Преобразуйте вершины в мировое пространство
    v1 = RefPoly.u * v1 + RefPoly.body.position
    v2 = RefPoly.u * v2 + RefPoly.body.position

    // Вычислить нормаль опорной лицевой стороны в мировом пространстве
    var sidePlaneNormal: Vec2 = (v2 - v1)
    sidePlaneNormal.Normalize()

    // Ортогонализировать
    var refFaceNormal = Vec2(sidePlaneNormal.y, -sidePlaneNormal.x)

    // ax + by = c
    // c является расстоянием от источника
    var refC: Double = Dot(refFaceNormal, v1)
    var negSide: Double = -Dot(sidePlaneNormal, v1)
    var posSide: Double = Dot(sidePlaneNormal, v2)

    // Закрепите плоскость, падающую с лицевой стороны, на боковые плоскости опорной грани
    if (Clip(-sidePlaneNormal, negSide, incidentFace) < 2)
    // Из-за ошибки с плавающей запятой возможно отсутствие требуемых точек
        return

    if (Clip(sidePlaneNormal, posSide, incidentFace) < 2)
    // Из-за ошибки с плавающей запятой возможно отсутствие требуемых точек
        return

    // Сальто
    m.normal = if (flip) -refFaceNormal else refFaceNormal

    // Держите точки позади контрольной грани
    //обрезанные точки за контрольной гранью
    var cp: Int = 0
    var separation: Double = Dot(refFaceNormal, incidentFace[0]) - refC
    if (separation <= 0.0) {
        m.contacts[cp] = incidentFace[0]
        m.penetration = -separation
        ++cp
    } else
        m.penetration = 0.0

    separation = Dot(refFaceNormal, incidentFace[1]) - refC
    if (separation <= 0.0) {
        m.contacts[cp] = incidentFace[1]

        m.penetration += -separation
        ++cp

        // Среднее проникновение
        m.penetration /= cp.toDouble()
    }

    m.contact_count = cp
}




