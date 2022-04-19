package com.fiz.battleinthespace.feature_mainscreen.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.*
import com.fiz.battleinthespace.database.*

class MainViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    var players: LiveData<List<Player>> = playerRepository.getPlayers(); private set

    private var countPlayer: Int = playerRepository.getCountPlayers()

    var money = Transformations.map(players) { it[0].money }; private set
    var type = MutableLiveData(0); private set


    fun getItems(): List<TypeItems> {
        return players.value?.get(0)?.items ?: listOf()
    }

    private fun changeItems(key: Int, type: Int, value: StateProduct) {
        val items = players.value?.get(0)?.items?.toMutableList() ?: return
        items[type].items[key].state = value
        playerRepository.save(players.value?.get(0)?.copy(items = items.toList()))
    }

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayer = numberRadioButton
    }

    fun eventClickOnMission(value: Int) {
        playerRepository.save(players.value?.get(0)?.copy(mission = value))
    }

    fun savePlayers() {
        playerRepository.saveCountPlayers(countPlayer)
        for (n in 0 until countPlayer)
            playerRepository.updatePlayer(players.value?.get(n))
    }

    fun countPlayerLiveDataEquals(value: Int): Boolean {
        return countPlayer == value
    }

    fun countPlayerLiveDataCompare(value: Int): Boolean {
        return countPlayer >= value
    }

    fun onClickReset(count: Int) {
        val player1 = Player(id = 0, name = "Player 1")
        val player2 = Player(
            id = 1,
            name = "Player 2",
            controllerPlayer = false
        )
        val player3 = Player(
            id = 2,
            name = "Player 3",
            controllerPlayer = false
        )
        val player4 = Player(
            id = 3,
            name = "Player 4",
            controllerPlayer = false
        )

        players.value?.get(0)?.controllerPlayer = true
        players.value?.get(1)?.controllerPlayer = false
        players.value?.get(2)?.controllerPlayer = false
        players.value?.get(3)?.controllerPlayer = false

        val player = when (count) {
            1 -> player1
            2 -> player2
            3 -> player3
            else -> player4
        }

        playerRepository.updatePlayer(player)
    }

    fun getController(index: Int): Boolean {
        return players.value?.get(index)?.controllerPlayer ?: false
    }

    fun addSaveInstanceState(outState: Bundle) {
        outState.putInt(
            "countPlayers",
            countPlayer
        )
        for (n in 0 until 4) {
            val value = players.value?.get(n)
                ?: throw Error("Не доступна LiveData playerListLiveData")

            outState.putString("name$n", value.name)
            outState.putBoolean("playerControllerPlayer$n", value.controllerPlayer)
            outState.putInt("mission$n", value.mission)
//            outState.putString("items$n", PlayerTypeConverters().fromItems(value.items))
        }
    }

    fun clickTypeItem(value: Int) {
        type.postValue(value)
    }

    fun getDataForIntent(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayer)
        for (n in 0 until 4) {
            val value = players.value?.get(n)
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
        val money = players.value?.get(0)?.money
        if (money != null) {
            if (money - getItems()[indexType].items[index].cost >= 0) {
                playerRepository.save(
                    players.value?.get(0)
                        ?.copy(money = players.value?.get(0)?.money?.minus(getItems()[indexType].items[index].cost)!!)
                )
                changeItems(index, indexType, StateProduct.BUY)
            }
        }
    }

    fun gameActivityFinish(intent: Intent) {
        val score = intent.getIntExtra("score", 0)
        players.value?.get(0)?.money = players.value?.get(0)?.money?.plus(score)!!
        savePlayers()
    }

    fun initPlayerIfFirstStart() {
        if (players.value == null || players.value!!.isEmpty())
            playerRepository.fillInitValue()
    }

    fun clickItems(position: Int) {
        if (position == 0) {
            this.type.postValue(0)
        } else {
            val type = this.type.value ?: return
            val indexType = type - 1
            val listProduct =
                Item.addZeroFirstItem(getItems()[indexType].items)
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

    fun getItemsWithZero(): List<Item> {
        val type = this.type.value ?: return listOf()
        val indexType = type - 1
        val items = players.value?.get(0)?.items ?: listOf()
        return Item.addZeroFirstItem(items[indexType].items)
    }

    override fun onCleared() {
        super.onCleared()
        playerRepository.close()
    }
}

class MainViewModelFactory(private val playerRepository: PlayerRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(playerRepository) as T
    }
}