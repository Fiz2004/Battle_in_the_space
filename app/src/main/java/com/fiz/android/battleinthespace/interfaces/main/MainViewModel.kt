package com.fiz.android.battleinthespace.interfaces.main

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.interfaces.main.options.OptionsFragment
import com.fiz.android.battleinthespace.interfaces.main.space_station.SpaceStationFragment
import com.fiz.android.battleinthespace.interfaces.main.statistics.StatisticsFragment
import com.fiz.android.battleinthespace.options.Mission
import com.fiz.android.battleinthespace.options.Records
import com.fiz.android.battleinthespace.options.Station

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _countPlayers = MutableLiveData<Int>(4)
    val countPlayers: LiveData<Int>
        get() = _countPlayers

    var name: MutableList<String> =
        MutableList(4) { i -> "${application.applicationContext.resources.getString(R.string.player)} ${i + 1}" }
    var playerControllerPlayer: MutableList<Boolean> = mutableListOf(true, false, false, false)

    fun setCountPlayers(numberRadioButton: Int) {
        _countPlayers.value = numberRadioButton
    }

    var mission: Mission = Mission()
    var station: Station = Station()
    var records: Records = Records()

    fun playersEditTexts(id: Int, text: String) {
        name[id] = text
    }

    fun playersToggleButtonsClicked(id: Int) {
        playerControllerPlayer[id] = !playerControllerPlayer[id]
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("countPlayers", countPlayers.value ?: 4)
        outState.putString("name1", name[0])
        outState.putString("name2", name[1])
        outState.putString("name3", name[2])
        outState.putString("name4", name[3])
        outState.putBoolean("playerControllerPlayer1", playerControllerPlayer[0])
        outState.putBoolean("playerControllerPlayer2", playerControllerPlayer[1])
        outState.putBoolean("playerControllerPlayer3", playerControllerPlayer[2])
        outState.putBoolean("playerControllerPlayer4", playerControllerPlayer[3])
        outState.putSerializable(Mission::class.java.simpleName, mission)
        outState.putSerializable(Station::class.java.simpleName, station)
        outState.putSerializable(Records::class.java.simpleName, records)
    }

    fun onClickDone(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayers.value)
        intent.putExtra("name1", name[0])
        intent.putExtra("name2", name[1])
        intent.putExtra("name3", name[2])
        intent.putExtra("name4", name[3])
        intent.putExtra("playerControllerPlayer1", playerControllerPlayer[0])
        intent.putExtra("playerControllerPlayer2", playerControllerPlayer[1])
        intent.putExtra("playerControllerPlayer3", playerControllerPlayer[2])
        intent.putExtra("playerControllerPlayer4", playerControllerPlayer[3])
        intent.putExtra(Mission::class.java.simpleName, mission)
        intent.putExtra(Station::class.java.simpleName, station)
        intent.putExtra(Records::class.java.simpleName, records)
        return intent
    }

    fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> MissionSelectedFragment()
            1 -> SpaceStationFragment()
            2 -> StatisticsFragment()
            else -> OptionsFragment()
        }

        val bundle = Bundle()
        when (position) {
            0 -> bundle.putSerializable(Mission::class.java.simpleName, mission)
            1 -> bundle.putSerializable(Station::class.java.simpleName, station)
            2 -> bundle.putSerializable(Records::class.java.simpleName, records)
            else -> {}
        }
        fragment.arguments = bundle

        return fragment
    }

    fun changeFragment(id: Int) {
        mission.mission = id
    }

}