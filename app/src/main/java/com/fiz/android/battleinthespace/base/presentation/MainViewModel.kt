package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.*
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.base.data.TypeItems
import com.fiz.android.battleinthespace.base.data.database.PlayerTypeConverters
import com.fiz.android.battleinthespace.base.domain.accounthelper.AccountHelper

class MainViewModel(
    private val playerRepository: PlayerRepository,
    private val accountHelper: AccountHelper) : ViewModel() {
    var playerListLiveData: LiveData<List<Player>?> = playerRepository.getPlayers()

    var player: Player = Player(money = 666)

    val user = Transformations.map(accountHelper.user) { it }
    val mAuth = accountHelper.mAuth

    private var _type = MutableLiveData(0)
    val type: LiveData<Int>
        get() = _type

    private var _errorTextToToast = MutableLiveData<String?>(null)
    val errorTextToToast: LiveData<String?>
        get() = _errorTextToToast

    init {
        accountHelper.initErrorTextToToast(_errorTextToToast)
    }

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

    fun addSaveInstanceState(outState: Bundle) {
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

    fun gameActivityFinish(intent: Intent) {
        val result = intent.getIntExtra("score", 0)
        addMoney(value = result)
        savePlayers()
    }

    fun refreshPlayerListLiveData(playerList: List<Player>?) {
        if (playerList == null || playerList.isEmpty())
            fillInitValue()
        else
            initPlayer(playerList[0])
    }

    fun signUpWithEmail(email: String, password: String) {
        accountHelper.signUpWithEmail(email, password)
    }

    fun signInWithEmail(email: String, password: String) {
        accountHelper.signInWithEmail(email, password)
    }

    fun signInWithGoogle(act: MainActivity) {
        accountHelper.signInWithGoogle(act)
    }

    fun signInFirebaseWithGoogle(result: Intent) {
        accountHelper.signInFirebaseWithGoogle(result)
    }

    fun resetPassword(email: String) {
        accountHelper.resetPassword(email)
    }

    fun signInOutG(act: MainActivity) {
        accountHelper.signInOutG(act)
    }
}

class MainViewModelFactory : ViewModelProvider.Factory {
    private val dataSource = PlayerRepository.get()
    private val accountHelper = AccountHelper()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dataSource, accountHelper) as T
    }
}