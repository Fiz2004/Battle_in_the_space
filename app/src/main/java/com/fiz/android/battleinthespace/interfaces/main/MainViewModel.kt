package com.fiz.android.battleinthespace.interfaces.main

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.interfaces.main.options.OptionsFragment
import com.fiz.android.battleinthespace.interfaces.main.space_station.SpaceStationFragment
import com.fiz.android.battleinthespace.interfaces.main.statistics.StatisticsFragment
import com.fiz.android.battleinthespace.options.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val playerRepository = PlayerRepository.get()

    var playerListLiveData = MutableLiveData<List<Player>>(playerRepository.getPlayers())

    var countPlayerLiveData = MutableLiveData<Int>(playerRepository.getCountPlayer())

    var name: MutableList<String> =
        MutableList(4) { i -> "${application.applicationContext.resources.getString(R.string.player)} ${i + 1}" }

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayerLiveData.value = numberRadioButton
    }

    var mission: Mission = Mission()
    var station: Station = Station()
    var records: Records = Records()

    fun playersEditTexts(id: Int, text: String) {
        name[id] = text
    }

    fun checkControllerPlayer(id: Int) {
        (playerListLiveData.value)?.get(id - 1)?.controllerPlayer =
            !((playerListLiveData.value)?.get(id - 1)?.controllerPlayer ?: false)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("countPlayers", countPlayerLiveData.value ?: 4)
        outState.putString("name1", name[0])
        outState.putString("name2", name[1])
        outState.putString("name3", name[2])
        outState.putString("name4", name[3])
        outState.putBoolean("playerControllerPlayer1", (playerListLiveData.value)?.get(0)?.controllerPlayer ?: false)
        outState.putBoolean("playerControllerPlayer2", (playerListLiveData.value)?.get(1)?.controllerPlayer ?: false)
        outState.putBoolean("playerControllerPlayer3", (playerListLiveData.value)?.get(2)?.controllerPlayer ?: false)
        outState.putBoolean("playerControllerPlayer4", (playerListLiveData.value)?.get(3)?.controllerPlayer ?: false)
        outState.putSerializable(Mission::class.java.simpleName, mission)
        outState.putSerializable(Station::class.java.simpleName, station)
        outState.putSerializable(Records::class.java.simpleName, records)
    }

    fun onClickDone(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayerLiveData.value)
        intent.putExtra("name1", name[0])
        intent.putExtra("name2", name[1])
        intent.putExtra("name3", name[2])
        intent.putExtra("name4", name[3])
        intent.putExtra("playerControllerPlayer1", (playerListLiveData.value)?.get(0)?.controllerPlayer)
        intent.putExtra("playerControllerPlayer2", (playerListLiveData.value)?.get(1)?.controllerPlayer)
        intent.putExtra("playerControllerPlayer3", (playerListLiveData.value)?.get(2)?.controllerPlayer)
        intent.putExtra("playerControllerPlayer4", (playerListLiveData.value)?.get(3)?.controllerPlayer)
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