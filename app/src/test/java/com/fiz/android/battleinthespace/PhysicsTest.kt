package com.fiz.android.battleinthespace

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
        assertFalse(Physics.overlap(2.5,2.5,3.0,5.6,5.6,3.0))
    }

    @Test
    fun overlap2RectangleOverlapBorder() {
        assertTrue(Physics.overlap(2.5,2.5,3.0,5.5,5.5,3.0))
    }

    @Test
    fun overlap2RectangleOverlap() {
        assertTrue(Physics.overlap(2.5,2.5,3.0,5.0,5.0,3.0))
    }

    @Test
    fun overlap2RectangleIntersectionBorderNoOverlap() {
        assertFalse(Physics.overlap(0.5,9.5,3.0,15.0,5.0,3.0))
    }

    @Test
    fun overlap2RectangleIntersectionBorderYesOverlap() {
        assertTrue(Physics.overlap(0.5,9.5,3.0,15.0,7.5,3.0))
    }

    @Test
    fun overlap2RectangleEqualsYesOverlap() {
        assertTrue(Physics.overlap(3.0,3.0,3.0,3.0,3.0,3.0))
    }

    @Test
    fun overlapRectangle2RectangleNoOverlap() {
        assertFalse(Physics.overlapRectangle(2.5,2.5,3.0,5.6,5.6,3.0))
    }

    @Test
    fun overlapRectangle2RectangleOverlapBorder() {
        assertTrue(Physics.overlapRectangle(2.5,2.5,3.0,5.5,5.5,3.0))
    }

    @Test
    fun overlapRectangle2RectangleOverlap() {
        val result=Physics.overlapRectangle(2.5,2.5,3.0,5.0,5.0,3.0)
        assertTrue(result)
    }

    @Test
    fun overlapRectangle2RectangleIntersectionBorderNoOverlap() {
        assertFalse(Physics.overlapRectangle(0.5,9.5,3.0,15.0,5.0,3.0))
    }

    @Test
    fun overlapRectangle2RectangleIntersectionBorderYesOverlap() {
        assertTrue(Physics.overlapRectangle(0.5,9.5,3.0,15.0,7.5,3.0))
    }

    @Test
    fun overlapRectangle2RectangleEqualsYesOverlap() {
        assertTrue(Physics.overlapRectangle(3.0,3.0,3.0,3.0,3.0,3.0))
    }

    @Test
    fun overlapCircle2CircleNoOverlap() {
        assertFalse(Physics.overlapCircle(2.5,2.5,3.0,5.5,5.5,3.0))
    }

    @Test
    fun overlapCircle2CircleOverlapBorder() {
        assertFalse(Physics.overlapCircle(2.5,2.5,3.0,5.5,5.5,3.0))
    }

    @Test
    fun overlapCircle2CircleOverlap() {
        val result=Physics.overlapCircle(2.5,2.5,3.0,4.0,4.0,3.0)
        assertTrue(result)
    }

    @Test
    fun overlapCircle2CircleIntersectionBorderNoOverlap() {
        assertFalse(Physics.overlapCircle(0.5,9.5,3.0,15.0,5.0,3.0))
    }

    @Test
    fun overlapCircle2CircleIntersectionBorderYesOverlap() {
        assertTrue(Physics.overlapCircle(0.5,9.5,3.0,15.0,7.5,3.0))
    }

    @Test
    fun overlapCircle2CircleEqualsYesOverlap() {
        assertTrue(Physics.overlapCircle(3.0,3.0,3.0,3.0,3.0,3.0))
    }

}