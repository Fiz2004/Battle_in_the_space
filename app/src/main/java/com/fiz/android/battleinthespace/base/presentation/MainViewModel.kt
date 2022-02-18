package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.base.data.*
import com.fiz.android.battleinthespace.base.data.module.PlayerRealm
import com.fiz.android.battleinthespace.base.data.module.asPlayer

class MainViewModel(
    private val playerRepository: PlayerRepository) : ViewModel() {

    var playerListLiveData: LiveData<List<PlayerRealm>?> =
        MutableLiveData(playerRepository.getPlayers())

    var player: Player = Player(money = 666)

    var controllerPlayer: MutableLiveData<List<Boolean>> = MutableLiveData(listOf(true, false, false, false))

    private var _type = MutableLiveData(0)
    val type: LiveData<Int>
        get() = _type

    private var _money = MutableLiveData(player.money)
    val money: LiveData<Int>
        get() = _money

    private val countPlayer: MutableLiveData<Int> = MutableLiveData(playerRepository.getCountPlayers())

    fun getItems(): List<TypeItems> {
        return player.items
    }

    private fun changeItems(key: Int, type: Int, value: StateProduct) {
        player.items[type].items[key].state = value
    }

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayer.value = numberRadioButton
    }

    fun changeMission(value: Int) {
        player.mission = value
    }

    fun setType(value: Int) {
        _type.value = value

    }

    fun savePlayers() {
        playerRepository.saveCountPlayers(countPlayer.value!!)
        playerRepository.updatePlayer(player)
    }

    fun countPlayerLiveDataEquals(value: Int): Boolean {
        return countPlayer.value == value
    }

    fun countPlayerLiveDataCompare(value: Int): Boolean {
        return countPlayer.value!! >= value
    }

    fun onClickReset(count: Int) {
        val player1 = Player(id = 0, name = "Player 1")
        val player2 = Player(id = 1, name = "Player 2", controllerPlayer = false)
        val player3 = Player(id = 2, name = "Player 3", controllerPlayer = false)
        val player4 = Player(id = 3, name = "Player 4", controllerPlayer = false)

        controllerPlayer.value = listOf(true, false, false, false)

        val player = when (count) {
            1 -> player1
            2 -> player2
            3 -> player3
            else -> player4
        }

        playerRepository.updatePlayer(player)
    }

    fun addSaveInstanceState(outState: Bundle) {
        outState.putInt(
            "countPlayers",
            countPlayer.value!!)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData playerListLiveData")

            outState.putString("name$n", value.name)
            outState.putBoolean("playerControllerPlayer$n", value.controllerPlayer)
            outState.putInt("mission$n", value.mission)
//            outState.putString("items$n", PlayerTypeConverters().fromItems(value.items))
        }
    }

    fun getDataForIntent(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayer.value)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData playerListLiveData")

            intent.putExtra("name$n", value.name)
            intent.putExtra("playerControllerPlayer$n", value.controllerPlayer)
            intent.putExtra("mission$n", value.mission)
//            intent.putExtra("items$n", PlayerTypeConverters().fromItems(value.items))
        }
        return intent
    }

    private fun buyItem(
        index: Int,
        indexType: Int) {
        val money = player.money
        if (money - getItems()[indexType].items[index].cost >= 0) {
            player.money -= getItems()[indexType].items[index].cost
            _money.value = player.money
            changeItems(index, indexType, StateProduct.BUY)
        }
    }

    fun gameActivityFinish(intent: Intent) {
        val score = intent.getIntExtra("score", 0)
        player.money += score
        _money.value = player.money
        savePlayers()
    }

    fun refreshPlayerListLiveData(playerList: List<PlayerRealm>?) {
        if (playerList == null || playerList.isEmpty())
            playerRepository.fillInitValue()
        else
            player = playerList[0].asPlayer()
        _money.value = player.money
    }

    fun clickItems(position: Int) {
        if (position == 0) {
            setType(0)
        } else {
            val indexType = type.value?.minus(1) ?: throw Error("Не доступна Livedata type")
            val listProduct = Item.addZeroFirstItem(getItems()[indexType].items)
            if (listProduct[position].state == StateProduct.BUY) {
                listProduct.forEachIndexed { index, it ->
                    if (it.state == StateProduct.INSTALL)
                        changeItems(index - 1, indexType, StateProduct.BUY)
                }
                changeItems(position - 1, indexType, StateProduct.INSTALL)
            } else {
                buyItem(position - 1, indexType)
            }
            setType(type.value!!)
        }
    }

    fun getItemsWithZero(): List<Item> {
        val indexType = type.value?.minus(1) ?: throw Error("Не доступна Livedata type")
        val items = player.items
        return Item.addZeroFirstItem(items[indexType].items)
    }
}

class MainViewModelFactory : ViewModelProvider.Factory {
    private val dataSource = PlayerRepository.get()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dataSource) as T
    }
}