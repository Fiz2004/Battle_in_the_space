package com.fiz.android.battleinthespace.base.data.database.realm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.module.ItemRealm
import com.fiz.android.battleinthespace.base.data.module.PlayerRealm
import com.fiz.android.battleinthespace.base.data.module.TypeItemsRealm
import com.fiz.android.battleinthespace.base.data.module.asPlayer
import io.realm.Realm
import io.realm.kotlin.where

class PlayerDatabaseRealm(val databaseRealm: Realm) {
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
        databaseRealm.executeTransaction {
            it.insertOrUpdate(playerRealm)
        }
    }

    fun get(id: Int): LiveData<Player?> {
        return MutableLiveData(
            databaseRealm.where<PlayerRealm>().equalTo("id", id).findFirst()?.asPlayer()
        )
    }

    fun clear() {
        val all = databaseRealm.where<PlayerRealm>().findAll()
        all.deleteAllFromRealm()
    }

    fun getAll(): List<PlayerRealm>? {
        val all = databaseRealm.where<PlayerRealm>().findAll()
        return all
    }

    fun getCount(): Int? {
        val all = databaseRealm.where<PlayerRealm>().findAll()
        return all.size
    }
}