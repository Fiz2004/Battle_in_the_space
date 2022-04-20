package com.fiz.battleinthespace.feature_mainscreen.ui

import android.content.Intent
import androidx.lifecycle.*
import com.fiz.battleinthespace.database.Item
import com.fiz.battleinthespace.database.PlayerRepository
import com.fiz.battleinthespace.database.StateProduct
import com.fiz.battleinthespace.database.TypeItems
import kotlinx.coroutines.launch

class MainViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    init {
        if (playerRepository.isFirstLaunch())
            viewModelScope.launch {
                playerRepository.initFirstLaunchPlayers()
            }
    }

    var players = playerRepository.getPlayers(); private set
    var player = Transformations.map(players) { it[0] }; private set

    var countPlayer = MutableLiveData(playerRepository.getCountPlayers()); private set

    var money = Transformations.map(players) { it[0].money }; private set
    var type = MutableLiveData(0); private set


    fun getItems(): List<TypeItems> {
        val player = player.value ?: return listOf()

        return player.items
    }

    private fun changeItems(key: Int, type: Int, value: StateProduct) {
        val player = player.value ?: return

        val items = player.items.toMutableList()
        items[type].items[key].state = value

        viewModelScope.launch {
            playerRepository.save(player.copy(items = items.toList()))
        }
    }

    fun setCountPlayers(numberRadioButton: Int) {
        playerRepository.saveCountPlayers(numberRadioButton)
        countPlayer.postValue(numberRadioButton)
    }

    fun clickOnMission(value: Int) {
        val player = player.value ?: return

        viewModelScope.launch {
            playerRepository.save(player.copy(mission = value))
        }
    }

    fun onClickReset() {
        viewModelScope.launch {
            playerRepository.initFirstLaunchPlayers()
        }
    }

    fun clickTypeItem(value: Int) {
        type.postValue(value)
    }

    fun getDataForIntent(intent: Intent): Intent {
        for (n in 0 until 4) {
            val value = players.value?.get(n)
                ?: throw Error("Не доступна LiveData playerListLiveData")

            intent.putExtra("name$n", value.name)
            intent.putExtra("playerControllerPlayer$n", value.controllerPlayer)
            intent.putExtra("mission$n", value.mission)
        }
        return intent
    }

    private fun buyItem(
        index: Int,
        indexType: Int
    ) {
        val player = player.value ?: return

        val balance = player.money - getItems()[indexType].items[index].cost

        if (balance >= 0) {
            viewModelScope.launch {
                playerRepository.save(
                    player.copy(money = balance)
                )
            }

            changeItems(index, indexType, StateProduct.BUY)
        }
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
        val items = player.value?.items ?: listOf()
        return Item.addZeroFirstItem(items[indexType].items)
    }

    override fun onCleared() {
        super.onCleared()
        playerRepository.close()
    }

    fun nameChanged(index: Int, newName: String) {
        val players = players.value ?: return

        viewModelScope.launch {
            playerRepository.save(
                players[index].copy(name = newName)
            )
        }
    }

    fun changeControllerPlayer(index: Int, isChecked: Boolean) {
        val players = players.value ?: return

        viewModelScope.launch {
            playerRepository.save(
                players[index].copy(controllerPlayer = isChecked)
            )
        }
    }
}

class MainViewModelFactory(private val playerRepository: PlayerRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(playerRepository) as T
    }
}