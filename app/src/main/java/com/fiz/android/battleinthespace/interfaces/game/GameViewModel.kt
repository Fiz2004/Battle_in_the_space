package com.fiz.android.battleinthespace.interfaces.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.options.Records
import com.fiz.android.battleinthespace.options.Station

class GameViewModel(
    __countPlayers: Int,
    __name: MutableList<String>,
    __playerControllerPlayer: MutableList<Boolean>,
    val mission: Int,
    val station: Station,
    val records: Records) :
    ViewModel() {
    private val _countPlayers = MutableLiveData<Int>(__countPlayers)
    val countPlayers: LiveData<Int>
        get() = _countPlayers

    private val _name: MutableList<String> = __name
    val name: MutableList<String>
        get() = _name

    private val _playerControllerPlayer = __playerControllerPlayer
    val playerControllerPlayer: MutableList<Boolean>
        get() = _playerControllerPlayer
}

class GameViewModelFactory(
    private val countPlayers: Int,
    private val name: MutableList<String>,
    private val playerControllerPlayer: MutableList<Boolean>,
    private val mission: Int,
    private val station: Station,
    private val records: Records) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(countPlayers, name, playerControllerPlayer, mission, station, records) as T
    }
}