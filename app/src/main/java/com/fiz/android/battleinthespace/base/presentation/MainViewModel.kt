package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.*
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class MainViewModel(private val playerRepository: PlayerRepository) : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()

    var email = MutableLiveData<FirebaseUser?>(null)

    var playerListLiveData: LiveData<List<Player>> = playerRepository.getPlayers()

    var player: Player = Player(money = 200)

    private val _countPlayerLiveData = MutableLiveData(playerRepository.getCountPlayers())

    val countPlayerLiveData: LiveData<Int>
        get() = _countPlayerLiveData

    private var _type = 0
    val type: Int
        get() = _type

    fun fillInitValue() {
        playerRepository.fillInitValue()
    }

    fun setCountPlayers(numberRadioButton: Int) {
        _countPlayerLiveData.value = numberRadioButton
    }

    fun setType(value: Int) {
        _type = value
    }

    fun savePlayers() {
        playerRepository.saveCountPlayers(countPlayerLiveData.value ?: 4)
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
        viewModelScope.launch {
            playerRepository.updatePlayer(player)
        }
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
        player.money -= cost
    }
}

class MainViewModelFactory(private val dataSource: PlayerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dataSource) as T
    }
}