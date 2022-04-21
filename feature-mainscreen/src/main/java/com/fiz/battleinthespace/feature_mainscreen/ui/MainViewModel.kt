package com.fiz.battleinthespace.feature_mainscreen.ui

import androidx.lifecycle.*
import com.fiz.battleinthespace.database.models.Item
import com.fiz.battleinthespace.database.models.StateProduct
import com.fiz.battleinthespace.database.models.TypeItems
import com.fiz.battleinthespace.feature_mainscreen.domain.PlayerRepository
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
            playerRepository.savePlayer(player.copy(items = items.toList()))
        }
    }

    fun setCountPlayers(numberRadioButton: Int) {
        playerRepository.saveCountPlayers(numberRadioButton)
        countPlayer.postValue(numberRadioButton)
    }

    fun clickOnMission(value: Int) {
        val player = player.value ?: return

        viewModelScope.launch {
            playerRepository.savePlayer(player.copy(mission = value))
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

    private fun buyItem(
        index: Int,
        indexType: Int
    ) {
        val player = player.value ?: return

        val balance = player.money - getItems()[indexType].items[index].cost

        if (balance >= 0) {
            viewModelScope.launch {
                playerRepository.savePlayer(
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

    fun nameChanged(index: Int, newName: String) {
        val players = players.value ?: return
        if (players[index].name == newName) return

        viewModelScope.launch {
            playerRepository.savePlayer(
                players[index].copy(name = newName)
            )
        }
    }

    fun changeControllerPlayer(index: Int, isChecked: Boolean) {
        val players = players.value ?: return

        viewModelScope.launch {
            playerRepository.savePlayer(
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