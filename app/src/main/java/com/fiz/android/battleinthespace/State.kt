package com.fiz.android.battleinthespace

import android.content.SharedPreferences
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val speedSpaceShipMax= 500.0/1000
private const val speedMeteoriteMax = 100.0/1000
private const val speedBulletMax = 200.0/1000

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
            centerX = 1,
            centerY = 1,
            angle = 0
        ),
        Respawn(
            centerX = 15,
            centerY = 1,
            angle = 180
        ),
        Respawn(
            centerX = 1,
            centerY = 15,
            angle = 0
        ),
        Respawn(
            centerX = 15,
            centerY = 15,
            angle = 180
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
                centerX = respawns[n].centerX.toDouble(),
                centerY = respawns[n].centerY.toDouble(),
                angle = respawns[n].angle.toDouble(),
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
                sizePx = 0.5,
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
                centerX = respawns[n].centerX.toDouble(),
                centerY = respawns[n].centerY.toDouble(),
                angle = respawns[n].angle.toDouble(),
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
                    sizePx = 0.5,
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
                    sizePx = 0.5,
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

    fun update(controller: Array<Controller>,deltaTime: Int): Boolean {
        for (player in 0 until countPlayers) {
            if (controller[player].up)
                updateMoveUp(player,deltaTime)
            if (controller[player].down)
                updateMoveDown(player,deltaTime)
            if (controller[player].right)
                updateMoveRight(player,deltaTime)
            if (controller[player].left)
                updateMoveLeft(player,deltaTime)
            if (controller[player].fire) {
                if (controller[player].timeLastFire == 0) {
                    updatePressFire(player,deltaTime)
                    controller[player].timeLastFire = 500
                } else {
                    controller[player].timeLastFire -= deltaTime.toInt()
                    if (controller[player].timeLastFire < 0)
                        controller[player].timeLastFire = 0
                }
            }
        }

        updateSpaceShips(deltaTime)
        updateBullets(deltaTime)
        updateMeteorites(deltaTime)

        if (lifes.none { it > 0 } || meteorites.isEmpty()) {
            newRound()
        }

        if (round == 11)
            return false

        return true
    }

    private fun updateMoveUp(numberPlayer: Int,deltaTime: Int) {
        spaceShips[numberPlayer].moveUp(deltaTime)
    }

    private fun updateMoveDown(numberPlayer: Int,deltaTime: Int) {
        spaceShips[numberPlayer].moveDown(deltaTime)
    }

    private fun updateMoveRight(numberPlayer: Int,deltaTime: Int) {
        spaceShips[numberPlayer].moveRight(deltaTime)
    }

    private fun updateMoveLeft(numberPlayer: Int,deltaTime: Int) {
        spaceShips[numberPlayer].moveLeft(deltaTime)
    }

    private fun updatePressFire(numberPlayer: Int,deltaTime: Int) {
        if (spaceShips[numberPlayer].inGame) {
            bullets += Bullet(
                centerX = spaceShips[numberPlayer].centerX + (50*deltaTime/1000) * cos(spaceShips[numberPlayer].angle / 180 * Math.PI),
                centerY = spaceShips[numberPlayer].centerY - (50*deltaTime/1000) * sin(spaceShips[numberPlayer].angle / 180 * Math.PI),
                speedX = +speedBulletMax * cos(spaceShips[numberPlayer].angle / 180 * Math.PI),
                speedY = -speedBulletMax * sin(spaceShips[numberPlayer].angle / 180 * Math.PI),
                angle = 0.0,// TODO Возможно не требуется проверить
                roadLength = 0.0,
                player = numberPlayer
            )
        }
    }

    private fun updateSpaceShips(deltaTime: Int) {
        for (spaceShip in spaceShips)
            if (spaceShip.inGame) {
                spaceShip.update(deltaTime,width,height)
            } else {
                val numberPlayer=spaceShips.indexOf(spaceShip)
                if (lifes[numberPlayer] >= 0)
                    for (k in spaceShips.indices)
                        if (numberPlayer == k) {
                            for (z in 0..3) {
                                if (isRespawnFree(z, k, numberPlayer)) {
                                    spaceShips[k].centerX = respawns[z].centerX.toDouble()
                                    spaceShips[k].centerY = respawns[z].centerY.toDouble()
                                    spaceShips[k].speedX = 0.0
                                    spaceShips[k].speedY = 0.0
                                    spaceShips[k].angle = respawns[z].angle.toDouble()
                                    spaceShips[k].inGame = true
                                    break
                                }
                            }
                        }
            }
        collisionSpaceShips()
    }

    private fun isRespawnFree(z: Int, k: Int, player: Int): Boolean {
        for (n in 0 until countPlayers - 1)
            if (n != k)
                if ((respawns[z].centerX - 2 < spaceShips[n].centerX + 0.5) && (respawns[z].centerX + 2 > spaceShips[n].centerX + 0.5) &&
                    ((respawns[z].centerY - 2 < spaceShips[n].centerY + 0.5) && (respawns[z].centerY + 2 > spaceShips[n].centerY + 0.5) ||
                            (respawns[z].centerY - 2 < spaceShips[n].centerY - 0.5) && (respawns[z].centerY + 2 > spaceShips[n].centerY - 0.5)) ||
                    (respawns[z].centerX - 2 < spaceShips[n].centerX - 0.5) && (respawns[z].centerX + 2 > spaceShips[n].centerX - 0.5) &&
                    ((respawns[z].centerY - 2 < spaceShips[n].centerY + 0.5) && (respawns[z].centerY + 2 > spaceShips[n].centerY + 0.5) ||
                            (respawns[z].centerY - 2 < spaceShips[n].centerY - 0.5) && (respawns[z].centerY + 2 > spaceShips[n].centerY - 0.5))
                )
                    return false
        for (n in 0 until bullets.size)
            if (n != player)
                if ((respawns[z].centerX - 2 < bullets[n].centerX + 2) && (respawns[z].centerX + 2 > bullets[n].centerX + 2) &&
                    ((respawns[z].centerY - 2 < bullets[n].centerY + 2) && (respawns[z].centerY + 2 > bullets[n].centerY + 2) ||
                            (respawns[z].centerY - 2 < bullets[n].centerY - 2) && (respawns[z].centerY + 2 > bullets[n].centerY - 2)) ||
                    (respawns[z].centerX - 2 < bullets[n].centerX - 2) && (respawns[z].centerX + 2 > bullets[n].centerX - 2) &&
                    ((respawns[z].centerY - 2 < bullets[n].centerY + 2) && (respawns[z].centerY + 2 > bullets[n].centerY + 2) ||
                            (respawns[z].centerY - 2 < bullets[n].centerY - 2) && (respawns[z].centerY + 2 > bullets[n].centerY - 2))
                )
                    return false
        for (n in 0 until meteorites.size)
            if (n != player)
                if ((respawns[z].centerX - 2 < meteorites[n].centerX + meteorites[n].sizePx) && (respawns[z].centerX + 2 > meteorites[n].centerX + meteorites[n].sizePx) &&
                    ((respawns[z].centerY - 2 < meteorites[n].centerY + meteorites[n].sizePx) && (respawns[z].centerY + 2 > meteorites[n].centerY + meteorites[n].sizePx) ||
                            (respawns[z].centerY - 2 < meteorites[n].centerY - meteorites[n].sizePx) && (respawns[z].centerY + 2 > meteorites[n].centerY - meteorites[n].sizePx)) ||
                    (respawns[z].centerX - 2 < meteorites[n].centerX - meteorites[n].sizePx) && (respawns[z].centerX + 2 > meteorites[n].centerX - meteorites[n].sizePx) &&
                    ((respawns[z].centerY - 2 < meteorites[n].centerY + meteorites[n].sizePx) && (respawns[z].centerY + 2 > meteorites[n].centerY + meteorites[n].sizePx) ||
                            (respawns[z].centerY - 2 < meteorites[n].centerY - meteorites[n].sizePx) && (respawns[z].centerY + 2 > meteorites[n].centerY - meteorites[n].sizePx))
                )

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
                        if (((changeXifBorder(spaceShips[n].centerX + 0.5) >= changeXifBorder(
                                spaceShips[k].centerX - 0.5
                            )) &&
                                    (changeXifBorder(spaceShips[n].centerX + 0.5) <= changeXifBorder(
                                        spaceShips[k].centerX + 0.5
                                    )) &&

                                    (((changeYifBorder(spaceShips[n].centerY + 0.5) >= changeYifBorder(
                                        spaceShips[k].centerY - 0.5
                                    )) &&
                                            (changeYifBorder(spaceShips[n].centerY + 0.5) <= changeYifBorder(
                                                spaceShips[k].centerY + 0.5
                                            ))) ||

                                            ((changeYifBorder(spaceShips[n].centerY - 0.5) >= changeYifBorder(
                                                spaceShips[k].centerY - 0.5
                                            )) &&
                                                    (changeYifBorder(spaceShips[n].centerY - 0.5) <= changeYifBorder(
                                                        spaceShips[k].centerY + 0.5
                                                    ))))
                                    ) ||

                            ((changeXifBorder(spaceShips[n].centerX - 0.5) <= changeXifBorder(
                                spaceShips[k].centerX + 0.5
                            )) &&
                                    (changeXifBorder(spaceShips[n].centerX - 0.5) >= changeXifBorder(
                                        spaceShips[k].centerX - 0.5
                                    )) &&

                                    (((changeYifBorder(spaceShips[n].centerY + 0.5) >= changeYifBorder(
                                        spaceShips[k].centerY - 0.5
                                    )) &&
                                            (changeYifBorder(spaceShips[n].centerY + 0.5) <= changeYifBorder(
                                                spaceShips[k].centerY + 0.5
                                            ))) ||

                                            ((changeYifBorder(spaceShips[n].centerY - 0.5) >= changeYifBorder(
                                                spaceShips[k].centerY - 0.5
                                            )) &&
                                                    (changeYifBorder(spaceShips[n].centerY - 0.5) <= changeYifBorder(
                                                        spaceShips[k].centerY + 0.5
                                                    )))))
                        ) {
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
                bullet.update(deltaTime,width,height)

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
                        if ((((bullets[k].centerX + 2 >= spaceShips[n].centerX - 0.5) && (bullets[k].centerX + 2 <= spaceShips[n].centerX + 0.5) &&
                                    ((bullets[k].centerY + 2 >= spaceShips[n].centerY - 0.5) && (bullets[k].centerY + 2 <= spaceShips[n].centerY + 0.5) ||
                                            (bullets[k].centerY - 2 >= spaceShips[n].centerY - 0.5) && (bullets[k].centerY - 2 <= spaceShips[n].centerY + 0.5))) ||
                                    ((bullets[k].centerX - 2 >= spaceShips[n].centerX - 0.5) && (bullets[k].centerX - 2 <= spaceShips[n].centerX + 0.5) && (
                                            (bullets[k].centerY + 2 >= spaceShips[n].centerY - 0.5) && (bullets[k].centerY + 2 <= spaceShips[n].centerY + 0.5) ||
                                                    (bullets[k].centerY - 2 >= spaceShips[n].centerY - 0.5) && (bullets[k].centerY - 2 <= spaceShips[n].centerY + 0.5)))
                                    ) &&
                            (n != bullets[k].player)
                        ) {
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
                if (((changeXifBorder(bullets[n].centerX + 2) >= changeXifBorder(bullets[k].centerX - 2)) &&
                            (changeXifBorder(bullets[n].centerX + 2) <= changeXifBorder(bullets[k].centerX + 2)) &&

                            (((changeYifBorder(bullets[n].centerY + 2) >= changeYifBorder(bullets[k].centerY - 2)) &&
                                    (changeYifBorder(bullets[n].centerY + 2) <= changeYifBorder(
                                        bullets[k].centerY + 2
                                    ))) ||

                                    ((changeYifBorder(bullets[n].centerY - 2) >= changeYifBorder(
                                        bullets[k].centerY - 2
                                    )) &&
                                            (changeYifBorder(bullets[n].centerY - 2) <= changeYifBorder(
                                                bullets[k].centerY + 2
                                            ))))
                            ) ||

                    ((changeXifBorder(bullets[n].centerX - 2) <= changeXifBorder(bullets[k].centerX + 2)) &&
                            (changeXifBorder(bullets[n].centerX - 2) >= changeXifBorder(bullets[k].centerX - 2)) &&

                            (((changeYifBorder(bullets[n].centerY + 2) >= changeYifBorder(bullets[k].centerY - 2)) &&
                                    (changeYifBorder(bullets[n].centerY + 2) <= changeYifBorder(
                                        bullets[k].centerY + 2
                                    ))) ||

                                    ((changeYifBorder(bullets[n].centerY - 2) >= changeYifBorder(
                                        bullets[k].centerY - 2
                                    )) &&
                                            (changeYifBorder(bullets[n].centerY - 2) <= changeYifBorder(
                                                bullets[k].centerY + 2
                                            )))))
                ) {
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
                meteorite.update(deltaTime,width,height)

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
                if (((changeXifBorder(meteorites[n].centerX + meteorites[n].sizePx) >= changeXifBorder(
                        meteorites[k].centerX - meteorites[k].sizePx
                    )) &&
                            (changeXifBorder(meteorites[n].centerX + meteorites[n].sizePx) <= changeXifBorder(
                                meteorites[k].centerX + meteorites[k].sizePx
                            )) &&

                            (((changeYifBorder(meteorites[n].centerY + meteorites[n].sizePx) >= changeYifBorder(
                                meteorites[k].centerY - meteorites[k].sizePx
                            )) &&
                                    (changeYifBorder(meteorites[n].centerY + meteorites[n].sizePx) <= changeYifBorder(
                                        meteorites[k].centerY + meteorites[k].sizePx
                                    ))) ||

                                    ((changeYifBorder(meteorites[n].centerY - meteorites[n].sizePx) >= changeYifBorder(
                                        meteorites[k].centerY - meteorites[k].sizePx
                                    )) &&
                                            (changeYifBorder(meteorites[n].centerY - meteorites[n].sizePx) <= changeYifBorder(
                                                meteorites[k].centerY + meteorites[k].sizePx
                                            ))))
                            ) ||

                    (
                            (changeXifBorder(meteorites[n].centerX - meteorites[n].sizePx) <= changeXifBorder(
                                meteorites[k].centerX + meteorites[k].sizePx
                            )) &&
                                    (changeXifBorder(meteorites[n].centerX - meteorites[n].sizePx) >= changeXifBorder(
                                        meteorites[k].centerX - meteorites[k].sizePx
                                    )) &&

                                    (((changeYifBorder(meteorites[n].centerY + meteorites[n].sizePx) >= changeYifBorder(
                                        meteorites[k].centerY - meteorites[k].sizePx
                                    )) &&
                                            (changeYifBorder(meteorites[n].centerY + meteorites[n].sizePx) <= changeYifBorder(
                                                meteorites[k].centerY + meteorites[k].sizePx
                                            ))) ||

                                            ((changeYifBorder(meteorites[n].centerY - meteorites[n].sizePx) >= changeYifBorder(
                                                meteorites[k].centerY - meteorites[k].sizePx
                                            )) &&
                                                    (changeYifBorder(meteorites[n].centerY - meteorites[n].sizePx) <= changeYifBorder(
                                                        meteorites[k].centerY + meteorites[k].sizePx
                                                    ))))
                            )
                ) {
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
                        if (((bullets[k].centerX + 2 >= meteorites[n].centerX - meteorites[n].sizePx) && (bullets[k].centerX + 2 <= meteorites[n].centerX + meteorites[n].sizePx) && (
                                    (bullets[k].centerY + 2 >= meteorites[n].centerY - meteorites[n].sizePx) && (bullets[k].centerY + 2 <= meteorites[n].centerY + meteorites[n].sizePx) ||
                                            (bullets[k].centerY - 2 >= meteorites[n].centerY - meteorites[n].sizePx) && (bullets[k].centerY - 2 <= meteorites[n].centerY + meteorites[n].sizePx))
                                    ) ||
                            (
                                    (bullets[k].centerX - 2 >= meteorites[n].centerX - meteorites[n].sizePx) && (bullets[k].centerX - 2 <= meteorites[n].centerX + meteorites[n].sizePx) && (
                                            (bullets[k].centerY + 2 >= meteorites[n].centerY - meteorites[n].sizePx) && (bullets[k].centerY + 2 <= meteorites[n].centerY + meteorites[n].sizePx) ||
                                                    (bullets[k].centerY - 2 >= meteorites[n].centerY - meteorites[n].sizePx) && (bullets[k].centerY - 2 <= meteorites[n].centerY + meteorites[n].sizePx))
                                    )
                        ) {
                            meteorites[n].sizePx = meteorites[n].sizePx - 5
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
                                        centerX = meteorites[n].centerX + (meteorites[n].sizePx + 10),
                                        centerY = meteorites[n].centerY + (meteorites[n].sizePx + 10),
                                        angle = meteorites[n].angle - 120,
                                        speedX = +speedMeteoriteMax * cos(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                        speedY = -speedMeteoriteMax * sin(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                        sizePx = meteorites[n].sizePx,
                                        viewSize = meteorites[n].viewSize,
                                        view = meteorites[n].view,
                                    )
                                )

                                meteorites.add(
                                    Meteorite(
                                        centerX = meteorites[n].centerX + (meteorites[n].sizePx + 10),
                                        centerY = meteorites[n].centerY + (meteorites[n].sizePx + 10),
                                        angle = meteorites[n].angle - 240,
                                        speedX = +speedMeteoriteMax * cos(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                        speedY = -speedMeteoriteMax * sin(meteorites[meteorites.lastIndex].angle / 180 * Math.PI),
                                        sizePx = meteorites[n].sizePx,
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
                    if (((changeXifBorder(spaceShips[n].centerX + 0.5) >= changeXifBorder(meteorites[k].centerX - meteorites[k].sizePx)) &&
                                (changeXifBorder(spaceShips[n].centerX + 0.5) <= changeXifBorder(
                                    meteorites[k].centerX + meteorites[k].sizePx
                                )) &&

                                (((changeYifBorder(spaceShips[n].centerY + 0.5) >= changeYifBorder(
                                    meteorites[k].centerY - meteorites[k].sizePx
                                )) &&
                                        (changeYifBorder(spaceShips[n].centerY + 0.5) <= changeYifBorder(
                                            meteorites[k].centerY + meteorites[k].sizePx
                                        ))) ||

                                        ((changeYifBorder(spaceShips[n].centerY - 0.5) >= changeYifBorder(
                                            meteorites[k].centerY - meteorites[k].sizePx
                                        )) &&
                                                (changeYifBorder(spaceShips[n].centerY - 0.5) <= changeYifBorder(
                                                    meteorites[k].centerY + meteorites[k].sizePx
                                                ))))
                                ) ||

                        (
                                (changeXifBorder(spaceShips[n].centerX - 0.5) <= changeXifBorder(
                                    meteorites[k].centerX + meteorites[k].sizePx
                                )) &&
                                        (changeXifBorder(spaceShips[n].centerX - 0.5) >= changeXifBorder(
                                            meteorites[k].centerX - meteorites[k].sizePx
                                        )) &&

                                        (((changeYifBorder(spaceShips[n].centerY + 0.5) >= changeYifBorder(
                                            meteorites[k].centerY - meteorites[k].sizePx
                                        )) &&
                                                (changeYifBorder(spaceShips[n].centerY + 0.5) <= changeYifBorder(
                                                    meteorites[k].centerY + meteorites[k].sizePx
                                                ))) ||

                                                ((changeYifBorder(spaceShips[n].centerY - 0.5) >= changeYifBorder(
                                                    meteorites[k].centerY - meteorites[k].sizePx
                                                )) &&
                                                        (changeYifBorder(spaceShips[n].centerY - 0.5) <= changeYifBorder(
                                                            meteorites[k].centerY + meteorites[k].sizePx
                                                        ))))
                                )
                    ) {
                        meteorites[k].sizePx = meteorites[k].sizePx - 5
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
                                    sizePx = meteorites[k].sizePx,
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
                                    sizePx = meteorites[k].sizePx,
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
        if (status == "playing") {
            status = "pause"
            pauseTime = System.currentTimeMillis()
        } else {
            status = "playing"
//      character.timeBreath += System.currentTimeMillis() - pauseTime
        }
    }
}