package com.fiz.android.battleinthespace.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.android.battleinthespace.data.Player
import com.fiz.android.battleinthespace.database.PlayerRepository

class MainViewModel : ViewModel() {
    private val playerRepository = PlayerRepository.get()

    private var _playerListLiveData = MutableLiveData(playerRepository.getPlayers())
    val playerListLiveData: LiveData<List<Player>>
        get() = _playerListLiveData

    private var _playerLiveData =
        MutableLiveData(playerListLiveData.value?.find { it.id == 0 })
    val playerLiveData: LiveData<Player>
        get() = _playerLiveData as LiveData<Player>

    var money: MutableLiveData<Int> = MutableLiveData<Int>(_playerLiveData.value?.money)

    private val _countPlayerLiveData = MutableLiveData<Int>(4) /*playerRepository.getCountPlayer()*/
    val countPlayerLiveData: LiveData<Int>
        get() = _countPlayerLiveData

    private val _type = MutableLiveData(0)
    val type: LiveData<Int>
        get() = _type

    fun setCountPlayers(numberRadioButton: Int) {
        _countPlayerLiveData.value = numberRadioButton
    }

    fun countPlayerMore(count: Int): Boolean {
        if (countPlayerLiveData.value!! >= count)
            return true
        return false
    }

    fun setType(value: Int) {
        _type.value = value
    }

    fun savePlayers() {
        val player = playerLiveData.value!!
        playerRepository.updatePlayer(player)
    }

    fun onClickReset(count: Int) {
        val player1 = Player(id = 0, name = "Player 1")
        val player2 = Player(id = 1, name = "Player 2", controllerPlayer = false)
        val player3 = Player(id = 2, name = "Player 3", controllerPlayer = false)
        val player4 = Player(id = 3, name = "Player 4", controllerPlayer = false)

        val player = when (count) {
            1 -> {
                player1
            }
            2 -> {
                player2
            }
            3 -> {
                player3
            }
            else -> {
                player4
            }
        }
        _playerListLiveData.value?.get(count - 1)?.reset(player)
        _playerLiveData =
            MutableLiveData(playerListLiveData.value?.find { it.id == 0 })
        money.value = _playerLiveData.value?.money
        _type.value = 0
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("countPlayers", countPlayerLiveData.value ?: 4)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData")

            outState.putString("name$n", value.name)
            outState.putBoolean("playerControllerPlayer$n", value.controllerPlayer)
            outState.putInt("mission$n", value.mission)
            outState.putSerializable("items$n", value.items)
        }
    }

    fun onClickDone(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayerLiveData.value)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData")

            intent.putExtra("name$n", value.name)
            intent.putExtra("playerControllerPlayer$n", value.controllerPlayer)
            intent.putExtra("mission$n", value.mission)
            intent.putExtra("items$n", value.items)
        }
        return intent
    }

    fun configureMoney(cost: Int) {
        money.value = money.value?.minus(cost) ?: 0
        playerLiveData.value?.money = money.value!!
    }
}