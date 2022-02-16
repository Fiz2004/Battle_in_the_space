package com.fiz.android.battleinthespace.base.presentation.helpers

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.presentation.options.OptionsFragment
import com.fiz.android.battleinthespace.base.presentation.space_station.SpaceStationFragment
import com.fiz.android.battleinthespace.base.presentation.statistics.StatisticsFragment
import com.fiz.android.battleinthespace.presentation.main.MissionSelectedFragment

class SectionsPagerAdapter(fm: FragmentManager, lc: Lifecycle) :
    androidx.viewpager2.adapter.FragmentStateAdapter(fm, lc) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MissionSelectedFragment()
            1 -> SpaceStationFragment()
            2 -> StatisticsFragment()
            else -> OptionsFragment()
        }
    }

    companion object {
        fun getTitle(context: Context, position: Int): CharSequence {
            return when (position) {
                0 -> context.resources.getText(R.string.title_mission_selection)
                1 -> context.resources.getText(R.string.title_space_station)
                2 -> context.resources.getText(R.string.title_statistics)
                else -> context.resources.getText(R.string.title_options)
            }
        }
    }
}