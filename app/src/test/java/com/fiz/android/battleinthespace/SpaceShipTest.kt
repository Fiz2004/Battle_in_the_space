package com.fiz.android.battleinthespace

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

internal class SpaceShipTest {
    @Before
    fun setUp(){

    }

    @Test
    fun moveRightPerSecondByStartZero() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,0.0,true)
        spaceShip.moveRight(1000)
        assertEquals(spaceShip.angle,200.0,0.1)
    }

    @Test
    fun moveRightPerSecondByStartHighLimit() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,200.0,true)
        spaceShip.moveRight(1000)
        assertEquals(spaceShip.angle,40.0,0.1)
    }

    @Test
    fun moveLeftPerSecondByStartZero() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,0.0,true)
        spaceShip.moveLeft(1000)
        assertEquals(spaceShip.angle,160.0,0.1)
    }

    @Test
    fun moveLeftPerSecondByStartLowLimit() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,40.0,true)
        spaceShip.moveLeft(1000)
        assertEquals(spaceShip.angle,200.0,0.1)
    }

    @Test
    fun moveUpPerSecondByAngleZero() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,0.0,true)
        spaceShip.moveUp(1000)
        assertEquals(spaceShip.speedX,0.8,0.1)
        assertEquals(spaceShip.speedY,0.0,0.1)
    }

    @Test
    fun moveUpPerSecondByAngle45() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,45.0,true)
        spaceShip.moveUp(1000)
        assertEquals(spaceShip.speedX,0.5656,0.001)
        assertEquals(spaceShip.speedY,0.5656,0.001)
    }

    @Test
    fun moveUpPerSecondByAngle180() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,180.0,true)
        spaceShip.moveUp(1000)
        assertEquals(spaceShip.speedX,-0.8,0.1)
        assertEquals(spaceShip.speedY,0.0,0.1)
    }

    @Test
    fun moveUpPerSecondByAngle90() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,90.0,true)
        spaceShip.moveUp(1000)
        assertEquals(spaceShip.speedX,0.0,0.1)
        assertEquals(spaceShip.speedY,0.8,0.1)
    }


    @Test
    fun moveUpPerSecondByAngle270() {
        val spaceShip=SpaceShip(5.0,5.0,0.0,0.0,270.0,true)
        spaceShip.moveUp(1000)
        assertEquals(spaceShip.speedX,0.0,0.1)
        assertEquals(spaceShip.speedY,-0.8,0.1)
    }
}