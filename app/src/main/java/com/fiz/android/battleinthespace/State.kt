package com.fiz.android.battleinthespace

import kotlin.math.*

private const val speedSpaceShipMax = 500.0 / 1000
private const val speedMeteoriteMax = 100.0 / 1000
private const val speedBulletMax = 4.0

class State(
    val width: Double,
    val height: Double,
) {
    var countPlayers: Int = 4
    var round: Int = 1
    var status = "playing"

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
            angle = 45.0
        ),
        Respawn(
            centerX = width - 2.0,
            centerY = 2.0,
            angle = 135.0
        ),
        Respawn(
            centerX = 2.0,
            centerY = height - 2.0,
            angle = 315.0
        ),
        Respawn(
            centerX = width - 2.0,
            centerY = height - 2.0,
            angle = 225.0
        )
    )

    init {
        newGame()
    }

    private fun newGame() {
        round = 0
        newRound()

        scores.clear()
        for (n in 0 until countPlayers) {
            scores += 0
        }

        round = 1
    }

    private fun newRound() {
        round += 1

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

        lifes.clear()
        for (numberPlayer in 0 until countPlayers) {
            lifes += 3
        }

        //TODO Добавить создание метеоритов если их много в последующих раундах
        meteorites.clear()
        for (n in 0 until round)
            meteorites += Meteorite.create()

        animationBulletDestroys.clear()
        animationSpaceShipDestroys.clear()
    }

    fun update(controller: Array<Controller>, deltaTime: Int): Boolean {
        for (player in 0 until countPlayers) {
            if (controller[player].power != 0F) {
                updateMoveRotate(player, deltaTime, controller[player])
                updateMoveForward(player, deltaTime, controller[player])
            }
//            if (controller[player].up)
//                updateMoveUp(player, deltaTime)
//            if (controller[player].down)
//                updateMoveDown(player, deltaTime)
//            if (controller[player].right)
//                updateMoveRight(player, deltaTime)
//            if (controller[player].left)
//                updateMoveLeft(player, deltaTime)
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
            it.timeShow -= deltaTime
        }
        animationBulletDestroys = animationBulletDestroys.filter {
            it.timeShow > 0
        }.toMutableList()

        animationSpaceShipDestroys.forEach {
            it.timeShow -= deltaTime
        }
        animationSpaceShipDestroys = animationSpaceShipDestroys.filter {
            it.timeShow > 0
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

    private fun updateMoveRotate(numberPlayer: Int, deltaTime: Int, controller: Controller) {
        spaceShips[numberPlayer].moveRotate(deltaTime, controller)
    }

    private fun updateMoveForward(numberPlayer: Int, deltaTime: Int, controller: Controller) {
        spaceShips[numberPlayer].moveForward(deltaTime, controller)
    }

    private fun updatePressFire(numberPlayer: Int, deltaTime: Int) {
        if (spaceShips[numberPlayer].inGame) {
            bullets += Bullet(
                centerX = spaceShips[numberPlayer].centerX + 1 * cos(spaceShips[numberPlayer].angle / 180.0 * Math.PI),
                centerY = spaceShips[numberPlayer].centerY + 1 * sin(spaceShips[numberPlayer].angle / 180.0 * Math.PI),
                speedX = speedBulletMax * cos(spaceShips[numberPlayer].angle / 180.0 * Math.PI),
                speedY = speedBulletMax * sin(spaceShips[numberPlayer].angle / 180.0 * Math.PI),
                angle = 0.0,// TODO Возможно не требуется проверить
                roadLength = 0.0,
                player = numberPlayer
            )
        }
    }

    private fun updateSpaceShips(deltaTime: Int) {
        for (spaceShip in spaceShips)
            if (spaceShip.inGame)
                spaceShip.update(deltaTime, width, height)
            else
                checkRespawn(spaceShip)

        collisionSpaceShips()
    }

    private fun checkRespawn(currentSpaceShip: SpaceShip) {
        val numberPlayer = spaceShips.indexOf(currentSpaceShip)
        if (lifes[numberPlayer] > 0) {
            val respawn = respawns.find {
                var result = true
                for (spaceShip in spaceShips)
                    if (spaceShip.inGame)
                        if (overlap(it, spaceShip))
                            result = false
                for (bullet in bullets)
                    if (overlap(it, bullet))
                        result = false
                for (meteorite in meteorites)
                    if (overlap(it, meteorite))
                        result = false
                result
            }
            if (respawn != null) {
                spaceShips[numberPlayer] = SpaceShip(
                    centerX = respawn.centerX,
                    centerY = respawn.centerY,
                    angle = respawn.angle
                )
            }
        }
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
                        if (overlap(spaceShips[n], spaceShips[k])) {
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
                        }
        }
    }

    private fun updateBullets(deltaTime: Int) {
        if (bullets.isNotEmpty()) {
            for (bullet in bullets)
                bullet.update(deltaTime, width, height)

            collisionBulletSpaceShips()
            collisionBulletBullet()

            bullets = bullets.filter {
                it.roadLength <= 6
            }.toMutableList()
        }
    }

    private fun collisionBulletSpaceShips() {
        var speedSpaceShipX1: Double
        var speedBulletX2: Double
        var speedSpaceShipY1: Double
        var speedBulletY2: Double
        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        if (bullets.size > 0) {
            for (spaceShip in spaceShips)
                if (spaceShip.inGame)
                    for (bullet in bullets)
                        if (overlap(
                                bullet,
                                spaceShip
                            ) && spaceShips.indexOf(spaceShip) != bullet.player
                        ) {
                            speedSpaceShipX1 = spaceShip.speedX
                            speedBulletX2 = bullet.speedX
                            speedSpaceShipY1 = spaceShip.speedY
                            speedBulletY2 = bullet.speedY
                            if (((speedSpaceShipX1 > 0) && (speedBulletX2 < 0)) || ((speedSpaceShipX1 < 0) && (speedBulletX2 > 0)))
                                spaceShip.speedX = -speedSpaceShipX1
                            else {
                                spaceShip.speedX = (speedSpaceShipX1 + speedBulletX2) / 2
                                spaceShip.speedX = 2 * spaceShip.speedX
                            }
                            if (((speedSpaceShipY1 > 0) && (speedBulletY2 < 0)) || ((speedSpaceShipY1 < 0) && (speedBulletY2 > 0)))
                                spaceShip.speedY = -speedSpaceShipY1
                            else {
                                spaceShip.speedY = (speedSpaceShipY1 + speedBulletY2) / 2
                                spaceShip.speedY = 2 * spaceShip.speedY
                            }
                            animationBulletDestroys.add(
                                AnimationBulletDestroy(
                                    centerX = bullet.centerX,
                                    centerY = bullet.centerY,
                                )
                            )
                            listBulletDestroy.add(bullet)
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
                if (overlap(bullets[n], bullets[k])) {
                    animationBulletDestroys.add(
                        AnimationBulletDestroy(
                            centerX = bullets[k].centerX,
                            centerY = bullets[k].centerY,
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
                }

        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        val listMeteoritesDestroy: MutableList<Meteorite> = mutableListOf()
        if (bullets.isNotEmpty())
            if (meteorites.isNotEmpty())
                for (meteorite in meteorites)
                    for (bullet in bullets)
                        if (overlap(bullet, meteorite)) {
                            scores[bullet.player] =
                                scores[bullet.player] + 100 * meteorite.viewSize
                            listMeteoritesDestroy.add(meteorite)

                            animationBulletDestroys.add(
                                AnimationBulletDestroy(
                                    centerX = bullet.centerX,
                                    centerY = bullet.centerY,
                                )
                            )
                            listBulletDestroy.add(bullet)
                        }

        for (bullet in listBulletDestroy)
            bullets.remove(bullet)

        for (spaceShip in spaceShips)
            for (meteorite in meteorites)
                if (spaceShip.inGame)
                    if (overlap(spaceShip, meteorite)) {
                        lifes[spaceShips.indexOf(spaceShip)] -= 1
                        spaceShip.inGame = false
                        listMeteoritesDestroy.add(meteorite)

                        animationSpaceShipDestroys.add(
                            AnimationSpaceShipDestroy(
                                centerX = spaceShip.centerX,
                                centerY = spaceShip.centerY,
                            )
                        )
                    }

        //TODO поменять угол разлета метиоритов в зависимости от того под каким углом прилетела пуля
        val listMeteoritesFullDestroy: MutableList<Meteorite> = mutableListOf()
        if (listMeteoritesDestroy.isNotEmpty())
            for (meteorite in listMeteoritesDestroy) {
                meteorite.size = meteorite.size - 0.2
                meteorite.viewSize = meteorite.viewSize + 1
                if (meteorite.viewSize > 3) {
                    listMeteoritesFullDestroy.add(meteorite)
                } else {
                    val speed=sqrt(meteorite.speedX*meteorite.speedX+meteorite.speedY*meteorite.speedY)
                    meteorites.add(
                        Meteorite(
                            centerX = meteorite.centerX + (meteorite.size + (meteorite.size / 2 + 0.2) * sign(
                                cos((meteorite.angle - 120) / 180.0 * Math.PI)
                            )),
                            centerY = meteorite.centerY + (meteorite.size + (meteorite.size / 2 + 0.2) * sign(
                                sin((meteorite.angle - 120) / 180.0 * Math.PI)
                            )),
                            angle = meteorite.angle - 120,
                            speedX = speed * cos((meteorite.angle- 120) / 180.0 * Math.PI),
                            speedY = speed * sin((meteorite.angle- 120) / 180.0 * Math.PI),
                            size = meteorite.size,
                            viewSize = meteorite.viewSize,
                            view = meteorite.view,
                        )
                    )

                    meteorites.add(
                        Meteorite(
                            centerX = meteorite.centerX + (meteorite.size + (meteorite.size / 2 + 0.2) * sign(
                                cos((meteorite.angle - 240) / 180.0 * Math.PI)
                            )),
                            centerY = meteorite.centerY + (meteorite.size + (meteorite.size / 2 + 0.2) * sign(
                                sin((meteorite.angle - 240) / 180.0 * Math.PI)
                            )),
                            angle = meteorite.angle - 240,
                            speedX = speed * cos((meteorite.angle-240) / 180.0 * Math.PI),
                            speedY = speed * sin((meteorite.angle-240)  / 180.0 * Math.PI),
                            size = meteorite.size,
                            viewSize = meteorite.viewSize,
                            view = meteorite.view,
                        )
                    )
                }
            }

        for (meteorite in listMeteoritesFullDestroy) {
            meteorites.remove(meteorite)
        }
    }


    private fun overlap(actor1: Actor, actor2: Actor): Boolean {
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
        val LUA1 = L1 > L2 && L1 < R2 && U1 > U2 && U1 < D2
        val RUA1 = R1 > L2 && R1 < R2 && U1 > U2 && U1 < D2
        val LDA1 = L1 > L2 && L1 < R2 && D1 > U2 && D1 < D2
        val RDA1 = R1 > L2 && R1 < R2 && D1 > U2 && D1 < D2

        val LUA2 = L2 > L1 && L2 < R1 && U2 > U1 && U2 < D1
        val RUA2 = R2 > L1 && R2 < R1 && U2 > U1 && U2 < D1
        val LDA2 = L2 > L1 && L2 < R1 && D2 > U1 && D2 < D1
        val RDA2 = R2 > L1 && R2 < R1 && D2 > U1 && D2 < D1

        return LUA1 || RUA1 || LDA1 || RDA1 || LUA2 || RUA2 || LDA2 || RDA2
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