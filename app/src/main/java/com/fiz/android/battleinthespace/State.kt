package com.fiz.android.battleinthespace

import android.content.SharedPreferences
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val speedSpaceShipMax = 500.0 / 1000
private const val speedMeteoriteMax = 100.0 / 1000
private const val speedBulletMax = 4.0

class State(
    val width: Double,
    val height: Double,
    _settings: SharedPreferences
) {
    //  private val settings = _settings
    var countPlayers: Int = 4

    var spaceShips: MutableList<SpaceShip> = mutableListOf()
    private var scores: MutableList<Int> = mutableListOf()
    var lifes: MutableList<Int> = mutableListOf()
    var bullets: MutableList<Bullet> = mutableListOf()
    var meteorites: MutableList<Meteorite> = mutableListOf()
    var backgrounds: MutableList<MutableList<Int>> = mutableListOf()
    var animationBulletDestroys: MutableList<AnimationBulletDestroy> = mutableListOf()
    var animationSpaceShipDestroys: MutableList<AnimationSpaceShipDestroy> = mutableListOf()
    private var respawns: MutableList<Respawn> = mutableListOf(
        Respawn(
            centerX = 2.0,
            centerY = 2.0,
            angle = 0.0
        ),
        Respawn(
            centerX = 13.0,
            centerY = 2.0,
            angle = 180.0
        ),
        Respawn(
            centerX = 2.0,
            centerY = 13.0,
            angle = 0.0
        ),
        Respawn(
            centerX = 13.0,
            centerY = 13.0,
            angle = 180.0
        )
    )

    private var round: Int = 1

    var skipFrames: Int = 0
    var skipFrames1: Int = 0
    var status = "playing"
    private var pauseTime: Long = System.currentTimeMillis()

    init {
        newGame()
    }

    private fun newGame() {
        backgrounds.clear()
        for (n in 0 until height.toInt()) {
            val row: MutableList<Int> = mutableListOf()
            for (k in 0 until width.toInt())
                row += (0..7).shuffled().first()
            backgrounds += row
        }

        spaceShips.clear()
        for (n in 0 until countPlayers) {
            spaceShips += SpaceShip(
                centerX = respawns[n].centerX,
                centerY = respawns[n].centerY,
                angle = respawns[n].angle,
            )
        }

        scores.clear()
        for (n in 0 until countPlayers) {
            scores += 0
        }

        lifes.clear()
        for (n in 0 until countPlayers) {
            lifes += 3
        }

        meteorites.clear()
        for (n in 0..0) {
            val angle = (0..360).shuffled().first()
            meteorites += Meteorite(
                centerX = 8.0,
                centerY = 6.0,
                angle = angle.toDouble(),
                speedX = +speedMeteoriteMax * cos(angle / 180 * Math.PI),
                speedY = -speedMeteoriteMax * sin(angle / 180 * Math.PI),
                size = 1.0,
                viewSize = 0,
                view = 0
            )
        }

        round = 1

        animationBulletDestroys.clear()
        animationSpaceShipDestroys.clear()
    }

    private fun newRound() {
        round += 1

        spaceShips.clear()
        for (n in 0 until countPlayers) {
            spaceShips += SpaceShip(
                centerX = respawns[n].centerX,
                centerY = respawns[n].centerY,
                angle = respawns[n].angle,
            )
        }

        lifes.clear()
        for (n in 0 until countPlayers) {
            lifes += 2
        }

        meteorites.clear()
        for (n in 0..1) {
            if (n == 0) {
                val angle = (0..360).shuffled().first()
                meteorites += Meteorite(
                    centerX = 8.0,
                    centerY = 6.0,
                    speedX = +speedMeteoriteMax * cos(angle / 180 * Math.PI),
                    speedY = -speedMeteoriteMax * sin(angle / 180 * Math.PI),
                    angle = angle.toDouble(),
                    size = 1.0,
                    viewSize = 0,
                    view = round - 1
                )
            }
            if (n == 1) {
                val angle = (0..360).shuffled().first()
                meteorites += Meteorite(
                    centerX = 8.0,
                    centerY = 6.0,
                    speedX = +speedMeteoriteMax * cos(angle / 180 * Math.PI),
                    speedY = -speedMeteoriteMax * sin(angle / 180 * Math.PI),
                    angle = angle.toDouble(),
                    size = 1.0,
                    viewSize = 0,
                    view = round - 1
                )
            }
        }

        backgrounds.clear()
        for (n in 0 until 30) {
            val row: MutableList<Int> = mutableListOf()
            for (k in 0 until 30)
                row += (0..7).shuffled().first()
            backgrounds += row
        }
    }

    fun update(controller: Array<Controller>, deltaTime: Int): Boolean {
        for (player in 0 until countPlayers) {
            if (controller[player].up)
                updateMoveUp(player, deltaTime)
            if (controller[player].down)
                updateMoveDown(player, deltaTime)
            if (controller[player].right)
                updateMoveRight(player, deltaTime)
            if (controller[player].left)
                updateMoveLeft(player, deltaTime)
            if (controller[player].fire) {
                if (controller[player].timeLastFire == 0) {
                    updatePressFire(player, deltaTime)
                    controller[player].timeLastFire = 500
                } else {
                    controller[player].timeLastFire -= deltaTime
                    if (controller[player].timeLastFire < 0)
                        controller[player].timeLastFire = 0
                }
            }
        }

        updateSpaceShips(deltaTime)
        updateBullets(deltaTime)
        updateMeteorites(deltaTime)

        animationBulletDestroys.forEach {
            it.numberFrame+=1
            it.timeShow-=deltaTime
        }
        animationBulletDestroys=animationBulletDestroys.filter {
            it.timeShow>0
        }.toMutableList()
        animationSpaceShipDestroys.forEach {
            it.numberFrame+=1
            it.timeShow-=deltaTime
        }
        animationSpaceShipDestroys=animationSpaceShipDestroys.filter {
            it.timeShow>0
        }.toMutableList()

        if (lifes.none { it > 0 } || meteorites.isEmpty()) {
            newRound()
        }

        if (round == 11)
            return false

        return true
    }

    private fun updateMoveUp(numberPlayer: Int, deltaTime: Int) {
        spaceShips[numberPlayer].moveUp(deltaTime)
    }

    private fun updateMoveDown(numberPlayer: Int, deltaTime: Int) {
        spaceShips[numberPlayer].moveDown(deltaTime)
    }

    private fun updateMoveRight(numberPlayer: Int, deltaTime: Int) {
        spaceShips[numberPlayer].moveRight(deltaTime)
    }

    private fun updateMoveLeft(numberPlayer: Int, deltaTime: Int) {
        spaceShips[numberPlayer].moveLeft(deltaTime)
    }

    private fun updatePressFire(numberPlayer: Int, deltaTime: Int) {
        if (spaceShips[numberPlayer].inGame) {
            bullets += Bullet(
                centerX = spaceShips[numberPlayer].centerX + 1 * cos(spaceShips[numberPlayer].angle / 180 * Math.PI),
                centerY = spaceShips[numberPlayer].centerY + 1 * sin(spaceShips[numberPlayer].angle / 180 * Math.PI),
                speedX = speedBulletMax * cos(spaceShips[numberPlayer].angle / 180 * Math.PI),
                speedY = speedBulletMax * sin(spaceShips[numberPlayer].angle / 180 * Math.PI),
                angle = 0.0,// TODO Возможно не требуется проверить
                roadLength = 0.0,
                player = numberPlayer
            )
        }
    }

    private fun updateSpaceShips(deltaTime: Int) {
        for (spaceShip in spaceShips)
            if (spaceShip.inGame) {
                spaceShip.update(deltaTime, width, height)
            } else {
                val numberPlayer = spaceShips.indexOf(spaceShip)
                if (lifes[numberPlayer] > 0)
                    for (k in spaceShips.indices)
                        if (numberPlayer == k) {
                            for (z in 0..3) {
                                if (isRespawnFree(z, k)) {
                                    spaceShips[k].centerX = respawns[z].centerX
                                    spaceShips[k].centerY = respawns[z].centerY
                                    spaceShips[k].speedX = 0.0
                                    spaceShips[k].speedY = 0.0
                                    spaceShips[k].angle = respawns[z].angle
                                    spaceShips[k].inGame = true
                                    break
                                }
                            }
                        }
            }
        collisionSpaceShips()
    }

    fun overlap(actor1: Actor, actor2: Actor): Boolean {
        val size1 = (actor1.size / 2)
        val size2 = (actor2.size / 2)
        val L1 = changeXifBorder(actor1.centerX - size1)
        val L2 = changeXifBorder(actor2.centerX - size2)
        val R1 = changeXifBorder(actor1.centerX + size1)
        val R2 = changeXifBorder(actor2.centerX + size2)
        val U1 = changeYifBorder(actor1.centerY - size1)
        val U2 = changeYifBorder(actor2.centerY - size2)
        val D1 = changeYifBorder(actor1.centerY + size1)
        val D2 = changeYifBorder(actor2.centerY + size2)
        val LUA=L1>L2&&L1<R2&&U1>U2&&U1<D2
        val RUA=R1>L2&&R1<R2&&U1>U2&&U1<D2
        val LDA=L1>L2&&L1<R2&&D1>U2&&D1<D2
        val RDA=R1>L2&&R1<R2&&D1>U2&&D1<D2

        return LUA||RUA||LDA||RDA
    }

    private fun isRespawnFree(z: Int, k: Int): Boolean {
        for (n in 0 until countPlayers)
            if (n != k)
                if (overlap(respawns[z], spaceShips[n]))
                    return false
        for (n in 0 until bullets.size)
                if (overlap(respawns[z], bullets[n]))
                    return false
        for (n in 0 until meteorites.size)
                if (overlap(respawns[z], meteorites[n]))
                    return false
        return true
    }

    private fun collisionSpaceShips() {
        var speedSpaceShipX1: Double
        var speedSpaceShipX2: Double
        var speedSpaceShipY1: Double
        var speedSpaceShipY2: Double

        if (countPlayers > 1) {
            for (n in 0 until (countPlayers - 1))
                for (k in (n + 1) until countPlayers)
                    if (spaceShips[n].inGame && spaceShips[k].inGame)
                        if (overlap(spaceShips[n],spaceShips[k])) {
                            speedSpaceShipX1 = spaceShips[n].speedX
                            speedSpaceShipX2 = spaceShips[k].speedX
                            speedSpaceShipY1 = spaceShips[n].speedY
                            speedSpaceShipY2 = spaceShips[k].speedY
                            if (((speedSpaceShipX1 > 0) && (speedSpaceShipX2 < 0)) || ((speedSpaceShipX1 < 0) && (speedSpaceShipX2 > 0))) {
                                spaceShips[n].speedX = -speedSpaceShipX1
                                spaceShips[k].speedX = -speedSpaceShipX2
                            } else {
                                spaceShips[n].speedX = (speedSpaceShipX1 + speedSpaceShipX2) / 2
                                spaceShips[k].speedX = (speedSpaceShipX1 + speedSpaceShipX2) / 2
                                if (abs(speedSpaceShipX1) > abs(speedSpaceShipX2))
                                    spaceShips[k].speedX = 2 * spaceShips[k].speedX
                                else
                                    if (abs(speedSpaceShipX1) < abs(speedSpaceShipX2))
                                        spaceShips[n].speedX = 2 * spaceShips[n].speedX
                            }
                            if (spaceShips[n].speedX > speedSpaceShipMax)
                                spaceShips[n].speedX = speedSpaceShipMax
                            if (spaceShips[k].speedX > speedSpaceShipMax)
                                spaceShips[k].speedX = speedSpaceShipMax
                            if (spaceShips[n].speedX < -speedSpaceShipMax)
                                spaceShips[n].speedX = -speedSpaceShipMax
                            if (spaceShips[k].speedX < -speedSpaceShipMax)
                                spaceShips[k].speedX = -speedSpaceShipMax
                            if ((speedSpaceShipY1 > 0 && speedSpaceShipY2 < 0) || (speedSpaceShipY1 < 0 && speedSpaceShipY2 > 0)) {
                                spaceShips[n].speedY = -speedSpaceShipY1
                                spaceShips[k].speedY = -speedSpaceShipY2
                            } else {
                                spaceShips[n].speedY = (speedSpaceShipY1 + speedSpaceShipY2) / 2
                                spaceShips[k].speedY = (speedSpaceShipY1 + speedSpaceShipY2) / 2
                                if (abs(speedSpaceShipY1) > abs(speedSpaceShipY2))
                                    spaceShips[k].speedY *= 2
                                else
                                    if (abs(speedSpaceShipY1) < abs(speedSpaceShipY2))
                                        spaceShips[n].speedY *= 2
                            }
                            if (spaceShips[n].speedY > speedSpaceShipMax)
                                spaceShips[n].speedY = speedSpaceShipMax
                            if (spaceShips[k].speedY > speedSpaceShipMax)
                                spaceShips[k].speedY = speedSpaceShipMax
                            if (spaceShips[n].speedY < -speedSpaceShipMax)
                                spaceShips[n].speedY = -speedSpaceShipMax
                            if (spaceShips[k].speedY < -speedSpaceShipMax)
                                spaceShips[k].speedY = -speedSpaceShipMax
                        }
        }
    }

    private fun updateBullets(deltaTime: Int) {
        if (bullets.isNotEmpty()) {
            for (bullet in bullets)
                bullet.update(deltaTime, width, height)

            collisionBulletSpaceShips()
            collisionBulletBullet()

            val tempBullet: MutableList<Bullet> = mutableListOf()
            for (n in 0 until bullets.size)
                if (bullets[n].roadLength <= 6)
                    tempBullet += bullets[n]
            bullets = tempBullet
        }
    }

    private fun collisionBulletSpaceShips() {
        var speedSpaceShipX1: Double
        var speedBulletX2: Double
        var speedSpaceShipY1: Double
        var speedBulletY2: Double
        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        if (bullets.size > 0) {
            for (n in 0 until countPlayers)
                if (spaceShips[n].inGame)
                    for (k in 0 until bullets.size)
                        if (overlap(bullets[k], spaceShips[n])&&n != bullets[k].player) {
                            speedSpaceShipX1 = spaceShips[n].speedX
                            speedBulletX2 = bullets[k].speedX
                            speedSpaceShipY1 = spaceShips[n].speedY
                            speedBulletY2 = bullets[k].speedY
                            if (((speedSpaceShipX1 > 0) && (speedBulletX2 < 0)) || ((speedSpaceShipX1 < 0) && (speedBulletX2 > 0)))
                                spaceShips[n].speedX = -speedSpaceShipX1
                            else {
                                spaceShips[n].speedX = (speedSpaceShipX1 + speedBulletX2) / 2
                                spaceShips[n].speedX = 2 * spaceShips[n].speedX
                            }
                            if (spaceShips[n].speedX > speedSpaceShipMax)
                                spaceShips[n].speedX = speedSpaceShipMax
                            if (spaceShips[n].speedX < -speedSpaceShipMax)
                                spaceShips[n].speedX = -speedSpaceShipMax
                            if (((speedSpaceShipY1 > 0) && (speedBulletY2 < 0)) || ((speedSpaceShipY1 < 0) && (speedBulletY2 > 0)))
                                spaceShips[n].speedY = -speedSpaceShipY1
                            else {
                                spaceShips[n].speedY = (speedSpaceShipY1 + speedBulletY2) / 2
                                spaceShips[n].speedY = 2 * spaceShips[n].speedY
                            }
                            if (spaceShips[n].speedY > speedSpaceShipMax)
                                spaceShips[n].speedY = speedSpaceShipMax
                            if (spaceShips[n].speedY < -speedSpaceShipMax)
                                spaceShips[n].speedY = (-speedSpaceShipMax)
                            animationBulletDestroys.add(
                                AnimationBulletDestroy(
                                    centerX = bullets[k].centerX,
                                    centerY = bullets[k].centerY,
                                    numberFrame = 0
                                )
                            )
                            listBulletDestroy.add(bullets[k])
                        }

            for (bullet in listBulletDestroy) {
                bullets.remove(bullet)
            }
        }
    }

    private fun collisionBulletBullet() {
        val listBulletDestroy: MutableList<Bullet> = mutableListOf()

        for (n in 0 until (bullets.size - 1))
            for (k in (n + 1) until bullets.size)
                if (overlap(bullets[n], bullets[k])){
                    animationBulletDestroys.add(
                        AnimationBulletDestroy(
                            centerX = bullets[k].centerX,
                            centerY = bullets[k].centerY,
                            numberFrame = 0
                        )
                    )
                    listBulletDestroy.add(bullets[k])
                }

        for (bullet in listBulletDestroy) {
            bullets.remove(bullet)
        }
    }

    private fun updateMeteorites(deltaTime: Int) {
        if (meteorites.isNotEmpty()) {
            for (meteorite in meteorites)
                meteorite.update(deltaTime, width, height)

            collisionMeteoriteMeteorite()
        }
    }

    private fun collisionMeteoriteMeteorite() {
        var speedMeteoriteX1: Double
        var speedMeteoriteX2: Double
        var speedMeteoriteY1: Double
        var speedMeteoriteY2: Double

        for (n in 0 until (meteorites.size - 1))
            for (k in (n + 1) until meteorites.size)
                if (overlap(meteorites[n], meteorites[k])) {
                    speedMeteoriteX1 = meteorites[n].speedX
                    speedMeteoriteX2 = meteorites[k].speedX
                    speedMeteoriteY1 = meteorites[n].speedY
                    speedMeteoriteY2 = meteorites[k].speedY
                    if (((speedMeteoriteX1 > 0) && (speedMeteoriteX2 < 0)) || ((speedMeteoriteX1 < 0) && (speedMeteoriteX2 > 0))) {
                        meteorites[n].speedX = -speedMeteoriteX1
                        meteorites[k].speedX = -speedMeteoriteX2
                    } else {
                        meteorites[n].speedX = (speedMeteoriteX1 + speedMeteoriteX2) / 2
                        meteorites[k].speedX = (speedMeteoriteX1 + speedMeteoriteX2) / 2
                        if (abs(speedMeteoriteX1) > abs(speedMeteoriteX2))
                            meteorites[k].speedX *= 2
                        else
                            if (abs(speedMeteoriteX1) < abs(speedMeteoriteX2))
                                meteorites[n].speedX *= 2
                    }
                    if (meteorites[n].speedX > speedMeteoriteMax)
                        meteorites[n].speedX = speedMeteoriteMax
                    if (meteorites[k].speedX > speedMeteoriteMax)
                        meteorites[k].speedX = speedMeteoriteMax
                    if (meteorites[n].speedX < -speedMeteoriteMax)
                        meteorites[n].speedX = -speedMeteoriteMax
                    if (meteorites[k].speedX < -speedMeteoriteMax)
                        meteorites[k].speedX = -speedMeteoriteMax
                    if (((speedMeteoriteY1 > 0) && (speedMeteoriteY2 < 0)) || ((speedMeteoriteY1 < 0) && (speedMeteoriteY2 > 0))) {
                        meteorites[n].speedY = -speedMeteoriteY1
                        meteorites[k].speedY = -speedMeteoriteY2
                    } else {
                        meteorites[n].speedY = (speedMeteoriteY1 + speedMeteoriteY2) / 2
                        meteorites[k].speedY = (speedMeteoriteY1 + speedMeteoriteY2) / 2
                        if (abs(speedMeteoriteY1) > abs(speedMeteoriteY2))
                            meteorites[k].speedY *= 2
                        else
                            if (abs(speedMeteoriteY1) < abs(speedMeteoriteY2))
                                meteorites[n].speedY *= 2
                    }
                    if (meteorites[n].speedY > speedMeteoriteMax)
                        meteorites[n].speedY = speedMeteoriteMax
                    if (meteorites[k].speedY > speedMeteoriteMax)
                        meteorites[k].speedY = speedMeteoriteMax
                    if (meteorites[n].speedY < -speedMeteoriteMax)
                        meteorites[n].speedY = -speedMeteoriteMax
                    if (meteorites[k].speedY < -speedMeteoriteMax)
                        meteorites[k].speedY = -speedMeteoriteMax
                }

        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        val listMeteoritesDestroy: MutableList<Meteorite> = mutableListOf()
        if (bullets.size >= 0)
            if (meteorites.size >= 0) {
                for (n in 0 until meteorites.size)
                    for (k in 0 until bullets.size)
                        if (overlap(bullets[k], meteorites[n])) {
                            meteorites[n].size = meteorites[n].size - 5
                            meteorites[n].viewSize = meteorites[n].viewSize + 1
                            scores[bullets[k].player] =
                                scores[bullets[k].player] + 100 * meteorites[n].viewSize
                            if (meteorites[n].viewSize > 3) {
                                listMeteoritesDestroy.add(meteorites[n])


                                animationBulletDestroys.add(
                                    AnimationBulletDestroy(
                                        centerX = bullets[k].centerX,
                                        centerY = bullets[k].centerY,
                                        numberFrame = 0
                                    )
                                )
                                listBulletDestroy.add(bullets[k])
                            } else {

                                meteorites.add(
                                    Meteorite(
                                        centerX = meteorites[n].centerX + (meteorites[n].size + 10),
                                        centerY = meteorites[n].centerY + (meteorites[n].size + 10),
                                        angle = meteorites[n].angle - 120,
                                        speedX = +speedMeteoriteMax * cos(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                        speedY = -speedMeteoriteMax * sin(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                        size = meteorites[n].size,
                                        viewSize = meteorites[n].viewSize,
                                        view = meteorites[n].view,
                                    )
                                )

                                meteorites.add(
                                    Meteorite(
                                        centerX = meteorites[n].centerX + (meteorites[n].size + 10),
                                        centerY = meteorites[n].centerY + (meteorites[n].size + 10),
                                        angle = meteorites[n].angle - 240,
                                        speedX = +speedMeteoriteMax * cos(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                        speedY = -speedMeteoriteMax * sin(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                        size = meteorites[n].size,
                                        viewSize = meteorites[n].viewSize,
                                        view = meteorites[n].view,
                                    )
                                )

                            }
                            animationBulletDestroys.add(
                                AnimationBulletDestroy(
                                    centerX = bullets[k].centerX,
                                    centerY = bullets[k].centerY,
                                    numberFrame = 0
                                )
                            )
                            listBulletDestroy.add(bullets[k])
                        }
            }
        for (bullet in listBulletDestroy) {
            bullets.remove(bullet)
        }

        for (n in 0 until countPlayers)
            for (k in 0 until meteorites.size)
                if (spaceShips[n].inGame)
                    if (overlap(spaceShips[n], meteorites[k])) {
                        meteorites[k].size = meteorites[k].size - 5
                        meteorites[k].viewSize = meteorites[k].viewSize + 1
                        lifes[n] = lifes[n] - 1
                        spaceShips[n].inGame = false
                        if (meteorites[k].viewSize > 3) {
                            listMeteoritesDestroy.add(meteorites[k])
                        } else {

                            meteorites.add(
                                Meteorite(
                                    centerX = meteorites[k].centerX,
                                    centerY = meteorites[k].centerY,
                                    angle = meteorites[k].angle - 90,
                                    speedX = +speedMeteoriteMax * cos(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                    speedY = -speedMeteoriteMax * sin(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                    size = meteorites[k].size,
                                    viewSize = meteorites[k].viewSize,
                                    view = meteorites[k].view,
                                )
                            )

                            meteorites.add(
                                Meteorite(
                                    centerX = meteorites[k].centerX,
                                    centerY = meteorites[k].centerY,
                                    angle = meteorites[k].angle - 180,
                                    speedX = +speedMeteoriteMax * cos(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                    speedY = -speedMeteoriteMax * sin(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                    size = meteorites[k].size,
                                    viewSize = meteorites[k].viewSize,
                                    view = meteorites[k].view,
                                )
                            )

                        }

                        animationSpaceShipDestroys.add(
                            AnimationSpaceShipDestroy(
                                centerX = spaceShips[n].centerX,
                                centerY = spaceShips[n].centerY,
                                numberFrame = 0,
                            )
                        )
                    }

        for (meteorite in listMeteoritesDestroy) {
            meteorites.remove(meteorite)
        }
    }

    private fun changeXifBorder(coordinateCenterX: Double): Double {
        var result = coordinateCenterX
        if (coordinateCenterX > width)
            result = coordinateCenterX - width
        if (coordinateCenterX < 0)
            result = coordinateCenterX + width
        return result
    }

    private fun changeYifBorder(coordinateCenterY: Double): Double {
        var result = coordinateCenterY
        if (coordinateCenterY > height)
            result = coordinateCenterY - height
        if (coordinateCenterY < 0)
            result = coordinateCenterY + height
        return result
    }

    fun clickPause() {
        if (status == "playing")
            status = "pause"
         else
            status = "playing"
    }
}