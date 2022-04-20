package com.fiz.battleinthespace.feature_mainscreen.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected.MissionDestroyMeteoriteFragment
import com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected.MissionDestroySpaceShipsFragment

class NestedSectionsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> MissionDestroyMeteoriteFragment()
            else -> MissionDestroySpaceShipsFragment()
        }
        return fragment
    }

    companion object {
        fun getTitle(position: Int): Int {
            return when (position) {
                0 -> R.string.mission_destroy_meteorites
                else -> R.string.mission_destroy_spaceships
            }
        }
    }
}