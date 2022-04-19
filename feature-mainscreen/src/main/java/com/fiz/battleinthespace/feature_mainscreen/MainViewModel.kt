package com.fiz.battleinthespace.feature_mainscreen

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.battleinthespace.database.PlayerRepository
import com.fiz.battleinthespace.database.StateProduct
import com.fiz.battleinthespace.database.TypeItems
import com.fiz.battleinthespace.database.module.asPlayer
import com.fiz.battleinthespace.database.storage.SharedPrefPlayerStorage
import io.realm.Realm
import io.realm.RealmChangeListener

class MainViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    var players: List<com.fiz.battleinthespace.database.Player>?

    init {
        val list = mutableListOf<com.fiz.battleinthespace.database.Player>()
        for (player in playerRepository.getPlayers()!!)
            list.add(player.asPlayer())
        players = list
    }

    var type: Int = 0

    private var countPlayer: Int = playerRepository.getCountPlayers()

    private var realmListener: RealmChangeListener<Realm> = RealmChangeListener {
        val list = mutableListOf<com.fiz.battleinthespace.database.Player>()
        for (player in playerRepository.getPlayers()!!)
            list.add(player.asPlayer())
        players = list
    }

    init {
        playerRepository.databaseRealm.addChangeListener(realmListener)
    }

    fun getItems(): List<TypeItems> {
        return players?.get(0)?.items!!
    }

    private fun changeItems(key: Int, type: Int, value: StateProduct) {
        players?.get(0)?.items?.get(type)?.items?.get(key)?.state = value
    }

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayer = numberRadioButton
    }

    fun changeMission(value: Int) {
        players?.get(0)?.mission = value
    }

    fun savePlayers() {
        playerRepository.saveCountPlayers(countPlayer)
        for (n in 0 until countPlayer)
            playerRepository.updatePlayer(players?.get(n)!!)
    }

    fun countPlayerLiveDataEquals(value: Int): Boolean {
        return countPlayer == value
    }

    fun countPlayerLiveDataCompare(value: Int): Boolean {
        return countPlayer >= value
    }

    fun onClickReset(count: Int) {
        val player1 = com.fiz.battleinthespace.database.Player(id = 0, name = "Player 1")
        val player2 = com.fiz.battleinthespace.database.Player(
            id = 1,
            name = "Player 2",
            controllerPlayer = false
        )
        val player3 = com.fiz.battleinthespace.database.Player(
            id = 2,
            name = "Player 3",
            controllerPlayer = false
        )
        val player4 = com.fiz.battleinthespace.database.Player(
            id = 3,
            name = "Player 4",
            controllerPlayer = false
        )

        players?.get(0)?.controllerPlayer = true
        players?.get(1)?.controllerPlayer = false
        players?.get(2)?.controllerPlayer = false
        players?.get(3)?.controllerPlayer = false

        val player = when (count) {
            1 -> player1
            2 -> player2
            3 -> player3
            else -> player4
        }

        playerRepository.updatePlayer(player)
    }

    fun getController(index: Int): Boolean {
        return players?.get(index)?.controllerPlayer!!
    }

    fun addSaveInstanceState(outState: Bundle) {
        outState.putInt(
            "countPlayers",
            countPlayer
        )
        for (n in 0 until 4) {
            val value = players?.get(n)
                ?: throw Error("Не доступна LiveData playerListLiveData")

            outState.putString("name$n", value.name)
            outState.putBoolean("playerControllerPlayer$n", value.controllerPlayer)
            outState.putInt("mission$n", value.mission)
//            outState.putString("items$n", PlayerTypeConverters().fromItems(value.items))
        }
    }

    fun clickTypeItem(value: Int) {
        type = value
    }

    fun getDataForIntent(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayer)
        for (n in 0 until 4) {
            val value = players?.get(n)
                ?: throw Error("Не доступна LiveData playerListLiveData")

            intent.putExtra("name$n", value.name)
            intent.putExtra("playerControllerPlayer$n", value.controllerPlayer)
            intent.putExtra("mission$n", value.mission)
//            intent.putExtra("items$n", PlayerTypeConverters().fromItems(value.items))
        }
        return intent
    }

    private fun buyItem(
        index: Int,
        indexType: Int
    ) {
        val money = players?.get(0)?.money
        if (money != null) {
            if (money - getItems()[indexType].items[index].cost >= 0) {
                players?.get(0)?.money =
                    players?.get(0)?.money?.minus(getItems()[indexType].items[index].cost)!!
                changeItems(index, indexType, StateProduct.BUY)
            }
        }
    }

    fun gameActivityFinish(intent: Intent) {
        val score = intent.getIntExtra("score", 0)
        players?.get(0)?.money = players?.get(0)?.money?.plus(score)!!
        savePlayers()
    }

    fun initPlayerIfFirstStart() {
        if (players == null || players!!.isEmpty())
            playerRepository.fillInitValue()
    }

    fun clickItems(position: Int, type: Int) {
        if (position == 0) {
            this.type = 0
        } else {
            val indexType = type - 1
            val listProduct =
                com.fiz.battleinthespace.database.Item.addZeroFirstItem(getItems()[indexType].items)
            if (listProduct[position].state == StateProduct.BUY) {
                listProduct.forEachIndexed { index, it ->
                    if (it.state == StateProduct.INSTALL)
                        changeItems(index - 1, indexType, StateProduct.BUY)
                }
                changeItems(position - 1, indexType, StateProduct.INSTALL)
            } else {
                buyItem(position - 1, indexType)
            }
        }
    }

    fun getItemsWithZero(type: Int): List<com.fiz.battleinthespace.database.Item> {
        val indexType = type - 1
        val items = players?.get(0)?.items!!
        return com.fiz.battleinthespace.database.Item.addZeroFirstItem(items[indexType].items)
    }

    override fun onCleared() {
        super.onCleared()
        playerRepository.databaseRealm.removeChangeListener { realmListener }
        playerRepository.close()
    }

    fun getMoney(): Int {
        return players?.get(0)?.money ?: 0
    }
}

class MainViewModelFactory(sharedPrefPlayerStorage: SharedPrefPlayerStorage) :
    ViewModelProvider.Factory {
    private val dataSource = PlayerRepository(sharedPrefPlayerStorage)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dataSource) as T
    }
}