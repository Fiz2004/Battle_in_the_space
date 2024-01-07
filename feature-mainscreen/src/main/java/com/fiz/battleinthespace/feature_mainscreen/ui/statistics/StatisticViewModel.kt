package com.fiz.battleinthespace.feature_mainscreen.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.usecase.GetPlayersFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class StatisticViewModel @Inject constructor(
    private val getPlayersFlowUseCase: GetPlayersFlowUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(StatisticViewState())
    val viewState = _viewState.asStateFlow()

    private val _viewEffect = MutableSharedFlow<StatisticViewEffect>()
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
                            StatisticViewEffect.ShowErrorMessage(
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
                                items = buildList {
                                    add(StatisticItemUi.HeaderItem("Полная статистика по игрокам"))
                                    add(StatisticItemUi.HeaderItem("Игроки"))
                                    addAll(result.getOrNull()?.filter { it.controllerPlayer }
                                        ?.map { StatisticItemUi.StatisticItem(it.name, it.money.toString()) }
                                        ?: emptyList())
                                    add(StatisticItemUi.HeaderItem("Компьютер"))
                                    addAll(result.getOrNull()?.filter { !it.controllerPlayer }
                                        ?.map { StatisticItemUi.StatisticItem(it.name, it.money.toString()) }
                                        ?: emptyList())
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}