package com.fiz.battleinthespace.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

inline fun <UiState> Fragment.collectUiState(
    viewState: StateFlow<UiState>,
    crossinline block: (UiState) -> Unit,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            viewState.collectLatest {
                block(it)
            }
        }
    }
}

inline fun <UiEffect> Fragment.collectUiEffect(
    viewEffect: SharedFlow<UiEffect>,
    crossinline block: (UiEffect) -> Unit,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            viewEffect.collect {
                block(it)
            }
        }
    }
}

inline fun AppCompatActivity.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(Dispatchers.Main) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

inline fun <UiState> AppCompatActivity.collectUiState(
    viewState: StateFlow<UiState>,
    crossinline block: (UiState) -> Unit,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(minActiveState) {
            viewState.collectLatest {
                block(it)
            }
        }
    }
}


inline fun <UiEffect> AppCompatActivity.collectUiEffect(
    viewEffect: SharedFlow<UiEffect>,
    crossinline block: (UiEffect) -> Unit,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(minActiveState) {
            viewEffect.collect {
                block(it)
            }
        }
    }
}