package com.fiz.android.battleinthespace.interfaces

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.options.Mission
import com.fiz.android.battleinthespace.options.Options
import com.fiz.android.battleinthespace.options.Records
import com.fiz.android.battleinthespace.options.Station
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), OptionsFragment.Companion.Listener,
    MissionSelectedFragment.Companion.Listener {
    private lateinit var options: Options
    private lateinit var mission: Mission
    private lateinit var station: Station
    private lateinit var records: Records

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        options = Options(applicationContext)
        mission = Mission(applicationContext)
        station = Station(applicationContext)
        records = Records(applicationContext)

        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        val viewPager = findViewById<ViewPager2>(R.id.viewpager)
        viewPager.adapter = pagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTitle(position)
        }.attach()
    }

    private fun getTitle(position: Int): CharSequence {
        return when (position) {
            0 -> resources.getText(R.string.title_mission_selection)
            1 -> resources.getText(R.string.title_space_station)
            2 -> resources.getText(R.string.title_statistics)
            else -> resources.getText(R.string.title_options)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Options::class.java.simpleName, options)
        outState.putSerializable(Mission::class.java.simpleName, mission)
        outState.putSerializable(Station::class.java.simpleName, station)
        outState.putSerializable(Records::class.java.simpleName, records)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        options = savedInstanceState.getSerializable(Options::class.java.simpleName) as Options
        mission = savedInstanceState.getSerializable(Mission::class.java.simpleName) as Mission
        station = savedInstanceState.getSerializable(Station::class.java.simpleName) as Station
        records = savedInstanceState.getSerializable(Records::class.java.simpleName) as Records
    }

    fun onClickDone(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(Options::class.java.simpleName, options)
        intent.putExtra(Mission::class.java.simpleName, mission)
        intent.putExtra(Station::class.java.simpleName, station)
        intent.putExtra(Records::class.java.simpleName, records)
        startActivity(intent)
    }

    override fun playersRadioButtonsClicked(id: Int) {
        options.countPlayers = id + 1
    }

    override fun playersEditTexts(id: Int, text: String) {
        options.name[id] = text
    }

    override fun playersToggleButtonsClicked(id: Int) {
        options.playerControllerPlayer[id] = !options.playerControllerPlayer[id]
    }

    private inner class SectionsPagerAdapter(fm: FragmentManager, lc: Lifecycle) :
        androidx.viewpager2.adapter.FragmentStateAdapter(fm, lc) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
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
                else -> bundle.putSerializable(Options::class.java.simpleName, options)
            }
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun changeFragment(id: Int) {
        mission.mission = id
    }
}

