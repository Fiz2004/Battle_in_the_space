package com.fiz.battleinthespace.feature_mainscreen.ui.space_station

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.usecase.BuyItemStateUseCase
import com.fiz.battleinthespace.domain.usecase.GetCategoryItemsUseCase
import com.fiz.battleinthespace.domain.usecase.GetItemsUseCase
import com.fiz.battleinthespace.domain.usecase.GetPlayersFlowUseCase
import com.fiz.battleinthespace.domain.usecase.SetItemStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SpaceStationViewModel @Inject constructor(
    private val getPlayersFlowUseCase: GetPlayersFlowUseCase,
    private val getCategoryItemsUseCase: GetCategoryItemsUseCase,
    private val getItemsUseCase: GetItemsUseCase,
    private val setItemStateUseCase: SetItemStateUseCase,
    private val buyItemStateUseCase: BuyItemStateUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(SpaceStationViewState())
    val viewState = _viewState.asStateFlow()

    private val _viewEffect = MutableSharedFlow<SpaceStationViewEffect>()
    val viewEffect = _viewEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            _viewState.update { state ->
                state.copy(
                    isLoading = true,
                )
            }
            getPlayersFlowUseCase().collect { result ->
                when {
                    result.isFailure -> {
                        _viewEffect.emit(
                            SpaceStationViewEffect.ShowErrorMessage(
                                result.exceptionOrNull()?.message ?: "Load Error"
                            )
                        )
                        _viewState.update { state ->
                            state.copy(
                                isLoading = false,
                            )
                        }
                    }

                    result.isSuccess -> {
                        _viewState.update { state ->
                            state.copy(
                                isLoading = false,
                                money = result.getOrNull()?.first()?.money ?: 0,
                                items = if (viewState.value.category == null) {
                                    getCategoryItemsUseCase().map { it.toItemUi() }
                                } else {
                                    getItemsUseCase(viewState.value.category!!.id).map { it.toItemUi() }
                                }
                            )
                        }
                    }
                }
            }
        }
    }


    fun clickItem(spaceStationItemUi: SpaceStationItemUi) {
        viewModelScope.launch {
            when (spaceStationItemUi) {
                is SpaceStationItemUi.CategorySpaceStationItem -> {
                    _viewState.update { state ->
                        state.copy(
                            category = spaceStationItemUi,
                            items = getItemsUseCase(spaceStationItemUi.id).map { it.toItemUi() }
                        )
                    }
                }

                is SpaceStationItemUi.SubSpaceStationItemUi.BackSpaceStationItem -> {
                    _viewState.update { state ->
                        state.copy(
                            category = null,
                            items = getCategoryItemsUseCase().map { it.toItemUi() },
                        )
                    }
                }

                is SpaceStationItemUi.SubSpaceStationItemUi.SubSpaceStationItem -> {
                    val listProduct =
                        viewState.value.items.filterIsInstance<SpaceStationItemUi.SubSpaceStationItemUi.SubSpaceStationItem>()
                    when (spaceStationItemUi.state) {
                        StateProduct.NONE -> {
                            setItemStateUseCase(spaceStationItemUi.toItem(), StateProduct.PREPARE)
                        }

                        StateProduct.PREPARE -> {
                            buyItemStateUseCase(spaceStationItemUi.toItem())
                        }

                        StateProduct.BUY -> {
                            listProduct.forEach {
                                if (it.state == StateProduct.INSTALL) {
                                    setItemStateUseCase(it.toItem(), StateProduct.BUY)
                                }
                            }
                            setItemStateUseCase(spaceStationItemUi.toItem(), StateProduct.INSTALL)
                        }

                        StateProduct.INSTALL -> {}
                    }
                }
            }
        }
    }

    fun undoClickItem(spaceStationItemUi: SpaceStationItemUi) {
        viewModelScope.launch {
            setItemStateUseCase(
                (spaceStationItemUi as SpaceStationItemUi.SubSpaceStationItemUi.SubSpaceStationItem).toItem(),
                StateProduct.NONE
            )
        }
    }

}