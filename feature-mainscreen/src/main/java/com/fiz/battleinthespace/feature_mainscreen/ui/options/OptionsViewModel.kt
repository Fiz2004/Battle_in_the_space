package com.fiz.battleinthespace.feature_mainscreen.ui.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.usecase.GetCountPlayersFlowUseCase
import com.fiz.battleinthespace.domain.usecase.GetFlowEmailUseCase
import com.fiz.battleinthespace.domain.usecase.GetPlayersFlowUseCase
import com.fiz.battleinthespace.domain.usecase.ResetPlayerUseCase
import com.fiz.battleinthespace.domain.usecase.SetControllerUseCase
import com.fiz.battleinthespace.domain.usecase.SetCountPlayersUseCase
import com.fiz.battleinthespace.domain.usecase.SetNameUseCase
import com.fiz.battleinthespace.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class OptionsViewModel @Inject constructor(
    private val getPlayersFlowUseCase: GetPlayersFlowUseCase,
    private val getCountPlayersFlowUseCase: GetCountPlayersFlowUseCase,
    private val setCountPlayersUseCase: SetCountPlayersUseCase,
    private val resetPlayerUseCase: ResetPlayerUseCase,
    private val setNameUseCase: SetNameUseCase,
    private val setControllerUseCase: SetControllerUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getFlowEmailUseCase: GetFlowEmailUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(OptionsViewState())
    val viewState = _viewState.asStateFlow()

    private val _viewEffect = MutableSharedFlow<OptionsViewEffect>()
    val viewEffect = _viewEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            _viewState.update { state ->
                state.copy(
                    isLoading = true,
                )
            }

            launch {
                getPlayersFlowUseCase().collect { playersResult ->
                    when {
                        playersResult.isFailure -> {
                            _viewEffect.emit(
                                OptionsViewEffect.ShowErrorMessage(
                                    playersResult.exceptionOrNull()?.message ?: "Load Error"
                                )
                            )
                            _viewState.update { state ->
                                state.copy(
                                    isLoading = false,
                                )
                            }
                        }

                        playersResult.isSuccess -> {
                            _viewState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    players = playersResult.getOrNull() ?: emptyList()
                                )
                            }
                        }
                    }
                }
            }

            launch {
                getCountPlayersFlowUseCase().collect { countPlayersResult ->
                    when {
                        countPlayersResult.isFailure -> {
                            _viewEffect.emit(
                                OptionsViewEffect.ShowErrorMessage(
                                    countPlayersResult.exceptionOrNull()?.message ?: "Load Error"
                                )
                            )
                            _viewState.update { state ->
                                state.copy(
                                    isLoading = false,
                                )
                            }
                        }

                        countPlayersResult.isSuccess -> {
                            _viewState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    countPlayer = countPlayersResult.getOrNull() ?: 0
                                )
                            }
                        }
                    }
                }
            }

            launch {
                getFlowEmailUseCase()
                    .collect { email ->
                        _viewState.update { state ->
                            state.copy(
                                email = email
                            )
                        }
                    }
            }
        }
    }

    fun setCountPlayers(numberRadioButton: Int) {
        viewModelScope.launch {
            try {
                setCountPlayersUseCase(numberRadioButton)
            } catch (e: Exception) {
                _viewEffect.emit(OptionsViewEffect.ShowErrorMessage(e.message.toString()))
            }
        }
    }

    fun onClickReset(number: Int) {
        viewModelScope.launch {
            resetPlayerUseCase(number)
        }
    }

    fun nameChanged(index: Int, newName: String) {
        viewModelScope.launch {
            setNameUseCase(index, newName)
        }
    }

    fun changeControllerPlayer(index: Int, isChecked: Boolean) {
        viewModelScope.launch {
            setControllerUseCase(index, isChecked)
        }
    }

    fun signInOutG() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}