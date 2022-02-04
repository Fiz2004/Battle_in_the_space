package com.fiz.android.battleinthespace.interfaces.main

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiz.android.battleinthespace.options.Player
import com.fiz.android.battleinthespace.options.PlayerRepository
import com.fiz.android.battleinthespace.options.Records

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val playerRepository = PlayerRepository.get()

    var playerListLiveData = MutableLiveData<List<Player>>(playerRepository.getPlayers())

    var countPlayerLiveData = MutableLiveData(playerRepository.getCountPlayer())

    var type = MutableLiveData(0)

    var records: Records = Records()

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayerLiveData.value = numberRadioButton
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
        outState.putSerializable(Records::class.java.simpleName, records)
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
        intent.putExtra(Records::class.java.simpleName, records)
        return intent
    }

    fun configureMoney(cost: Int) {
        val newValue = playerListLiveData.value
        newValue?.get(0)?.money = playerListLiveData.value?.get(0)?.money?.minus(cost) ?: 0
        playerListLiveData.value = newValue
    }
}