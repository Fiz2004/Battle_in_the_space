package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.base.data.TypeItems
import com.fiz.android.battleinthespace.base.data.database.PlayerTypeConverters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel(private val playerRepository: PlayerRepository) : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()

    var user = MutableLiveData<FirebaseUser?>(null)

    var playerListLiveData: LiveData<List<Player>> = playerRepository.getPlayers()

    var player: Player = Player(money = 666)

    private var _type = MutableLiveData(0)
    val type: LiveData<Int>
        get() = _type

    private val countPlayer: MutableLiveData<Int> = MutableLiveData(playerRepository.getCountPlayers())

    fun getItems(): List<TypeItems> {
        return player.items
    }

    fun getMoney(): Int {
        return player.money
    }

    fun changeItems(key: Int, type: Int, value: StateProduct) {
        player.items[type].items[key].state = value
    }

    fun fillInitValue() {
        playerRepository.fillInitValue()
    }

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayer.value = numberRadioButton
    }

    fun initPlayer(newPlayer: Player) {
        player = newPlayer
    }

    fun addMoney(value: Int) {
        player.money += value
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

        val player = when (count) {
            1 -> player1
            2 -> player2
            3 -> player3
            else -> player4
        }

        playerRepository.updatePlayer(player)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(
            "countPlayers",
            countPlayer.value!!)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData playerListLiveData")

            outState.putString("name$n", value.name)
            outState.putBoolean("playerControllerPlayer$n", value.controllerPlayer)
            outState.putInt("mission$n", value.mission)
            outState.putString("items$n", PlayerTypeConverters().fromItems(value.items))
        }
    }

    fun getDataForIntent(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayer.value)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData playerListLiveData")

            intent.putExtra("name$n", value.name)
            intent.putExtra("playerControllerPlayer$n", value.controllerPlayer)
            intent.putExtra("mission$n", value.mission)
            intent.putExtra("items$n", PlayerTypeConverters().fromItems(value.items))
        }
        return intent
    }

    fun buyItem(
        index: Int,
        indexType: Int) {
        val money = getMoney()
        if (money - getItems()[indexType].items[index].cost >= 0) {
            player.money -= getItems()[indexType].items[index].cost
            changeItems(index, indexType, StateProduct.BUY)
        }
    }
}

class MainViewModelFactory(private val dataSource: PlayerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dataSource) as T
    }
}