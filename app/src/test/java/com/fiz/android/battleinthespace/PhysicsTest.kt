package com.fiz.android.battleinthespace

import com.fiz.android.battleinthespace.engine.Physics
import com.fiz.android.battleinthespace.engine.Vec
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class PhysicsTest {

    @Before
    fun setUp() {
        Physics.createWorld(15.0,15.0)

    }

    @Test
    fun overlap2RectangleNoOverlap() {
        assertFalse(Physics.overlap(Vec(2.5,2.5),3.0,Vec(5.6,5.6),3.0))
    }

    @Test
    fun overlap2RectangleOverlapBorder() {
        assertTrue(Physics.overlap(Vec(2.5,2.5),3.0,Vec(5.5,5.5),3.0))
    }

    @Test
    fun overlap2RectangleOverlap() {
        assertTrue(Physics.overlap(Vec(2.5,2.5),3.0,Vec(5.0,5.0),3.0))
    }

    @Test
    fun overlap2RectangleIntersectionBorderNoOverlap() {
        assertFalse(Physics.overlap(Vec(0.5,9.5),3.0,Vec(15.0,5.0),3.0))
    }

    @Test
    fun overlap2RectangleIntersectionBorderYesOverlap() {
        assertTrue(Physics.overlap(Vec(0.5,9.5),3.0,Vec(15.0,7.5),3.0))
    }

    @Test
    fun overlap2RectangleEqualsYesOverlap() {
        assertTrue(Physics.overlap(Vec(3.0,3.0),3.0,Vec(3.0,3.0),3.0))
    }

    @Test
    fun overlapRectangle2RectangleNoOverlap() {
        assertFalse(Physics.overlapRectangle(Vec(2.5,2.5),3.0,Vec(5.6,5.6),3.0))
    }

    @Test
    fun overlapRectangle2RectangleOverlapBorder() {
        assertTrue(Physics.overlapRectangle(Vec(2.5,2.5),3.0,Vec(5.5,5.5),3.0))
    }

    @Test
    fun overlapRectangle2RectangleOverlap() {
        val result= Physics.overlapRectangle(Vec(2.5,2.5),3.0,Vec(5.0,5.0),3.0)
        assertTrue(result)
    }

    @Test
    fun overlapRectangle2RectangleIntersectionBorderNoOverlap() {
        assertFalse(Physics.overlapRectangle(Vec(0.5,9.5),3.0,Vec(15.0,5.0),3.0))
    }

    @Test
    fun overlapRectangle2RectangleIntersectionBorderYesOverlap() {
        assertTrue(Physics.overlapRectangle(Vec(0.5,9.5),3.0,Vec(15.0,7.5),3.0))
    }

    @Test
    fun overlapRectangle2RectangleEqualsYesOverlap() {
        assertTrue(Physics.overlapRectangle(Vec(3.0,3.0),3.0,Vec(3.0,3.0),3.0))
    }

    @Test
    fun overlapCircle2CircleNoOverlap() {
        assertFalse(Physics.overlapCircle(Vec(2.5,2.5),3.0,Vec(5.5,5.5),3.0))
    }

    @Test
    fun overlapCircle2CircleOverlapBorder() {
        assertFalse(Physics.overlapCircle(Vec(2.5,2.5),3.0,Vec(5.5,5.5),3.0))
    }

    @Test
    fun overlapCircle2CircleOverlap() {
        val result= Physics.overlapCircle(Vec(2.5,2.5),3.0,Vec(4.0,4.0),3.0)
        assertTrue(result)
    }

    @Test
    fun overlapCircle2CircleIntersectionBorderNoOverlap() {
        assertFalse(Physics.overlapCircle(Vec(0.5,9.5),3.0,Vec(15.0,5.0),3.0))
    }

    @Test
    fun overlapCircle2CircleIntersectionBorderYesOverlap() {
        assertTrue(Physics.overlapCircle(Vec(0.5,9.5),3.0,Vec(15.0,7.5),3.0))
    }

    @Test
    fun overlapCircle2CircleEqualsYesOverlap() {
        assertTrue(Physics.overlapCircle(Vec(3.0,3.0),3.0,Vec(3.0,3.0),3.0))
    }

}