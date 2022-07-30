package com.fiz.battleinthespace.feature_mainscreen.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected.MissionSelectedFragment
import com.fiz.battleinthespace.feature_mainscreen.ui.options.OptionsFragment
import com.fiz.battleinthespace.feature_mainscreen.ui.space_station.SpaceStationFragment
import com.fiz.battleinthespace.feature_mainscreen.ui.statistics.StatisticsFragment

const val MISSION_SELECTED_PAGE_INDEX = 0
const val SPACE_STATION_PAGE_INDEX = 1
const val STATISTICS_PAGE_INDEX = 2
const val OPTIONS_PAGE_INDEX = 3

class SectionsPagerAdapter(fm: FragmentManager, lc: Lifecycle) :
    FragmentStateAdapter(fm, lc) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        MISSION_SELECTED_PAGE_INDEX to { MissionSelectedFragment() },
        SPACE_STATION_PAGE_INDEX to { SpaceStationFragment() },
        STATISTICS_PAGE_INDEX to { StatisticsFragment() },
        OPTIONS_PAGE_INDEX to { OptionsFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}