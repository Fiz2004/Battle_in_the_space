package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.engine.Collision
import com.fiz.android.battleinthespace.engine.Physics
import com.fiz.android.battleinthespace.engine.Vec

class ListActors(
    val width: Double,
    val height: Double,
    var players: MutableList<Player>
) {
    var actors: MutableList<MoveableActor> = mutableListOf()
        get() {
            field.clear()
            field.addAll(spaceShips)
            field.addAll(bullets)
            field.addAll(meteorites)
            return field
        }

    var spaceShips: MutableList<SpaceShip> = mutableListOf()
    var bullets: MutableList<Bullet> = mutableListOf()
    var meteorites: MutableList<Meteorite> = mutableListOf()
    var listAnimationDestroy: MutableList<DrawableAnimationDestroy> = mutableListOf()

    private var lineSpaceShipsOnRespawn: MutableList<SpaceShip> = mutableListOf()

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

    fun createSpaceShips(countPlayers: Int) {
        for (n in 0 until countPlayers)
            spaceShips += SpaceShip(respawns[n], player = n)
    }

    fun createMeteorites(countMeteorites: Int) {
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

        if (meteorites.any { overlap(newMeteorite, it) })
            return false

        if (respawns.any { overlap(newMeteorite, it) })
            return false

        meteorites.add(newMeteorite)
        return true
    }

    fun update(deltaTime: Double) {
        updateSpaceShips(deltaTime)
        updateBullets(deltaTime)
        updateMeteorites(deltaTime)
    }

    private fun updateSpaceShips(deltaTime: Double) {
        for (spaceShip in spaceShips.filter { it.inGame })
            spaceShip.update(deltaTime, width, height)

        lineSpaceShipsOnRespawn = lineSpaceShipsOnRespawn.filterNot { isCanRespawn(deltaTime)(it) }.toMutableList()

        findAllCollisionSpaceShipsSpaceShipsAndKickback()
    }

    private fun isCanRespawn(deltaTime: Double): (SpaceShip) -> Boolean {
        return fun(spaceShip: SpaceShip): Boolean {
            return spaceShip.isCanRespawnFromTime(deltaTime) && isRespawnFree(spaceShip)
        }
    }

    private fun isRespawnFree(currentSpaceShip: SpaceShip): Boolean {
        val numberPlayer = spaceShips.indexOf(currentSpaceShip)
        if (players[numberPlayer].life > 0) {
            val respawn = respawns.find(::findFreeRespawn)
            if (respawn != null) {
                spaceShips[numberPlayer].respawn(respawn)
                return true
            }
        }
        return false
    }

    private fun findFreeRespawn(respawn: Respawn): Boolean {
        if (spaceShips.filter { it.inGame }.any { overlap(it, respawn) })
            return false

        if (bullets.any { overlap(it, respawn) })
            return false

        if (meteorites.any { overlap(it, respawn) })
            return false

        return true
    }

    private fun findAllCollisionSpaceShipsSpaceShipsAndKickback() {
        createCombinations(spaceShips.filter { it.inGame })
            .filter { overlap(it.first, it.second) }
            .forEach { kickback(it.first, it.second) }
    }

    private fun updateBullets(deltaTime: Double) {
        if (bullets.isNotEmpty()) {
            for (bullet in bullets)
                bullet.update(deltaTime, width, height)

            findCollisionSpaceShipsBulletsAndKickbackAndMarkThem()
            findCollisionBulletBulletAndMarkThem()

            destroyBullets()
        }
    }

    private fun findCollisionSpaceShipsBulletsAndKickbackAndMarkThem() {
        bullets.filter { bullet ->
            val spaceShipsIsCollisionBullet = getSpaceShipsIsCollision(bullet)
            spaceShipsIsCollisionBullet.forEach { spaceShip ->
                kickback(spaceShip, bullet)
                bullet.inGame = false
            }
            spaceShipsIsCollisionBullet.isEmpty()
        }
    }

    private fun getSpaceShipsIsCollision(bullet: Bullet): List<SpaceShip> {
        return spaceShips.filterIndexed { indexSpaceShip, spaceShip ->
            overlap(spaceShip, bullet) && bullet.player != indexSpaceShip
        }
    }

    private fun findCollisionBulletBulletAndMarkThem() {
        createCombinations(bullets)
            .filter { overlap(it.first, it.second) }
            .forEach {
                it.first.inGame = false
                it.second.inGame = false
            }

    }

    private fun updateMeteorites(deltaTime: Double) {
        meteorites.forEach { meteorite -> meteorite.update(deltaTime, width, height) }

        findCombinationsMeteoritesMetioritesOverlapAndKickback()

        val spaceShipsIsCollisionMeteorite = getSpaceShipsIsCollisionMeteorite()
        addAnimationsDestroySpaceShipFor(spaceShipsIsCollisionMeteorite)

        val mapMeteoritesDestroyAndAngle: MutableMap<Meteorite, Double> =
            getTotalMapMeteoritesDestroyAndAngle(spaceShipsIsCollisionMeteorite)

        mapMeteoritesDestroyAndAngle.forEach {
            createThreeMeteorites(it.key, it.value)
            meteorites.remove(it.key)
        }

        destroyBullets()
    }

    private fun findCombinationsMeteoritesMetioritesOverlapAndKickback() {
        createCombinations(meteorites)
            .filter { overlap(it.first, it.second) }
            .forEach { kickback(it.first, it.second) }
    }

    private fun destroyBullets() {
        bullets.filter { !it.inGame }.forEach { bullet ->
            listAnimationDestroy.add(BulletAnimationDestroy(bullet))
        }
        bullets = bullets.filter { it.inGame }.toMutableList()
        bullets = bullets.filter {
            it.roadLength <= Bullet.roadLengthMax
        }.toMutableList()
    }

    private fun getSpaceShipsIsCollisionMeteorite(): List<SpaceShip> {
        val result = mutableListOf<SpaceShip>()
        meteorites.forEach { meteorite ->
            result += spaceShips.filter { it.inGame }
                .filter { spaceShip -> overlap(spaceShip, meteorite) }
        }
        return result
    }

    private fun addAnimationsDestroySpaceShipFor(spaceShipsIsCollisionMeteorite: List<SpaceShip>) {
        spaceShipsIsCollisionMeteorite.forEach { spaceShip ->
            players[spaceShips.indexOf(spaceShip)].life -= 1
            spaceShip.inGame = false
            if (players[spaceShips.indexOf(spaceShip)].life > 0)
                lineSpaceShipsOnRespawn.add(spaceShip)
            listAnimationDestroy.add(SpaceShipAnimationDestroy(spaceShip))
        }
    }

    private fun getTotalMapMeteoritesDestroyAndAngle(
        spaceShipsIsCollisionMeteorite: List<SpaceShip>
    ): MutableMap<Meteorite, Double> {
        val result: MutableMap<Meteorite, Double> = mutableMapOf()
        meteorites.forEach { meteorite ->
            result += getMapMeteoritesDestroyAndAngleOnCollisionBulletsFor(
                meteorite
            )
        }

        meteorites.forEach { meteorite ->
            result += getMapMeteoritesDestroyAndAngleOnCollisionSpaceShipsFor(
                spaceShipsIsCollisionMeteorite,
                meteorite
            )
        }
        return result
    }

    private fun getMapMeteoritesDestroyAndAngleOnCollisionBulletsFor(
        meteorite: Meteorite
    ): MutableMap<Meteorite, Double> {
        val result = mutableMapOf<Meteorite, Double>()

        bullets.filter { bullet -> overlap(bullet, meteorite) }.forEach { bullet ->
            meteorite.inGame = false
            bullet.inGame = false
            players[bullet.player].score += meteorite.viewSize + 1
            result[meteorite] = bullet.angle
        }
        return result
    }

    private fun getMapMeteoritesDestroyAndAngleOnCollisionSpaceShipsFor(
        spaceShipsIsCollisionMeteorite: List<SpaceShip>,
        meteorite: Meteorite
    ): MutableMap<Meteorite, Double> {
        val result = mutableMapOf<Meteorite, Double>()

        spaceShipsIsCollisionMeteorite.filter { spaceShip -> overlap(spaceShip, meteorite) }
            .forEach { spaceShip ->
                result[meteorite] = spaceShip.angle
            }

        return result
    }

    private fun createThreeMeteorites(
        meteoriteDestroy: Meteorite,
        angleDestroy: Double
    ) {
        if (meteoriteDestroy.viewSize + 1 > 3)
            return

        for (angle in (0 until 360) step 120) {
            val newMeteorite = meteoriteDestroy.createMeteorite(angleDestroy, angle.toDouble())

            if (!meteorites.filter { it != meteoriteDestroy }.any { overlap(it, newMeteorite) })
                meteorites.add(newMeteorite)
        }
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

    private fun <K> createCombinations(list: List<K>): MutableList<Pair<K, K>> {
        val result = mutableListOf<Pair<K, K>>()
        for (n in 0 until (list.size - 1))
            for (k in (n + 1) until list.size)
                result += Pair(list[n], list[k])
        return result
    }
}