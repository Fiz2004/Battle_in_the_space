package com.fiz.battleinthespace.database.data_source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.fiz.battleinthespace.database.entity.ItemRealm
import com.fiz.battleinthespace.database.entity.PlayerRealm
import com.fiz.battleinthespace.database.entity.TypeItemsRealm
import com.fiz.battleinthespace.database.entity.asPlayer
import com.fiz.battleinthespace.database.utils.LiveRealmResults
import com.fiz.battleinthespace.domain.models.Player
import io.realm.Realm
import io.realm.kotlin.where
import javax.inject.Inject


class PlayersLocalDataSource @Inject constructor(private val databaseRealm: Realm) {

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

    fun get(id: Int): Player? {
        return databaseRealm.where<PlayerRealm>().equalTo("id", id).findFirst()?.asPlayer()
    }

    fun clear() {
        databaseRealm.executeTransactionAsync {
            it.deleteAll()
        }
    }

    private val all = LiveRealmResults(databaseRealm.where<PlayerRealm>().findAll())

    fun getAll(): LiveData<List<Player>> {
        return Transformations.map(all) {
            it?.map {
                it.asPlayer()
            }
        }
    }

    fun getCount(): Int {
        val all = databaseRealm.where<PlayerRealm>().findAll()
        return all.size
    }

    fun close() {
        databaseRealm.close()
    }

    fun save(player: Player) {
        update(player)
    }
}
