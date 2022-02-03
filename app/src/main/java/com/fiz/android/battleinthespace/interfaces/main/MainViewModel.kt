package com.fiz.android.battleinthespace.interfaces.main

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fiz.android.battleinthespace.interfaces.main.options.OptionsFragment
import com.fiz.android.battleinthespace.interfaces.main.space_station.SpaceStationFragment
import com.fiz.android.battleinthespace.interfaces.main.statistics.StatisticsFragment
import com.fiz.android.battleinthespace.options.Mission
import com.fiz.android.battleinthespace.options.Player
import com.fiz.android.battleinthespace.options.PlayerRepository
import com.fiz.android.battleinthespace.options.Records

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val playerRepository = PlayerRepository.get()

    var playerListLiveData = MutableLiveData<List<Player>>(playerRepository.getPlayers())

    var countPlayerLiveData = MutableLiveData<Int>(playerRepository.getCountPlayer())

    var type = MutableLiveData<Int>(0)

    var records: Records = Records()

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayerLiveData.value = numberRadioButton
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("countPlayers", countPlayerLiveData.value ?: 4)
        for (n in 0 until 4) {
            outState.putString("name$n", (playerListLiveData.value)?.get(n)?.name ?: "")
            outState.putBoolean(
                "playerControllerPlayer$n",
                (playerListLiveData.value)?.get(n)?.controllerPlayer ?: false)
            outState.putInt("mission$n", (playerListLiveData.value)?.get(n)?.mission ?: 0)
        }
        outState.putSerializable(Records::class.java.simpleName, records)
    }

    fun onClickDone(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayerLiveData.value)
        for (n in 0 until 4) {
            intent.putExtra("name$n", (playerListLiveData.value)?.get(n)?.name)
            intent.putExtra("playerControllerPlayer$n", (playerListLiveData.value)?.get(n)?.controllerPlayer)
            intent.putExtra("mission$n", (playerListLiveData.value)?.get(n)?.mission)
        }
        intent.putExtra(Records::class.java.simpleName, records)
        return intent
    }

    fun configureMoney(cost: Int) {
        val newValue = playerListLiveData.value
        newValue?.get(0)?.money = playerListLiveData.value?.get(0)?.money?.minus(cost) ?: 0
        playerListLiveData.value = newValue
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
            0 -> bundle.putSerializable(Mission::class.java.simpleName, (playerListLiveData.value)?.get(0)?.mission)
            1 -> {}
            2 -> bundle.putSerializable(Records::class.java.simpleName, records)
            else -> {}
        }
        fragment.arguments = bundle

        return fragment
    }

}