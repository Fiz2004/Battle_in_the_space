package com.fiz.android.battleinthespace

import com.fiz.android.battleinthespace.actor.*
import com.fiz.android.battleinthespace.engine.Collision
import com.fiz.android.battleinthespace.engine.Physics
import com.fiz.android.battleinthespace.engine.Vec

class Level(
    val width: Double,
    val height: Double,
    private var countPlayers: Int = 4,
    private var countMeteorites: Int
) {
    var backgrounds: MutableList<MutableList<Int>> = mutableListOf()
    var spaceShips: MutableList<SpaceShip> = mutableListOf()
    var bullets: MutableList<Bullet> = mutableListOf()
    var meteorites: MutableList<Meteorite> = mutableListOf()

    var animationBulletDestroys: MutableList<AnimationDestroy> = mutableListOf()
    var animationSpaceShipDestroys: MutableList<AnimationDestroy> = mutableListOf()

    var lifes: MutableList<Int> = mutableListOf()

    private var respawns: MutableList<Respawn> = mutableListOf(
        Respawn(
            Vec(width / 4, height / 4),
            angle = 45.0
        ),
        Respawn(
            Vec(width - width / 4, height / 4),
            angle = 135.0
        ),
        Respawn(
            Vec(width / 4, height - height / 4),
            angle = 315.0
        ),
        Respawn(
            Vec(width - width / 4, height - height / 4),
            angle = 225.0
        )
    )
    private var lineSpaceShipsOnRespawn: MutableList<SpaceShip> = mutableListOf()

    private var scores: MutableList<Int> = mutableListOf()

    init {
        Physics.createWorld(width, height)
        newRound()
    }

    private fun newRound() {
        createBackgrounds()
        createSpaceShips()
        createLifes()
        createMeteorites()
    }

    private fun createBackgrounds() {
        for (n in 0 until height.toInt()) {
            val row: MutableList<Int> = mutableListOf()
            for (k in 0 until width.toInt())
                row += (0 until NUMBER_BITMAP_BACKGROUND).shuffled().first()
            backgrounds += row
        }
    }

    private fun createSpaceShips() {
        for (n in 0 until countPlayers)
            spaceShips += SpaceShip(respawns[n])
    }

    private fun createLifes() {
        for (numberPlayer in 0 until countPlayers)
            lifes += 3
    }

    private fun createMeteorites() {
        meteorites.clear()
        for (n in 0 until countMeteorites)
            createMeteorite()
    }

    private fun createMeteorite() {
        var x = 0
        while (true) {
            val dx = (0 until width.toInt()).shuffled().first().toDouble()
            val dy = (0 until height.toInt()).shuffled().first().toDouble()
            if (isCreateMeteoriteWithoutOverlap(dx, dy))
                return
            x += 1
            if (x > 1000) throw Error("Превышено время ожидания функции createMeteorite")
        }
    }

    private fun isCreateMeteoriteWithoutOverlap(x: Double, y: Double): Boolean {
        val newMeteorite = Meteorite.createNew(x, y)

        for (meteorite in meteorites)
            if (overlap(newMeteorite, meteorite))
                return false

        for (respawn in respawns)
            if (overlap(newMeteorite, respawn))
                return false

        meteorites.add(newMeteorite)
        return true
    }

    fun update(controller: Array<Controller>, deltaTime: Double): Boolean {
        scores.clear()
        for (n in 0 until countPlayers)
            scores.add(0)

        for (player in 0 until countPlayers) {
            if (controller[player].power != 0F) {
                spaceShips[player].isFly = true
                updateMoveRotate(player, deltaTime, controller[player])
                updateMoveForward(player, deltaTime, controller[player])
            } else {
                spaceShips[player].isFly = false
            }
            if (controller[player].fire && controller[player].isCanFire(deltaTime))
                updatePressFire(player, deltaTime)
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

        if (lifes.none { it > 0 } || meteorites.isEmpty())
            return false

        return true
    }

    private fun updateMoveRotate(numberPlayer: Int, deltaTime: Double, controller: Controller) {
        spaceShips[numberPlayer].moveRotate(deltaTime, controller)
    }

    private fun updateMoveForward(numberPlayer: Int, deltaTime: Double, controller: Controller) {
        spaceShips[numberPlayer].moveForward(deltaTime, controller)
    }

    private fun updatePressFire(numberPlayer: Int, deltaTime: Double) {
        if (spaceShips[numberPlayer].inGame)
            bullets += Bullet.create(spaceShips, numberPlayer)
    }

    private fun updateSpaceShips(deltaTime: Double) {
        for (spaceShip in spaceShips)
            if (spaceShip.inGame)
                spaceShip.update(deltaTime, width, height)

        lineSpaceShipsOnRespawn = lineSpaceShipsOnRespawn.filterNot { spaceShips ->
            spaceShips.isCanRespawnFromTime(deltaTime) && respawnCheck(spaceShips)
        }.toMutableList()

        collisionSpaceShips()
    }

    private fun respawnCheck(currentSpaceShip: SpaceShip): Boolean {
        val numberPlayer = spaceShips.indexOf(currentSpaceShip)
        if (lifes[numberPlayer] > 0) {
            val respawn = respawns.find(::findFreeRespawn)
            if (respawn != null) {
                spaceShips[numberPlayer] = SpaceShip(respawn)
                return true
            }
        }
        return false
    }

    private fun findFreeRespawn(it: Respawn): Boolean {
        for (spaceShip in spaceShips)
            if (spaceShip.inGame && overlap(it, spaceShip))
                return false
        for (bullet in bullets)
            if (overlap(it, bullet))
                return false
        for (meteorite in meteorites)
            if (overlap(it, meteorite))
                return false
        return true
    }

    private fun collisionSpaceShips() {
        for (n in 0 until (countPlayers - 1))
            for (k in (n + 1) until countPlayers)
                if (spaceShips[n].inGame && spaceShips[k].inGame
                    && overlap(spaceShips[n], spaceShips[k])
                )
                    kickback(spaceShips[n], spaceShips[k])
    }

    private fun updateBullets(deltaTime: Double) {
        if (bullets.isNotEmpty()) {
            for (bullet in bullets)
                bullet.update(deltaTime, width, height)

            val listBulletDestroy: MutableList<Bullet> = mutableListOf()
            listBulletDestroy += getCollisionBulletSpaceShips()
            listBulletDestroy += getCollisionBulletBullet()
            for (bullet in listBulletDestroy)
                bullets.remove(bullet)

            bullets = bullets.filter {
                it.roadLength <= 6
            }.toMutableList()
        }
    }

    private fun getCollisionBulletSpaceShips(): MutableList<Bullet> {
        val result: MutableList<Bullet> = mutableListOf()
        for (spaceShip in spaceShips)
            if (spaceShip.inGame)
                for (bullet in bullets)
                    if (overlap(bullet, spaceShip)
                        && spaceShips.indexOf(spaceShip) != bullet.player
                    ) {
                        kickback(spaceShip, bullet)

                        animationBulletDestroys.add(AnimationDestroy(bullet))
                        result.add(bullet)
                    }

        return result
    }

    private fun getCollisionBulletBullet(): MutableList<Bullet> {
        val result: MutableList<Bullet> = mutableListOf()

        for (n in 0 until (bullets.size - 1))
            for (k in (n + 1) until bullets.size)
                if (overlap(bullets[n], bullets[k])) {
                    animationBulletDestroys.add(AnimationDestroy(bullets[k]))
                    result.add(bullets[k])
                }

        return result
    }

    private fun updateMeteorites(deltaTime: Double) {
        if (meteorites.isNotEmpty()) {
            for (meteorite in meteorites)
                meteorite.update(deltaTime, width, height)

            collisionMeteoriteMeteorite()
            collisionMeteoriteBulletsSpaceShips()
        }
    }

    private fun collisionMeteoriteMeteorite() {
        for (n in 0 until (meteorites.size - 1))
            for (k in (n + 1) until meteorites.size)
                if (overlap(meteorites[n], meteorites[k]))
                    kickback(meteorites[n], meteorites[k])
    }

    private fun collisionMeteoriteBulletsSpaceShips() {
        val mapMeteoritesDestroyAndAngle: MutableMap<Meteorite, Double> = mutableMapOf()

        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        for (meteorite in meteorites)
            for (bullet in bullets)
                if (overlap(bullet, meteorite)) {
                    scores[bullet.player] = scores[bullet.player] + meteorite.viewSize
                    mapMeteoritesDestroyAndAngle[meteorite] = bullet.angle

                    animationBulletDestroys.add(AnimationDestroy(bullet))
                    listBulletDestroy.add(bullet)
                }

        for (bullet in listBulletDestroy)
            bullets.remove(bullet)

        spaceShipDestroy(mapMeteoritesDestroyAndAngle)
        meteoritesDestroy(mapMeteoritesDestroyAndAngle)
    }

    private fun spaceShipDestroy(
        mapMeteoritesDestroyAndAngle: MutableMap<Meteorite, Double>
    ) {
        for (spaceShip in spaceShips)
            for (meteorite in meteorites)
                if (spaceShip.inGame && overlap(spaceShip, meteorite)) {
                    lifes[spaceShips.indexOf(spaceShip)] -= 1
                    spaceShip.inGame = false
                    if (lifes[spaceShips.indexOf(spaceShip)] > 0)
                        lineSpaceShipsOnRespawn.add(spaceShip)
                    mapMeteoritesDestroyAndAngle[meteorite] = spaceShip.angle

                    animationSpaceShipDestroys.add(AnimationDestroy(spaceShip))
                }
    }

    private fun meteoritesDestroy(
        mapMeteoritesDestroyAndAngle: MutableMap<Meteorite, Double>
    ) {
        val listMeteoritesFullDestroy: MutableList<Meteorite> = mutableListOf()

        for ((meteoriteDestroy, angleDestroy) in mapMeteoritesDestroyAndAngle.entries) {
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

            if (checkCanCreateNewMeteoriteIsOverlap(newMeteorite, meteoriteDestroy))
                meteorites.add(newMeteorite)
        }
    }

    private fun checkCanCreateNewMeteoriteIsOverlap(
        newMeteorite: Meteorite,
        meteoriteDestroy: Meteorite,
    ): Boolean {
        for (meteorite in meteorites)
            if (meteorite != meteoriteDestroy && overlap(newMeteorite, meteorite))
                return false
        return true
    }

    private fun overlap(actor1: Actor, actor2: Actor): Boolean {
        return Physics.overlapCircle(
            actor1.center, actor1.size,
            actor2.center, actor2.size
        )
    }

    private fun kickback(moveableActor1: MoveableActor, moveableActor2: MoveableActor) {
        val manifold = Collision(moveableActor1, moveableActor2)
        manifold.applyImpulse()
        manifold.positionalCorrection()
    }

    fun getScoresForUpdate(): MutableList<Int> {
        return scores
    }

}