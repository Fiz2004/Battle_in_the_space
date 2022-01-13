package com.fiz.android.battleinthespace

import kotlin.math.*

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
    var lineRespawn: MutableList<SpaceShip> = mutableListOf()

    init {
        Physics.createWorld(width, height)
        newGame()
    }

    private fun newGame() {
        round = 0
        newRound()

        scores.clear()
        for (n in 0 until countPlayers) {
            scores += 0
        }
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
        lineRespawn.clear()

        lifes.clear()
        for (numberPlayer in 0 until countPlayers) {
            lifes += 3
        }

        meteorites.clear()
        m2@ for (n in 0 until round) {
            for (dx in (0 until width.toInt() / 2))
                for (dy in (0 until height.toInt() / 2)) {
                    val newMeteorite1 = Meteorite.createNew(
                        width / 2.0 + dx,
                        height / 2.0 + dy
                    )
                    var fl1 = true
                    for (colMeteorite in meteorites)
                        if (overlap(newMeteorite1, colMeteorite))
                            fl1 = false
                    if (fl1) {
                        meteorites.add(newMeteorite1)
                        continue@m2
                    }
                    val newMeteorite2 = Meteorite.createNew(
                        width / 2.0 - dx,
                        height / 2.0 - dy
                    )
                    var fl2 = true
                    for (colMeteorite in meteorites)
                        if (overlap(newMeteorite2, colMeteorite))
                            fl2 = false
                    if (fl2)
                        meteorites.add(newMeteorite2)
                }
        }

        animationBulletDestroys.clear()
        animationSpaceShipDestroys.clear()
    }

    fun update(controller: Array<Controller>, deltaTime: Int): Boolean {
        for (player in 0 until countPlayers) {
            if (controller[player].power != 0F) {
                spaceShips[player].isFly = true
                updateMoveRotate(player, deltaTime, controller[player])
                updateMoveForward(player, deltaTime, controller[player])
            } else {
                spaceShips[player].isFly = false
            }
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
                angle = spaceShips[numberPlayer].angle,
                roadLength = 0.0,
                player = numberPlayer
            )
        }
    }

    private fun updateSpaceShips(deltaTime: Int) {
        for (spaceShip in spaceShips)
            if (spaceShip.inGame)
                spaceShip.update(deltaTime, width, height)

        val lineRespawnDestroy: MutableList<SpaceShip> = mutableListOf()
        for (spaceShip in lineRespawn)
            if (respawnCheck(spaceShip))
                lineRespawnDestroy.add(spaceShip)

        for (spaceShip in lineRespawnDestroy)
            lineRespawn.remove(spaceShip)

        collisionSpaceShips()
    }

    private fun respawnCheck(currentSpaceShip: SpaceShip): Boolean {
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
                return true
            }
        }
        return false
    }

    private fun collisionSpaceShips() {
        if (countPlayers > 1) {
            for (n in 0 until (countPlayers - 1))
                for (k in (n + 1) until countPlayers)
                    if (spaceShips[n].inGame && spaceShips[k].inGame)
                        if (overlap(spaceShips[n], spaceShips[k])) {
                            kickback(spaceShips[n], spaceShips[k])
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
        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        if (bullets.size > 0) {
            for (spaceShip in spaceShips)
                if (spaceShip.inGame)
                    for (bullet in bullets)
                        if (overlap(bullet, spaceShip)
                            && spaceShips.indexOf(spaceShip) != bullet.player
                        ) {
                            kickback(spaceShip, bullet)

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

            collisionMeteoriteMeteoriteBulletsSpaceShips()
        }
    }

    private fun collisionMeteoriteMeteoriteBulletsSpaceShips() {
        for (n in 0 until (meteorites.size - 1))
            for (k in (n + 1) until meteorites.size)
                if (overlap(meteorites[n], meteorites[k])) {
                    kickback(meteorites[n], meteorites[k])
                }


        val listMeteoritesDestroy: MutableList<Meteorite> = mutableListOf()
        val listAngleMeteoritesDestroy: MutableList<Double> = mutableListOf()

        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        for (meteorite in meteorites)
            for (bullet in bullets)
                if (overlap(bullet, meteorite)) {
                    scores[bullet.player] =
                        scores[bullet.player] + 100 * meteorite.viewSize
                    listMeteoritesDestroy.add(meteorite)
                    listAngleMeteoritesDestroy.add(bullet.angle)

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

        spaceShipDestroy(listMeteoritesDestroy, listAngleMeteoritesDestroy)

        meteoritesDestroy(listMeteoritesDestroy, listAngleMeteoritesDestroy)
    }

    private fun spaceShipDestroy(
        listMeteoritesDestroy: MutableList<Meteorite>,
        listAngleMeteoritesDestroy: MutableList<Double>
    ) {
        for (spaceShip in spaceShips)
            for (meteorite in meteorites)
                if (spaceShip.inGame)
                    if (overlap(spaceShip, meteorite)) {
                        lifes[spaceShips.indexOf(spaceShip)] -= 1
                        spaceShip.inGame = false
                        if (lifes[spaceShips.indexOf(spaceShip)] > 0)
                            lineRespawn.add(spaceShip)
                        listMeteoritesDestroy.add(meteorite)
                        listAngleMeteoritesDestroy.add(spaceShip.angle)

                        animationSpaceShipDestroys.add(AnimationSpaceShipDestroy(spaceShip))
                    }
    }

    private fun meteoritesDestroy(
        listMeteoritesDestroy: MutableList<Meteorite>,
        listAngleMeteoritesDestroy: MutableList<Double>
    ) {
        val listMeteoritesFullDestroy: MutableList<Meteorite> = mutableListOf()

        for ((index, meteoriteDestroy) in listMeteoritesDestroy.withIndex()) {
            val angleDestroy = listAngleMeteoritesDestroy[index]

            listMeteoritesFullDestroy.add(meteoriteDestroy)
            if (meteoriteDestroy.viewSize + 1 > 3)
                continue

            createThreeMeteorites(meteoriteDestroy, angleDestroy)
        }

        listMeteoritesFullDestroy.forEach { meteorites.remove(it) }
    }

    private fun createThreeMeteorites(
        meteoriteDestroy: Meteorite,
        angleDestroy: Double
    ) {
        for (angle in (0 until 360) step 120) {
            val newMeteorite = meteoriteDestroy.createMeteorite(angleDestroy, angle.toDouble())

            if (CheckCanCreateNewMeteoriteIsOverlap(newMeteorite, meteoriteDestroy))
                meteorites.add(newMeteorite)
        }
    }

    private fun CheckCanCreateNewMeteoriteIsOverlap(
        newMeteorite: Meteorite,
        meteoriteDestroy: Meteorite,
    ): Boolean {
        for (colMeteorite in meteorites)
            if (colMeteorite != meteoriteDestroy)
                if (overlap(newMeteorite, colMeteorite))
                    return false
        return true
    }

    private fun overlap(actor1: Actor, actor2: Actor): Boolean {
        return Physics.overlap(
            actor1.centerX, actor1.centerY, actor1.size,
            actor2.centerX, actor2.centerY, actor2.size
        )
    }

    private fun kickback(actor1: Actor, actor2: Actor) {
        val speedActorX1: Double = actor1.speedX
        val speedActorX2: Double = actor2.speedX
        val speedActorY1: Double = actor1.speedY
        val speedActorY2: Double = actor2.speedY

        actor1.speedX = Physics.getSpeedFirstAfterKickback(speedActorX1, speedActorX2)
        actor2.speedX = Physics.getSpeedFirstAfterKickback(speedActorX2, speedActorX1)
        actor1.speedY = Physics.getSpeedFirstAfterKickback(speedActorY1, speedActorY2)
        actor2.speedY = Physics.getSpeedFirstAfterKickback(speedActorY2, speedActorY1)
    }

    fun clickPause() {
        status = if (status == "playing")
            "pause"
        else
            "playing"
    }
}