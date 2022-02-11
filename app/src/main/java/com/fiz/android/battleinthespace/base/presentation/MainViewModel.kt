package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.*
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel(private val playerRepository: PlayerRepository) : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()

    var user = MutableLiveData<FirebaseUser?>(null)

    var playerListLiveData: LiveData<List<Player>> = playerRepository.getPlayers()

    private var player: MutableLiveData<Player> = Transformations.map(playerListLiveData) {
        it[0]
    } as MutableLiveData<Player>

    var items: MutableLiveData<HashMap<Int, StateProduct>> = Transformations.map(player) {
        it.items
    } as MutableLiveData<HashMap<Int, StateProduct>>

    private var _money: MutableLiveData<Int> = Transformations.map(player) {
        it.money
    } as MutableLiveData<Int>
    val money: LiveData<Int>
        get() = _money

    private var _mission: MutableLiveData<Int> = Transformations.map(player) {
        it.mission
    } as MutableLiveData<Int>
    val mission: LiveData<Int>
        get() = _mission

    private val countPlayerLiveData: MutableLiveData<Int> = Transformations.map(playerListLiveData) {
        it.size
    } as MutableLiveData<Int>

    private var _type = MutableLiveData(0)
    val type: LiveData<Int>
        get() = _type

    fun changeItems(Key: Int, value: StateProduct) {
        items.value?.set(Key, value)
    }

    fun fillInitValue() {
        playerRepository.fillInitValue()
    }

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayerLiveData.value = numberRadioButton
    }

    fun initPlayer(newPlayer: Player) {
        player.value = newPlayer
    }

    fun addMoney(value: Int) {
        _money.value = money.value?.plus(value)
    }

    fun changeMission(value: Int) {
        _mission.value = value
    }

    fun setType(value: Int) {
        _type.value = value
    }

    fun savePlayers() {
        playerRepository.saveCountPlayers(countPlayerLiveData.value ?: 4)
        player.value?.let { playerRepository.updatePlayer(it) }
    }

    fun countPlayerLiveDataEquals(value: Int): Boolean {
        return countPlayerLiveData.value == value
    }

    fun countPlayerLiveDataCompare(value: Int): Boolean {
        return countPlayerLiveData.value!! >= value
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
            countPlayerLiveData.value ?: throw Error("Не доступна LiveData countPlayerLiveData"))
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData playerListLiveData")

            outState.putString("name$n", value.name)
            outState.putBoolean("playerControllerPlayer$n", value.controllerPlayer)
            outState.putInt("mission$n", value.mission)
            outState.putSerializable("items$n", value.items)
        }
    }

    fun getDataForIntent(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayerLiveData.value)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData playerListLiveData")

            intent.putExtra("name$n", value.name)
            intent.putExtra("playerControllerPlayer$n", value.controllerPlayer)
            intent.putExtra("mission$n", value.mission)
            intent.putExtra("items$n", value.items)
        }
        return intent
    }

    fun moneyMinus(cost: Int) {
        _money.value = money.value?.minus(cost)
    }
}

class MainViewModelFactory(private val dataSource: PlayerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dataSource) as T
    }
}