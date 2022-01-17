package com.fiz.android.battleinthespace

import com.fiz.android.battleinthespace.actor.*
import com.fiz.android.battleinthespace.engine.Manifold
import com.fiz.android.battleinthespace.engine.Physics
import com.fiz.android.battleinthespace.engine.Vec
import com.fiz.android.battleinthespace.engine.Vec2
import kotlin.math.sqrt

class State(
    var countPlayers: Int = 4,
    var namePlayers: List<String> = listOf("Player 1", "Player 2", "Player 3", "Player 4"),
) {
    val level: Level = Level(20.0, 20.0)
    var round: Int = 1
    var status: String = "playing"
    var mainPlayer: Int = 0

    private var scores: MutableList<Int> = mutableListOf()
    var lifes: MutableList<Int> = mutableListOf()
    var animationBulletDestroys: MutableList<AnimationDestroy> = mutableListOf()
    var animationSpaceShipDestroys: MutableList<AnimationDestroy> = mutableListOf()
    private var respawns: MutableList<Respawn> = mutableListOf(
        Respawn(
            Vec(level.width / 4, level.height / 4),
            angle = 45.0
        ),
        Respawn(
            Vec(level.width - level.width / 4, level.height / 4),
            angle = 135.0
        ),
        Respawn(
            Vec(level.width / 4, level.height - level.height / 4),
            angle = 315.0
        ),
        Respawn(
            Vec(level.width - level.width / 4, level.height - level.height / 4),
            angle = 225.0
        )
    )
    private var lineRespawn: MutableList<SpaceShip> = mutableListOf()

    init {
        Physics.createWorld(level.width, level.height)
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

        level.backgrounds.clear()
        for (n in 0 until level.height.toInt()) {
            val row: MutableList<Int> = mutableListOf()
            for (k in 0 until level.width.toInt())
                row += (0..7).shuffled().first()
            level.backgrounds += row
        }

        level.spaceShips.clear()
        for (n in 0 until countPlayers)
            level.spaceShips += SpaceShip(respawns[n])
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
        level.meteorites.clear()
        for (n in 0 until round)
            createMeteorite()
    }

    private fun createMeteorite() {
        for (dx in (0 until level.width.toInt() / 2))
            for (dy in (0 until level.height.toInt() / 2)) {
                if (isCreateMeteoriteWithoutOverlap(
                        level.width / 2.0 + dx + 0.1,
                        level.height / 2.0 + dy + 0.1
                    )
                )
                    return
                if (isCreateMeteoriteWithoutOverlap(
                        level.width / 2.0 - dx + 0.1,
                        level.height / 2.0 - dy + 0.1
                    )
                )
                    return
            }
    }

    private fun isCreateMeteoriteWithoutOverlap(dx: Double, dy: Double): Boolean {
        val newMeteorite1 = Meteorite.createNew(dx, dy)

        for (colMeteorite in level.meteorites)
            if (overlap(newMeteorite1, colMeteorite))
                return false

        level.meteorites.add(newMeteorite1)
        return true
    }

    fun update(controller: Array<Controller>, deltaTime: Int): Boolean {
        for (player in 0 until countPlayers) {
            if (controller[player].power != 0F) {
                level.spaceShips[player].isFly = true
                updateMoveRotate(player, deltaTime, controller[player])
                updateMoveForward(player, deltaTime, controller[player])
            } else {
                level.spaceShips[player].isFly = false
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

        if (lifes.none { it > 0 } || level.meteorites.isEmpty())
            newRound()

        if (round == 11)
            return false

        return true
    }

    private fun updateMoveRotate(numberPlayer: Int, deltaTime: Int, controller: Controller) {
        level.spaceShips[numberPlayer].moveRotate(deltaTime, controller)
    }

    private fun updateMoveForward(numberPlayer: Int, deltaTime: Int, controller: Controller) {
        level.spaceShips[numberPlayer].moveForward(deltaTime, controller)
    }

    private fun updatePressFire(numberPlayer: Int, deltaTime: Int) {
        if (level.spaceShips[numberPlayer].inGame)
            level.bullets += Bullet.create(level.spaceShips, numberPlayer)
    }

    private fun updateSpaceShips(deltaTime: Int) {
        for (spaceShip in level.spaceShips)
            if (spaceShip.inGame)
                spaceShip.update(deltaTime, level.width, level.height)

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
        val numberPlayer = level.spaceShips.indexOf(currentSpaceShip)
        if (lifes[numberPlayer] > 0) {
            val respawn = respawns.find(::findFreeRespawn)
            if (respawn != null) {
                level.spaceShips[numberPlayer] = SpaceShip(respawn)
                return true
            }
        }
        return false
    }

    private fun findFreeRespawn(it: Respawn): Boolean {
        for (spaceShip in level.spaceShips)
            if (spaceShip.inGame && overlap(it, spaceShip))
                return false
        for (bullet in level.bullets)
            if (overlap(it, bullet))
                return false
        for (meteorite in level.meteorites)
            if (overlap(it, meteorite))
                return false
        return true
    }

    private fun collisionSpaceShips() {
        if (countPlayers > 1) {
            for (n in 0 until (countPlayers - 1))
                for (k in (n + 1) until countPlayers)
                    if (level.spaceShips[n].inGame && level.spaceShips[k].inGame
                        && overlap(level.spaceShips[n], level.spaceShips[k])
                    ) {
                        kickback(level.spaceShips[n], level.spaceShips[k])
                    }
        }
    }

    private fun updateBullets(deltaTime: Int) {
        if (level.bullets.isNotEmpty()) {
            for (bullet in level.bullets)
                bullet.update(deltaTime, level.width, level.height)

            collisionBulletSpaceShips()
            collisionBulletBullet()

            level.bullets = level.bullets.filter {
                it.roadLength <= 6
            }.toMutableList()
        }
    }

    private fun collisionBulletSpaceShips() {
        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        for (spaceShip in level.spaceShips)
            if (spaceShip.inGame)
                for (bullet in level.bullets)
                    if (overlap(bullet, spaceShip)
                        && level.spaceShips.indexOf(spaceShip) != bullet.player
                    ) {
                        kickback(spaceShip, bullet)

                        animationBulletDestroys.add(AnimationDestroy(bullet))
                        listBulletDestroy.add(bullet)
                    }

        for (bullet in listBulletDestroy) {
            level.bullets.remove(bullet)
        }
    }

    private fun collisionBulletBullet() {
        val listBulletDestroy: MutableList<Bullet> = mutableListOf()

        for (n in 0 until (level.bullets.size - 1))
            for (k in (n + 1) until level.bullets.size)
                if (overlap(level.bullets[n], level.bullets[k])) {
                    animationBulletDestroys.add(AnimationDestroy(level.bullets[k]))
                    listBulletDestroy.add(level.bullets[k])
                }

        for (bullet in listBulletDestroy) {
            level.bullets.remove(bullet)
        }
    }

    private fun updateMeteorites(deltaTime: Int) {
        if (level.meteorites.isNotEmpty()) {
            for (meteorite in level.meteorites)
                meteorite.update(deltaTime, level.width, level.height)

            collisionMeteoriteMeteoriteBulletsSpaceShips()
        }
    }

    private fun collisionMeteoriteMeteoriteBulletsSpaceShips() {
        for (n in 0 until (level.meteorites.size - 1))
            for (k in (n + 1) until level.meteorites.size)
                if (overlap(level.meteorites[n], level.meteorites[k])) {
                    kickback(level.meteorites[n], level.meteorites[k])
                }

        val listMeteoritesDestroy: MutableList<Meteorite> = mutableListOf()
        val listAngleMeteoritesDestroy: MutableList<Double> = mutableListOf()

        val listBulletDestroy: MutableList<Bullet> = mutableListOf()
        for (meteorite in level.meteorites)
            for (bullet in level.bullets)
                if (overlap(bullet, meteorite)) {
                    scores[bullet.player] =
                        scores[bullet.player] + 100 * meteorite.viewSize
                    listMeteoritesDestroy.add(meteorite)
                    listAngleMeteoritesDestroy.add(bullet.angle)

                    animationBulletDestroys.add(AnimationDestroy(bullet))
                    listBulletDestroy.add(bullet)
                }

        for (bullet in listBulletDestroy)
            level.bullets.remove(bullet)

        spaceShipDestroy(listMeteoritesDestroy, listAngleMeteoritesDestroy)

        meteoritesDestroy(listMeteoritesDestroy, listAngleMeteoritesDestroy)
    }

    private fun spaceShipDestroy(
        listMeteoritesDestroy: MutableList<Meteorite>,
        listAngleMeteoritesDestroy: MutableList<Double>
    ) {
        for (spaceShip in level.spaceShips)
            for (meteorite in level.meteorites)
                if (spaceShip.inGame && overlap(spaceShip, meteorite)) {
                    lifes[level.spaceShips.indexOf(spaceShip)] -= 1
                    spaceShip.inGame = false
                    if (lifes[level.spaceShips.indexOf(spaceShip)] > 0)
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

        listMeteoritesFullDestroy.forEach { level.meteorites.remove(it) }
    }

    private fun createThreeMeteorites(
        meteoriteDestroy: Meteorite,
        angleDestroy: Double
    ) {
        for (angle in (0 until 360) step 120) {
            val newMeteorite = meteoriteDestroy.createMeteorite(angleDestroy, angle.toDouble())

            if (checkCanCreateNewMeteoriteIsOverlap(newMeteorite, meteoriteDestroy))
                level.meteorites.add(newMeteorite)
        }
    }

    private fun checkCanCreateNewMeteoriteIsOverlap(
        newMeteorite: Meteorite,
        meteoriteDestroy: Meteorite,
    ): Boolean {
        for (colMeteorite in level.meteorites)
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
        val speedActorX1: Double = moveableActor1.speed.x
        val speedActorX2: Double = moveableActor2.speed.x
        val speedActorY1: Double = moveableActor1.speed.y
        val speedActorY2: Double = moveableActor2.speed.y

        // Вычислить вектор поступательного движения, который является нормальным
        val normal: Vec = moveableActor2.center - moveableActor1.center

        val distSqr: Double = normal.sumPow2()
        val radius: Double = moveableActor1.size/2 + moveableActor2.size/2

        val manifold=Manifold(moveableActor1,moveableActor2)
        // Не в контакте
        if (distSqr >= radius * radius) {
            manifold.contact_count = 0
            return
        }

        val distance: Double = sqrt(distSqr)

        manifold.contact_count = 1

        if (distance == 0.0) {
            manifold.penetration = moveableActor1.size/2
            manifold.normal = Vec(1.0, 0.0)
            manifold.contacts[0] = moveableActor1.center
        } else {
            manifold.penetration = radius - distance
            // Быстрее, чем при использовании нормализованного, так как мы уже выполнили sqrt
            manifold.normal = normal / distance
            manifold.contacts[0] = manifold.normal * moveableActor1.size/2.0 + moveableActor1.center
        }

        moveableActor1.speed.x = Physics.getSpeedFirstAfterKickback(speedActorX1, speedActorX2)
        moveableActor2.speed.x = Physics.getSpeedFirstAfterKickback(speedActorX2, speedActorX1)
        moveableActor1.speed.y = Physics.getSpeedFirstAfterKickback(speedActorY1, speedActorY2)
        moveableActor2.speed.y = Physics.getSpeedFirstAfterKickback(speedActorY2, speedActorY1)
    }

    fun clickPause() {
        status = if (status == "playing")
            "pause"
        else
            "playing"
    }
}
