//package com.fiz.android.battleinthespace
//
//import com.fiz.android.battleinthespace.engine.*
//import kotlin.math.min
//import kotlin.math.sqrt
//
//const val  EPSILON:Double = 0.0001
//val MaxPolyVertexCount: Int = 64
//
//
//const val gravityScale:Double = 5.0
//val gravity:Vec2=Vec2( 0.0, 10.0f * gravityScale )
//const val dt:Double = 1.0 / 60.0
//
//fun  Dispatch[Shape::eCount][Shape::eCount]:CollisionCallback =
//{
//    { CircletoCircle, CircletoPolygon
//    },
//    { PolygontoCircle, PolygontoPolygon
//    },
//};
//
//fun CircletoCircle(m: Manifold, a: Body, b: Body) {
//    var A: Circle = a as Circle
//    var B: Circle = b as Circle
//
//    // Вычислить вектор поступательного движения, который является нормальным
//    var normal: Vec2 = b.position - a.position
//
//    var dist_sqr: Double = normal.LenSqr()
//    var radius: Double = A.radius + B.radius
//
//    // Не в контакте
//    if (dist_sqr >= radius * radius) {
//        m.contact_count = 0
//        return
//    }
//
//    var distance: Double = sqrt(dist_sqr)
//
//    m.contact_count = 1
//
//    if (distance == 0.0) {
//        m.penetration = A.radius;
//        m.normal = Vec2(1.0, 0.0)
//        m.contacts[0] = a.position
//    } else {
//        m.penetration = radius - distance
//        // Быстрее, чем при использовании нормализованного, так как мы уже выполнили sqrt
//        m.normal = normal / distance;
//        m.contacts[0] = m.normal * A.radius + a.position;
//    }
//}
//
//fun CircletoPolygon(m:Manifold, a:Body, b:Body ){
//    var A:Circle = a.shape as Circle
//    var B:PolygonShape = b.shape as PolygonShape
//
//    m.contact_count = 0;
//
//    // Transform circle center to Polygon model space
//    Vec2 center = a->position;
//    center = B->u.Transpose() * (center-b->position);
//
//    // Find edge with minimum penetration
//    // Exact concept as using support points in Polygon vs Polygon
//    real separation = - FLT_MAX;
//    uint32 faceNormal = 0;
//    for (uint32 i = 0; i < B->m_vertexCount; ++i)
//    {
//        real s = Dot (B->m_normals[i], center-B->m_vertices[i]);
//
//        if (s > A->radius)
//        return;
//
//        if (s > separation) {
//            separation = s;
//            faceNormal = i;
//        }
//    }
//
//    // Grab face's vertices
//    Vec2 v1 = B->m_vertices[faceNormal];
//    uint32 i2 = faceNormal +1 < B->m_vertexCount ? faceNormal+1 : 0;
//    Vec2 v2 = B->m_vertices[i2];
//
//    // Check to see if center is within polygon
//    if (separation < EPSILON)
//        { m ->
//            contact_count = 1;
//            m->normal = -(B->u * B->m_normals[faceNormal]);
//            m->contacts[0] = m->normal * A->radius+a->position;
//            m->penetration = A->radius;
//            return;
//        }
//
//    // Determine which voronoi region of the edge center of circle lies within
//    real dot1 = Dot (center - v1, v2-v1);
//    real dot2 = Dot (center - v2, v1-v2);
//    m->penetration = A->radius-separation;
//
//    // Closest to v1
//    if (dot1 <= 0.0f) {
//        if (DistSqr(center, v1) > A->radius * A->radius)
//        return;
//
//        m->contact_count = 1;
//        Vec2 n = v1 -center;
//        n = B->u * n;
//        n.Normalize();
//        m->normal = n;
//        v1 = B->u * v1+b->position;
//        m->contacts[0] = v1;
//    }
//
//    // Closest to v2
//    else if (dot2 <= 0.0f) {
//        if (DistSqr(center, v2) > A->radius * A->radius)
//        return;
//
//        m->contact_count = 1;
//        Vec2 n = v2 -center;
//        v2 = B->u * v2+b->position;
//        m->contacts[0] = v2;
//        n = B->u * n;
//        n.Normalize();
//        m->normal = n;
//    }
//
//    // Closest to face
//    else {
//        Vec2 n = B->m_normals[faceNormal];
//        if (Dot(center - v1, n) > A->radius)
//        return;
//
//        n = B->u * n;
//        m->normal = -n;
//        m->contacts[0] = m->normal * A->radius+a->position;
//        m->contact_count = 1;
//    }
//}
//
//fun PolygontoCircle( Manifold *m, Body *a, Body *b )
//{
//    CircletoPolygon(m, b, a);
//    m->normal = -m->normal;
//}
//
//fun FindAxisLeastPenetration(A:PolygonShape,  B:PolygonShape ):Array<Double>{
//    var bestDistance:Double = -Double.MAX_VALUE
//    var bestIndex:Int
//
//    for (i in 0 until A.m_vertexCount)    {
//        // Получить нормаль лица из A
//        var n:Vec2 = A.m_normals[i]
//        var nw:Vec2 = A.u * n
//
//        // Преобразование нормали к лицу в пространство модели B
//        Mat2 buT = B->u.Transpose();
//        n = buT * nw;
//
//        // Retrieve support point from B along -n
//        Vec2 s = B->GetSupport(-n);
//
//        // Retrieve vertex on face from A, transform into
//        // B's model space
//        Vec2 v = A->m_vertices[i];
//        v = A->u * v+A->body->position;
//        v -= B->body->position;
//        v = buT * v;
//
//        // Compute penetration distance (in B's model space)
//        real d = Dot (n, s-v);
//
//        // Store greatest distance
//        if (d > bestDistance) {
//            bestDistance = d;
//            bestIndex = i;
//        }
//    }
//
//    var _faceIndex = bestIndex
//    return arrayOf(bestDistance,_faceIndex)
//}
//
//fun FindIncidentFace(  v:Vec2,  RefPoly:PolygonShape,  IncPoly:PolygonShape, referenceIndex:Int ){
//    Vec2 referenceNormal = RefPoly->m_normals[referenceIndex];
//
//    // Calculate normal in incident's frame of reference
//    referenceNormal = RefPoly->u * referenceNormal; // To world space
//    referenceNormal = IncPoly->u.Transpose() * referenceNormal; // To incident's model space
//
//    // Find most anti-normal face on incident polygon
//    int32 incidentFace = 0;
//    real minDot = FLT_MAX;
//    for (uint32 i = 0; i < IncPoly->m_vertexCount; ++i)
//    {
//        real dot = Dot (referenceNormal, IncPoly->m_normals[i]);
//        if (dot < minDot) {
//            minDot = dot;
//            incidentFace = i;
//        }
//    }
//
//    // Assign face vertices for incidentFace
//    v[0] = IncPoly->u * IncPoly->m_vertices[incidentFace]+IncPoly->body->position;
//    incidentFace = incidentFace + 1 >= (int32) IncPoly->m_vertexCount ? 0 : incidentFace+1;
//    v[1] = IncPoly->u * IncPoly->m_vertices[incidentFace]+IncPoly->body->position;
//}
//
//fun Clip( Vec2 n, real c, Vec2 *face ):Int
//{
//    uint32 sp = 0;
//    Vec2 out [2] = {
//        face[0],
//        face[1]
//    };
//
//    // Retrieve distances from each endpoint to the line
//    // d = ax + by - c
//    real d1 = Dot (n, face[0])-c;
//    real d2 = Dot (n, face[1])-c;
//
//    // If negative (behind plane) clip
//    if (d1 <= 0.0f) out[sp++] = face[0];
//    if (d2 <= 0.0f) out[sp++] = face[1];
//
//    // If the points are on different sides of the plane
//    if (d1 * d2 < 0.0f) // less than to ignore -0.0f
//    {
//        // Push interesection point
//        real alpha = d1 /(d1 - d2);
//        out[sp] = face[0] + alpha * (face[1] - face[0]);
//        ++sp;
//    }
//
//    // Assign our new converted values
//    face[0] = out[0];
//    face[1] = out[1];
//
//    assert(sp != 3);
//
//    return sp;
//}
//
//fun PolygontoPolygon(  m:Manifold,  a:Body,  b:Body ){
//    var A:PolygonShape = a.shape as PolygonShape
//    var B:PolygonShape = (b.shape as PolygonShape)
//
//    m.contact_count = 0
//
//    // Проверьте разделяющую ось с плоскостями лица A
//    var (penetrationA:Double,faceA:Int)  = FindAxisLeastPenetration (  A, B);
//    if (penetrationA >= 0.0f)
//        return;
//
//    // Проверьте разделяющую ось с плоскостями лица B
//    var faceB:Int
//    var (penetrationB:Double,faceB:Int) = FindAxisLeastPenetration (  B, A)
//    if (penetrationB >= 0.0f)
//        return;
//
//    uint32 referenceIndex;
//    bool flip; // Always point from a to b
//
//    PolygonShape * RefPoly; // Reference
//    PolygonShape * IncPoly; // Incident
//
//    // Determine which shape contains reference face
//    if (BiasGreaterThan(penetrationA, penetrationB)) {
//        RefPoly = A;
//        IncPoly = B;
//        referenceIndex = faceA;
//        flip = false;
//    } else {
//        RefPoly = B;
//        IncPoly = A;
//        referenceIndex = faceB;
//        flip = true;
//    }
//
//    // World space incident face
//    Vec2 incidentFace [2];
//    FindIncidentFace(incidentFace, RefPoly, IncPoly, referenceIndex);
//
//    //        y
//    //        ^  ->n       ^
//    //      +---c ------posPlane--
//    //  x < | i |\
//    //      +---+ c-----negPlane--
//    //             \       v
//    //              r
//    //
//    //  r : reference face
//    //  i : incident poly
//    //  c : clipped point
//    //  n : incident normal
//
//    // Setup reference face vertices
//    Vec2 v1 = RefPoly->m_vertices[referenceIndex];
//    referenceIndex = referenceIndex + 1 == RefPoly->m_vertexCount ? 0 : referenceIndex+1;
//    Vec2 v2 = RefPoly->m_vertices[referenceIndex];
//
//    // Transform vertices to world space
//    v1 = RefPoly->u * v1+RefPoly->body->position;
//    v2 = RefPoly->u * v2+RefPoly->body->position;
//
//    // Calculate reference face side normal in world space
//    Vec2 sidePlaneNormal =(v2 - v1);
//    sidePlaneNormal.Normalize();
//
//    // Orthogonalize
//    Vec2 refFaceNormal (sidePlaneNormal.y, -sidePlaneNormal.x);
//
//    // ax + by = c
//    // c is distance from origin
//    real refC = Dot (refFaceNormal, v1);
//    real negSide = - Dot(sidePlaneNormal, v1);
//    real posSide = Dot (sidePlaneNormal, v2);
//
//    // Clip incident face to reference face side planes
//    if (Clip(-sidePlaneNormal, negSide, incidentFace) < 2)
//        return; // Due to floating point error, possible to not have required points
//
//    if (Clip(sidePlaneNormal, posSide, incidentFace) < 2)
//        return; // Due to floating point error, possible to not have required points
//
//    // Flip
//    m->normal = flip ?-refFaceNormal : refFaceNormal;
//
//    // Keep points behind reference face
//    uint32 cp = 0; // clipped points behind reference face
//    real separation = Dot (refFaceNormal, incidentFace[0])-refC;
//    if (separation <= 0.0f)
//        { m ->
//            contacts[cp] = incidentFace[0];
//            m->penetration = -separation;
//            ++cp;
//        }
//    else
//        m->penetration = 0;
//
//    separation = Dot(refFaceNormal, incidentFace[1]) - refC;
//    if (separation <= 0.0f)
//        { m ->
//            contacts[cp] = incidentFace[1];
//
//            m->penetration += -separation;
//            ++cp;
//
//            // Average penetration
//            m->penetration /= (real)cp;
//        }
//
//    m->contact_count = cp;
//}
//
//
//
//
