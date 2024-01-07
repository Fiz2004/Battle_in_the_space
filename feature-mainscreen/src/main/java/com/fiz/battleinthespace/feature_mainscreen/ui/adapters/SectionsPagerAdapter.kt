package com.fiz.battleinthespace.feature_mainscreen.ui.adapters

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected.MissionSelectedFragment
import com.fiz.battleinthespace.feature_mainscreen.ui.options.OptionsFragment
import com.fiz.battleinthespace.feature_mainscreen.ui.space_station.SpaceStationFragment
import com.fiz.battleinthespace.feature_mainscreen.ui.statistics.StatisticsFragment

internal class SectionsPagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {

    override fun getItemCount() = Sections.size

    override fun createFragment(position: Int): Fragment {
        return Sections.getOrNull(position)?.getFragment?.invoke()
            ?: throw IndexOutOfBoundsException()
    }

    companion object {

        data class Page(
            @IdRes val id: Int,
            val getFragment: () -> Fragment
        )

        val Sections: List<Page> = listOf(
            Page(id = R.id.page_1, getFragment = { MissionSelectedFragment() }),
            Page(id = R.id.page_2, getFragment = { SpaceStationFragment() }),
            Page(id = R.id.page_3, getFragment = { StatisticsFragment() }),
            Page(id = R.id.page_4, getFragment = { OptionsFragment() })
        )
    }

}