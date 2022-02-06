package com.fiz.android.battleinthespace.interfaces.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.android.battleinthespace.options.Player
import com.fiz.android.battleinthespace.options.PlayerRepository

class MainViewModel : ViewModel() {
    private val playerRepository = PlayerRepository.get()

    private val _playerListLiveData = MutableLiveData(playerRepository.getPlayers())
    val playerListLiveData: LiveData<List<Player>>
        get() = _playerListLiveData

    private val _playerLiveData =
        MutableLiveData(playerRepository.getPlayer(0) ?: throw Error("Не могу загрузить данные о игроке"))
    val playerLiveData: LiveData<Player>
        get() = _playerLiveData

    val money: MutableLiveData<Int> = MutableLiveData<Int>(_playerLiveData.value?.money)

    private val _countPlayerLiveData = MutableLiveData<Int>(4) /*playerRepository.getCountPlayer()*/
    val countPlayerLiveData: LiveData<Int>
        get() = _countPlayerLiveData

    private val _type = MutableLiveData(0)
    val type: LiveData<Int>
        get() = _type

    fun setCountPlayers(numberRadioButton: Int) {
        _countPlayerLiveData.value = numberRadioButton
    }

    fun setType(value: Int) {
        _type.value = value
    }

    fun savePlayers() {
        val player = playerLiveData.value!!
        playerRepository.updatePlayer(player)
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