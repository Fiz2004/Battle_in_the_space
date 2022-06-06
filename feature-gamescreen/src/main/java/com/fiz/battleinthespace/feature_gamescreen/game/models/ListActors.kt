package com.fiz.battleinthespace.feature_gamescreen.game.models

import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Collision
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Physics
import com.fiz.battleinthespace.feature_gamescreen.game.engine.Vec
import com.fiz.battleinthespace.feature_gamescreen.game.models.weapon.Weapon
import java.io.Serializable


class ListActors(
    val width: Int,
    val height: Int,
    private var players: MutableList<Player>
) : Serializable {

    var spaceShips: MutableList<SpaceShip> = mutableListOf()
    var bullets: MutableList<Weapon> = mutableListOf()
    var meteorites: MutableList<Meteorite> = mutableListOf()
    var bulletsAnimationDestroy: MutableList<BulletAnimationDestroy> = mutableListOf()
    var spaceShipsAnimationDestroy: MutableList<SpaceShipAnimationDestroy> = mutableListOf()

    private var lineSpaceShipsOnRespawn: MutableList<SpaceShip> = mutableListOf()

    // 4 Для равномерного распредления по игровому миру
    private var respawns: MutableList<Respawn> = mutableListOf(
        Respawn(
            Vec(width / 4.0, height / 4.0),
            angle = 45.0
        ),
        Respawn(
            Vec(width - width / 4.0, height / 4.0),
            angle = 135.0
        ),
        Respawn(
            Vec(width / 4.0, height - height / 4.0),
            angle = 315.0
        ),
        Respawn(
            Vec(width - width / 4.0, height - height / 4.0),
            angle = 225.0
        )
    )

    fun createSpaceShips(countPlayers: Int) {
        spaceShips.clear()
        for (n in 0 until countPlayers)
            spaceShips += SpaceShip(respawns[n], playerGame = players[n])
    }

    fun createMeteorites(countMeteorites: Int, playSound: (Int) -> Unit) {
        meteorites.clear()
        for (n in 0 until countMeteorites)
            createMeteorite(playSound)
    }

    private fun createMeteorite(
        playSound: (Int) -> Unit
    ) {
        var x = 0
        while (true) {
            val dx = (0 until width).shuffled().first().toDouble()
            val dy = (0 until height).shuffled().first().toDouble()
            if (isCreateMeteoriteWithoutOverlap(dx, dy, playSound))
                return
            x += 1
            if (x > 1000) throw Error("Превышено время ожидания функции createMeteorite")
        }
    }

    private fun isCreateMeteoriteWithoutOverlap(
        x: Double, y: Double,
        playSound: (Int) -> Unit
    ): Boolean {
        val newMeteorite = Meteorite.createNew(x, y)

        if (meteorites.any { overlap(newMeteorite, it, playSound) })
            return false

        if (respawns.any { overlap(newMeteorite, it, playSound) })
            return false

        meteorites.add(newMeteorite)
        return true
    }

    fun update(deltaTime: Double, playSound: (Int) -> Unit) {
        updateSpaceShips(deltaTime, playSound)
        updateBullets(deltaTime, playSound)
        updateMeteorites(deltaTime, playSound)
    }

    private fun updateSpaceShips(deltaTime: Double, playSound: (Int) -> Unit) {
        for (spaceShip in spaceShips.filter { it.inGame })
            spaceShip.update(deltaTime, width, height)

        lineSpaceShipsOnRespawn =
            lineSpaceShipsOnRespawn.filterNot { isCanRespawn(deltaTime, playSound)(it) }
                .toMutableList()

        findAllCollisionSpaceShipsSpaceShipsAndKickback(playSound)
    }

    private fun isCanRespawn(deltaTime: Double, playSound: (Int) -> Unit): (SpaceShip) -> Boolean {
        return fun(spaceShip: SpaceShip): Boolean {
            return spaceShip.isCanRespawnFromTime(deltaTime) && isRespawnFree(spaceShip, playSound)
        }
    }

    private fun isRespawnFree(currentSpaceShip: SpaceShip, playSound: (Int) -> Unit): Boolean {
        val numberPlayer = spaceShips.indexOf(currentSpaceShip)
        if (players[numberPlayer].life > 0) {
            val respawn = respawns.find { findFreeRespawn(it, playSound) }
            if (respawn != null) {
                spaceShips[numberPlayer].respawn(respawn)
                return true
            }
        }
        return false
    }

    private fun findFreeRespawn(respawn: Respawn, playSound: (Int) -> Unit): Boolean {
        if (spaceShips.filter { it.inGame }.any { overlap(it, respawn, playSound) })
            return false

        if (bullets.any { overlap(it, respawn, playSound) })
            return false

        if (meteorites.any { overlap(it, respawn, playSound) })
            return false

        return true
    }

    private fun findAllCollisionSpaceShipsSpaceShipsAndKickback(playSound: (Int) -> Unit) {
        createCombinations(spaceShips.filter { it.inGame })
            .filter { overlap(it.first, it.second, playSound) }
            .forEach { kickback(it.first, it.second) }
    }

    private fun updateBullets(deltaTime: Double, playSound: (Int) -> Unit) {
        if (bullets.isNotEmpty()) {
            for (bullet in bullets)
                bullet.update(deltaTime, width, height)

            findCollisionSpaceShipsBulletsAndKickbackAndMarkThem(playSound)
            findCollisionBulletBulletAndMarkThem(playSound)

            destroyBullets()
        }
    }

    private fun findCollisionSpaceShipsBulletsAndKickbackAndMarkThem(playSound: (Int) -> Unit) {
        bullets.filter { bullet ->
            val spaceShipsIsCollisionBullet = getSpaceShipsIsCollision(bullet, playSound)
            spaceShipsIsCollisionBullet.forEach { spaceShip ->
                kickback(spaceShip, bullet)
                bullet.inGame = false
            }
            spaceShipsIsCollisionBullet.isEmpty()
        }
    }

    private fun getSpaceShipsIsCollision(
        bullet: Weapon,
        playSound: (Int) -> Unit
    ): List<SpaceShip> {
        return spaceShips.filterIndexed { indexSpaceShip, spaceShip ->
            overlap(spaceShip, bullet, playSound) && bullet.player != indexSpaceShip
        }
    }

    private fun findCollisionBulletBulletAndMarkThem(playSound: (Int) -> Unit) {
        createCombinations(bullets)
            .filter { overlap(it.first, it.second, playSound) }
            .forEach {
                it.first.inGame = false
                it.second.inGame = false
            }

    }

    private fun updateMeteorites(deltaTime: Double, playSound: (Int) -> Unit) {
        meteorites.forEach { meteorite -> meteorite.update(deltaTime, width, height) }

        findCombinationsMeteoritesMeteoritesOverlapAndKickback(playSound)

        val spaceShipsIsCollisionMeteorite = getSpaceShipsIsCollisionMeteorite(playSound)
        addAnimationsDestroySpaceShipFor(spaceShipsIsCollisionMeteorite)

        val mapMeteoritesDestroyAndAngle: MutableMap<Meteorite, Double> =
            getTotalMapMeteoritesDestroyAndAngle(spaceShipsIsCollisionMeteorite, playSound)

        mapMeteoritesDestroyAndAngle.forEach {
            createThreeMeteorites(it.key, it.value, playSound)
            meteorites.remove(it.key)
        }

        destroyBullets()
    }

    private fun findCombinationsMeteoritesMeteoritesOverlapAndKickback(playSound: (Int) -> Unit) {
        createCombinations(meteorites)
            .filter { overlap(it.first, it.second, playSound) }
            .forEach { kickback(it.first, it.second) }
    }

    private fun destroyBullets() {
        bullets.filter { !it.inGame }.forEach { bullet ->
            bulletsAnimationDestroy.add(BulletAnimationDestroy(bullet))
        }
        bullets = bullets.filter { it.inGame }.toMutableList()
        bullets = bullets.filter {
            it.roadLength <= it.roadLengthMax
        }.toMutableList()
    }

    private fun getSpaceShipsIsCollisionMeteorite(playSound: (Int) -> Unit): List<SpaceShip> {
        val result = mutableListOf<SpaceShip>()
        meteorites.forEach { meteorite ->
            result += spaceShips.filter { it.inGame }
                .filter { spaceShip -> overlap(spaceShip, meteorite, playSound) }
        }
        return result
    }

    private fun addAnimationsDestroySpaceShipFor(spaceShipsIsCollisionMeteorite: List<SpaceShip>) {
        spaceShipsIsCollisionMeteorite.forEach { spaceShip ->
            players[spaceShips.indexOf(spaceShip)].life -= 1
            spaceShip.inGame = false
            if (players[spaceShips.indexOf(spaceShip)].life > 0)
                lineSpaceShipsOnRespawn.add(spaceShip)
            spaceShipsAnimationDestroy.add(SpaceShipAnimationDestroy(spaceShip))
        }
    }

    private fun getTotalMapMeteoritesDestroyAndAngle(
        spaceShipsIsCollisionMeteorite: List<SpaceShip>, playSound: (Int) -> Unit
    ): MutableMap<Meteorite, Double> {
        val result: MutableMap<Meteorite, Double> = mutableMapOf()
        meteorites.forEach { meteorite ->
            result += getMapMeteoritesDestroyAndAngleOnCollisionBulletsFor(
                meteorite, playSound
            )
        }

        meteorites.forEach { meteorite ->
            result += getMapMeteoritesDestroyAndAngleOnCollisionSpaceShipsFor(
                spaceShipsIsCollisionMeteorite,
                meteorite,
                playSound
            )
        }
        return result
    }

    private fun getMapMeteoritesDestroyAndAngleOnCollisionBulletsFor(
        meteorite: Meteorite, playSound: (Int) -> Unit
    ): MutableMap<Meteorite, Double> {
        val result = mutableMapOf<Meteorite, Double>()

        bullets.filter { bullet -> overlap(bullet, meteorite, playSound) }.forEach { bullet ->
            meteorite.inGame = false
            bullet.inGame = false
            players[bullet.player].score += meteorite.viewSize + 1
            result[meteorite] = bullet.angle
        }
        return result
    }

    private fun getMapMeteoritesDestroyAndAngleOnCollisionSpaceShipsFor(
        spaceShipsIsCollisionMeteorite: List<SpaceShip>,
        meteorite: Meteorite, playSound: (Int) -> Unit
    ): MutableMap<Meteorite, Double> {
        val result = mutableMapOf<Meteorite, Double>()

        spaceShipsIsCollisionMeteorite.filter { spaceShip ->
            overlap(
                spaceShip,
                meteorite,
                playSound
            )
        }
            .forEach { spaceShip ->
                result[meteorite] = spaceShip.angle
            }

        return result
    }

    private fun createThreeMeteorites(
        meteoriteDestroy: Meteorite,
        angleDestroy: Double,
        playSound: (Int) -> Unit
    ) {
        if (meteoriteDestroy.viewSize + 1 > 3)
            return

        for (angle in (0 until 360) step 120) {
            val newMeteorite = meteoriteDestroy.createMeteorite(angleDestroy, angle.toDouble())

            if (!meteorites.filter { it != meteoriteDestroy }
                    .any { overlap(it, newMeteorite, playSound) })
                meteorites.add(newMeteorite)
        }
    }

    private fun overlap(actor1: Actor, actor2: Actor, playSound: (Int) -> Unit): Boolean {
        val result = Physics.overlapCircle(
            actor1.center, actor1.size,
            actor2.center, actor2.size
        )
        if (result)
            playSound(0)
        return result
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