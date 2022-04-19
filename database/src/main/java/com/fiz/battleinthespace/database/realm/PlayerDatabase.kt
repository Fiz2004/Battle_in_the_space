package com.fiz.battleinthespace.database.realm

import com.fiz.battleinthespace.database.Player
import com.fiz.battleinthespace.database.module.ItemRealm
import com.fiz.battleinthespace.database.module.PlayerRealm
import com.fiz.battleinthespace.database.module.TypeItemsRealm
import io.realm.Realm
import io.realm.kotlin.where

class PlayerDatabaseRealm(private val databaseRealm: Realm) {
    fun addPlayer(player: Player) {
        update(player)
    }

    fun update(player: Player) {
        val playerRealm = PlayerRealm()
        playerRealm.id = player.id
        playerRealm.name = player.name
        playerRealm.controllerPlayer = player.controllerPlayer
        playerRealm.mission = player.mission
        playerRealm.money = player.money
        player.items.forEach { tI ->
            val typeItem = TypeItemsRealm()
            typeItem.id = player.id
            typeItem.nameId = tI.name
            typeItem.imageId = tI.imageId

            tI.items.forEach {
                val item = ItemRealm()
                item.id = player.id
                item.nameId = it.name
                item.imageId = it.imageId
                item.cost = it.cost
                item.state?.stateProduct = it.state
                typeItem.items.add(item)
            }

            playerRealm.items.add(typeItem)

        }
        databaseRealm.executeTransactionAsync {
            it.insertOrUpdate(playerRealm)
        }
    }

    fun get(id: Int): PlayerRealm? {
        return databaseRealm.where<PlayerRealm>().equalTo("id", id).findFirst()
    }

    fun clear() {
        databaseRealm.executeTransactionAsync {
            it.deleteAll()
        }
    }

    fun getAll(): List<PlayerRealm>? {
        return databaseRealm.where<PlayerRealm>().findAll()
    }

    fun getCount(): Int {
        val all = databaseRealm.where<PlayerRealm>().findAll()
        return all.size
    }
}