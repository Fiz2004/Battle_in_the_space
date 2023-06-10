package com.fiz.battleinthespace.feature_mainscreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.common.Resource
import com.fiz.battleinthespace.domain.models.Item
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.TypeItems
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ViewState(
    val isLoadingPlayers: Boolean = true,
    val isLoadingCount: Boolean = true,
    val players: List<Player> = emptyList(),
    val countPlayer: Int = 4,
    val type: Int = 0
)

sealed class ViewEffect {
    data class ShowErrorMessage(val message: String) : ViewEffect()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    private val _viewEffect = MutableSharedFlow<ViewEffect>()
    val viewEffect = _viewEffect.asSharedFlow()

    init {
        if (settingsRepository.getIsFirstLaunch())
            viewModelScope.launch {
                settingsRepository.saveIsFirstLaunchComplete(uuid = playerRepository.initFirstLaunchPlayers())
            }

        viewModelScope.launch {
            launch {
                settingsRepository.getFlowUuid().flatMapMerge {
                    if (it != "")
                        playerRepository.getFlowPlayers(it)
                    else
                        flow {}
                }.collect { resource ->
                    when (resource) {
                        is Resource.Error -> _viewEffect.emit(ViewEffect.ShowErrorMessage("Load Error"))
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            resource.data?.let {
                                _viewState.value = _viewState.value.copy(
                                    isLoadingPlayers = false,
                                    players = it,
                                )
                            }
                                ?: _viewEffect.emit(ViewEffect.ShowErrorMessage("Load players equals null"))
                        }
                    }

                }
            }

            launch {
                settingsRepository.getFlowCountPlayers().collect { countPlayer ->
                    _viewState.value = _viewState.value.copy(
                        isLoadingCount = false,
                        countPlayer = countPlayer
                    )
                }
            }
        }
    }

    fun getItems(): List<TypeItems> {
        val player = _viewState.value.players[0]

        return player.items
    }

    private fun changeItems(key: Int, type: Int, value: StateProduct) {
        var player = _viewState.value.players[0]

        val items = player.items.toMutableList()
        items[type].items[key].state = value

        player = player.copy(items = items.toList())

        val players = _viewState.value.players.toMutableList()
        players[0] = player

        viewModelScope.launch {
            playerRepository.save(settingsRepository.getFlowUuid().first(), players)
        }
    }

    fun setCountPlayers(numberRadioButton: Int) {
        settingsRepository.saveCountPlayers(numberRadioButton)
    }

    fun clickOnMission(value: Int) {
        val player = _viewState.value.players.getOrNull(0) ?: return
        val players = _viewState.value.players.toMutableList()
        players[0] = player.copy(mission = value)

        viewModelScope.launch {
            playerRepository.save(settingsRepository.getUuid(), players)
        }
    }

    fun onClickReset(number: Int) {
        viewModelScope.launch {
            val uuid = settingsRepository.getUuid()
            playerRepository.resetPlayer(uuid, number)
        }
    }

    fun clickTypeItem(value: Int) {
        _viewState.value = _viewState.value.copy(type = value + 1)
    }

    private fun buyItem(
        index: Int,
        indexType: Int
    ) {
        val player = _viewState.value.players[0]

        val balance = player.money - getItems()[indexType].items[index].cost

        val players = _viewState.value.players.toMutableList()

        val items = player.items.toMutableList()
        items[indexType].items[index].state = StateProduct.BUY

        players[0] = player.copy(items = items.toList(), money = balance)

        if (balance >= 0) {
            viewModelScope.launch {
                playerRepository.save(settingsRepository.getFlowUuid().first(), players)
            }
        }
    }

    fun clickItems(position: Int) {
        if (position == 0) {
            _viewState.value = _viewState.value.copy(type = 0)
        } else {
            val type = _viewState.value.type
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
        val type = _viewState.value.type
        val indexType = type - 1
        val items = _viewState.value.players[0].items
        return Item.addZeroFirstItem(items[indexType].items)
    }

    fun nameChanged(index: Int, newName: String) {
        val players = _viewState.value.players.toMutableList()

        if (players.isEmpty()) return
        if (players[index].name == newName) return

        players[index] = players[index].copy(name = newName)

        viewModelScope.launch {
            playerRepository.save(settingsRepository.getFlowUuid().first(), players)
        }
    }

    fun changeControllerPlayer(index: Int, isChecked: Boolean) {
        val players = _viewState.value.players.toMutableList()
        players[index] = players[index].copy(controllerPlayer = isChecked)

        viewModelScope.launch {
            playerRepository.save(settingsRepository.getFlowUuid().first(), players)
        }
    }
}