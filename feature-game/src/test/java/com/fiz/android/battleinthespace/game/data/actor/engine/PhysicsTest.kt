package com.fiz.android.battleinthespace.game.data.actor.engine

import com.fiz.feature.game.engine.Physics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PhysicsTest {
    val width = 20
    val height = 20

    @Before
    fun setUp() {
        Physics.createWorld(width, height)
    }

    @Test
    fun overlap2RectangleNoOverlap() {
        assertFalse(
            Physics.overlap(
                com.fiz.battleinthespace.common.Vec(
                    2.5,
                    2.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(5.6, 5.6), 3.0
            )
        )
    }

    @Test
    fun overlap2RectangleOverlapBorder() {
        assertTrue(
            Physics.overlap(
                com.fiz.battleinthespace.common.Vec(
                    2.5,
                    2.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(5.5, 5.5), 3.0
            )
        )
    }

    @Test
    fun overlap2RectangleOverlap() {
        assertTrue(
            Physics.overlap(
                com.fiz.battleinthespace.common.Vec(
                    2.5,
                    2.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(5.0, 5.0), 3.0
            )
        )
    }

    @Test
    fun overlap2RectangleIntersectionBorderNoOverlap() {
        assertFalse(
            Physics.overlap(
                com.fiz.battleinthespace.common.Vec(
                    0.5,
                    9.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(width.toDouble(), 5.0), 3.0
            )
        )
    }

    @Test
    fun overlap2RectangleIntersectionBorderYesOverlap() {
        assertTrue(
            Physics.overlap(
                com.fiz.battleinthespace.common.Vec(
                    0.5,
                    9.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(width.toDouble(), 7.5), 3.0
            )
        )
    }

    @Test
    fun overlap2RectangleEqualsYesOverlap() {
        assertTrue(
            Physics.overlap(
                com.fiz.battleinthespace.common.Vec(
                    3.0,
                    3.0
                ), 3.0, com.fiz.battleinthespace.common.Vec(3.0, 3.0), 3.0
            )
        )
    }

    @Test
    fun overlapRectangle2RectangleNoOverlap() {
        assertFalse(
            Physics.overlapRectangle(
                com.fiz.battleinthespace.common.Vec(
                    2.5,
                    2.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(5.6, 5.6), 3.0
            )
        )
    }

    @Test
    fun overlapRectangle2RectangleOverlapBorder() {
        assertTrue(
            Physics.overlapRectangle(
                com.fiz.battleinthespace.common.Vec(
                    2.5,
                    2.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(5.5, 5.5), 3.0
            )
        )
    }

    @Test
    fun overlapRectangle2RectangleOverlap() {
        val result = Physics.overlapRectangle(
            com.fiz.battleinthespace.common.Vec(
                2.5,
                2.5
            ), 3.0, com.fiz.battleinthespace.common.Vec(5.0, 5.0), 3.0
        )
        assertTrue(result)
    }

    @Test
    fun overlapRectangle2RectangleIntersectionBorderNoOverlap() {
        assertFalse(
            Physics.overlapRectangle(
                com.fiz.battleinthespace.common.Vec(
                    0.5,
                    9.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(width.toDouble(), 5.0), 3.0
            )
        )
    }

    @Test
    fun overlapRectangle2RectangleIntersectionBorderYesOverlap() {
        assertTrue(
            Physics.overlapRectangle(
                com.fiz.battleinthespace.common.Vec(
                    0.5,
                    9.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(width.toDouble(), 7.5), 3.0
            )
        )
    }

    @Test
    fun overlapRectangle2RectangleEqualsYesOverlap() {
        assertTrue(
            Physics.overlapRectangle(
                com.fiz.battleinthespace.common.Vec(
                    3.0,
                    3.0
                ), 3.0, com.fiz.battleinthespace.common.Vec(3.0, 3.0), 3.0
            )
        )
    }

    @Test
    fun overlapCircle2CircleNoOverlap() {
        assertFalse(
            Physics.overlapCircle(
                com.fiz.battleinthespace.common.Vec(
                    2.5,
                    2.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(5.5, 5.5), 3.0
            )
        )
    }

    @Test
    fun overlapCircle2CircleOverlapBorder() {
        assertFalse(
            Physics.overlapCircle(
                com.fiz.battleinthespace.common.Vec(
                    2.5,
                    2.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(5.5, 5.5), 3.0
            )
        )
    }

    @Test
    fun overlapCircle2CircleOverlap() {
        val result = Physics.overlapCircle(
            com.fiz.battleinthespace.common.Vec(
                2.5,
                2.5
            ), 3.0, com.fiz.battleinthespace.common.Vec(4.0, 4.0), 3.0
        )
        assertTrue(result)
    }

    @Test
    fun overlapCircle2CircleIntersectionBorderNoOverlap() {
        assertFalse(
            Physics.overlapCircle(
                com.fiz.battleinthespace.common.Vec(
                    0.5,
                    9.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(width.toDouble(), 5.0), 3.0
            )
        )
    }

    @Test
    fun overlapCircle2CircleIntersectionBorderYesOverlap() {
        assertTrue(
            Physics.overlapCircle(
                com.fiz.battleinthespace.common.Vec(
                    0.5,
                    9.5
                ), 3.0, com.fiz.battleinthespace.common.Vec(width.toDouble(), 7.5), 3.0
            )
        )
    }

    @Test
    fun overlapCircle2CircleEqualsYesOverlap() {
        assertTrue(
            Physics.overlapCircle(
                com.fiz.battleinthespace.common.Vec(
                    3.0,
                    3.0
                ), 3.0, com.fiz.battleinthespace.common.Vec(3.0, 3.0), 3.0
            )
        )
    }

    @Test
    fun findDistance() {
        val center1 = com.fiz.battleinthespace.common.Vec(18.0, 5.0)
        val center2 = com.fiz.battleinthespace.common.Vec(1.0, 6.0)
        val result = Physics.findDistance(center1, center2)
        assertEquals(3.16, result, 0.01)
    }

    @Test
    fun findAngle1() {
        val center1 = com.fiz.battleinthespace.common.Vec(18.0, 5.0)
        val center2 = com.fiz.battleinthespace.common.Vec(1.0, 6.0)
        val result = Physics.findAngle(center1, center2)
        assertEquals(18.43, result, 0.01)
    }

    @Test
    fun findAngle2() {
        val center1 = com.fiz.battleinthespace.common.Vec(1.0, 3.0)
        val center2 = com.fiz.battleinthespace.common.Vec(3.0, 2.0)
        val result = Physics.findAngle(center1, center2)
        assertEquals(-26.56, result, 0.01)
    }
}