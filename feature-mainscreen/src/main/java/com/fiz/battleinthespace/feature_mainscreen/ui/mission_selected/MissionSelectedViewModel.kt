package com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.usecase.GetPlayersFlowUseCase
import com.fiz.battleinthespace.domain.usecase.SetMissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MissionSelectedViewModel @Inject constructor(
    private val setMissionUseCase: SetMissionUseCase,
    private val getPlayersFlowUseCase: GetPlayersFlowUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(MissionSelectedViewState())
    val viewState = _viewState.asStateFlow()

    private val _viewEffect = MutableSharedFlow<MissionSelectedViewEffect>()
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
                            MissionSelectedViewEffect.ShowErrorMessage(
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
                                mission = result.getOrNull()?.first()?.mission ?: 0,
                            )
                        }
                    }
                }
            }
        }
    }

    fun clickOnMission(value: Int) {
        viewModelScope.launch {
            setMissionUseCase(value)
        }
    }
}