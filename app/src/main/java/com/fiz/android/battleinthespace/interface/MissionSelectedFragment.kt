package com.fiz.android.battleinthespace.`interface`

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.fiz.android.battleinthespace.MissionDestroyMeteoriteFragment
import com.fiz.android.battleinthespace.MissionDestroySpaceShipsFragment
import com.fiz.android.battleinthespace.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MissionSelectedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mission_selected, container, false)
    }

    override fun onStart() {
        super.onStart()
        val localView = view ?: return
        val pagerAdapter = SectionsPagerAdapter(parentFragmentManager, lifecycle)
        val viewPager = localView.findViewById<ViewPager2>(R.id.viewpager_mission)
        viewPager.adapter = pagerAdapter

        val tabLayout = localView.findViewById<TabLayout>(R.id.tabs_mission)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTitle(position)
        }.attach()
    }

    private fun getTitle(position: Int): CharSequence {
        return when (position) {
            0 -> resources.getText(R.string.mission_destroy_meteorites)
            else -> resources.getText(R.string.mission_destroy_spaceships)
        }
    }

    private inner class SectionsPagerAdapter(fm: FragmentManager, lc: Lifecycle) :
        androidx.viewpager2.adapter.FragmentStateAdapter(fm, lc) {
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
    }
}