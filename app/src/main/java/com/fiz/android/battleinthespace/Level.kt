package com.fiz.android.battleinthespace

import com.fiz.android.battleinthespace.actor.*
import com.fiz.android.battleinthespace.engine.Collision
import com.fiz.android.battleinthespace.engine.Physics
import com.fiz.android.battleinthespace.engine.Vec

class Level(val width: Double,
            val height: Double,
            private var countPlayers: Int = 4,
            private var round:Int
){
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
    private var lineRespawn: MutableList<SpaceShip> = mutableListOf()

    private var scores: MutableList<Int> = mutableListOf()
    
    init{
        Physics.createWorld(width, height)
        newRound()
    }
    
    fun newRound(){
        backgrounds.clear()
        for (n in 0 until height.toInt()) {
            val row: MutableList<Int> = mutableListOf()
            for (k in 0 until width.toInt())
                row += (0 until NUMBER_BITMAP_BACKGROUND).shuffled().first()
            backgrounds += row
        }

        spaceShips.clear()
        for (n in 0 until countPlayers)
            spaceShips += SpaceShip(respawns[n])
        lineRespawn.clear()

        lifes.clear()
        for (numberPlayer in 0 until countPlayers) {
            lifes += 3
        }

        createMeteorites()

        animationBulletDestroys.clear()
        animationSpaceShipDestroys.clear()
    }

    private fun createMeteorites() {
        meteorites.clear()
        for (n in 0 until round)
            createMeteorite()
    }

    private fun createMeteorite() {
        for (dx in (0 until width.toInt() / 2))
            for (dy in (0 until height.toInt() / 2)) {
                if (isCreateMeteoriteWithoutOverlap(
                            width / 2.0 + dx + 0.1,
                            height / 2.0 + dy + 0.1
                        )
                )
                    return
                if (isCreateMeteoriteWithoutOverlap(
                            width / 2.0 - dx + 0.1,
                            height / 2.0 - dy + 0.1
                        )
                )
                    return
            }
    }

    private fun isCreateMeteoriteWithoutOverlap(dx: Double, dy: Double): Boolean {
        val newMeteorite1 = Meteorite.createNew(dx, dy)

        for (colMeteorite in meteorites)
            if (overlap(newMeteorite1, colMeteorite))
                return false

        meteorites.add(newMeteorite1)
        return true
    }

    fun update(controller: Array<Controller>, deltaTime: Int): Boolean {
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

    private fun updateMoveRotate(numberPlayer: Int, deltaTime: Int, controller: Controller) {
        spaceShips[numberPlayer].moveRotate(deltaTime, controller)
    }

    private fun updateMoveForward(numberPlayer: Int, deltaTime: Int, controller: Controller) {
        spaceShips[numberPlayer].moveForward(deltaTime, controller)
    }

    private fun updatePressFire(numberPlayer: Int, deltaTime: Int) {
        if (spaceShips[numberPlayer].inGame)
            bullets += Bullet.create(spaceShips, numberPlayer)
    }

    private fun updateSpaceShips(deltaTime: Int) {
        for (spaceShip in spaceShips)
            if (spaceShip.inGame)
                spaceShip.update(deltaTime, width, height)

        val lineRespawnDestroy: MutableList<SpaceShip> = mutableListOf()
        for (spaceShip in lineRespawn)
            if (spaceShip.isCanRespawn(deltaTime))
                if (respawnCheck(spaceShip))
                    lineRespawnDestroy.add(spaceShip)

        for (spaceShip in lineRespawnDestroy)
            lineRespawn.remove(spaceShip)

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
        if (countPlayers > 1) {
            for (n in 0 until (countPlayers - 1))
                for (k in (n + 1) until countPlayers)
                    if (spaceShips[n].inGame && spaceShips[k].inGame
                            && overlap(spaceShips[n], spaceShips[k]))
                        kickback(spaceShips[n], spaceShips[k])

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
        for (spaceShip in spaceShips)
            if (spaceShip.inGame)
                for (bullet in bullets)
                    if (overlap(bullet, spaceShip)
                            && spaceShips.indexOf(spaceShip) != bullet.player
                    ) {
                        kickback(spaceShip, bullet)

                        animationBulletDestroys.add(AnimationDestroy(bullet))
                        listBulletDestroy.add(bullet)
                    }

        for (bullet in listBulletDestroy) {
            bullets.remove(bullet)
        }
    }

    private fun collisionBulletBullet() {
        val listBulletDestroy: MutableList<Bullet> = mutableListOf()

        for (n in 0 until (bullets.size - 1))
            for (k in (n + 1) until bullets.size)
                if (overlap(bullets[n], bullets[k])) {
                    animationBulletDestroys.add(AnimationDestroy(bullets[k]))
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
                        scores[bullet.player] + meteorite.viewSize
                    listMeteoritesDestroy.add(meteorite)
                    listAngleMeteoritesDestroy.add(bullet.angle)

                    animationBulletDestroys.add(AnimationDestroy(bullet))
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
                if (spaceShip.inGame && overlap(spaceShip, meteorite)) {
                    lifes[spaceShips.indexOf(spaceShip)] -= 1
                    spaceShip.inGame = false
                    if (lifes[spaceShips.indexOf(spaceShip)] > 0)
                        lineRespawn.add(spaceShip)
                    listMeteoritesDestroy.add(meteorite)
                    listAngleMeteoritesDestroy.add(spaceShip.angle)

                    animationSpaceShipDestroys.add(AnimationDestroy(spaceShip))
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

            if (checkCanCreateNewMeteoriteIsOverlap(newMeteorite, meteoriteDestroy))
                meteorites.add(newMeteorite)
        }
    }

    private fun checkCanCreateNewMeteoriteIsOverlap(
        newMeteorite: Meteorite,
        meteoriteDestroy: Meteorite,
    ): Boolean {
        for (colMeteorite in meteorites)
            if (colMeteorite != meteoriteDestroy && overlap(newMeteorite, colMeteorite))
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

    fun getScoresForUpdate():MutableList<Int>{
        return scores
    }
    
}