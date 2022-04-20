package com.fiz.battleinthespace.database.data_source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.fiz.battleinthespace.database.LiveRealmResults
import com.fiz.battleinthespace.database.Player
import com.fiz.battleinthespace.database.module.ItemRealm
import com.fiz.battleinthespace.database.module.PlayerRealm
import com.fiz.battleinthespace.database.module.TypeItemsRealm
import com.fiz.battleinthespace.database.module.asPlayer
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where


private const val DATABASE_NAME = "BITS.realm"

class PlayersLocalDataSource {
    private val config = RealmConfiguration.Builder()
        .name(DATABASE_NAME)
        .allowWritesOnUiThread(true)
        .allowQueriesOnUiThread(true)
        .build()

    private val databaseRealm: Realm = Realm.getInstance(config)

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
