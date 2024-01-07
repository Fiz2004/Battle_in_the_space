package com.fiz.battleinthespace.feature_mainscreen.ui.adapters

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected.MissionDestroyMeteoriteFragment
import com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected.MissionDestroySpaceShipsFragment

internal class NestedSectionsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return Missions.size
    }

    override fun createFragment(position: Int): Fragment {
        return Missions[position].getFragment()
    }

    companion object {

        data class Mission(
            @StringRes val title: Int = 0,
            val getFragment: () -> Fragment
        )

        val Missions: List<Mission> = listOf(
            Mission(
                title = R.string.mission_destroy_meteorites,
                getFragment = { MissionDestroyMeteoriteFragment() }),
            Mission(
                title = R.string.mission_destroy_spaceships,
                getFragment = { MissionDestroySpaceShipsFragment() })
        )
    }
}