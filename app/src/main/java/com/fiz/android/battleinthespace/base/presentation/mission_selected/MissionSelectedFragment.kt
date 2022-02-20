package com.fiz.android.battleinthespace.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.MainViewModelFactory
import com.fiz.android.battleinthespace.base.presentation.mission_selected.MissionDestroyMeteoriteFragment
import com.fiz.android.battleinthespace.base.presentation.mission_selected.MissionDestroySpaceShipsFragment
import com.fiz.android.battleinthespace.databinding.FragmentMissionSelectedBinding
import com.google.android.material.tabs.TabLayoutMediator

class MissionSelectedFragment : Fragment() {
    private var _binding: FragmentMissionSelectedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(requireActivity().applicationContext)
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionSelectedBinding.inflate(inflater, container, false)

        val pagerAdapter = SectionsPagerAdapter(this)
        binding.viewpagerMission.adapter = pagerAdapter

        TabLayoutMediator(binding.tabsMission, binding.viewpagerMission) { tab, position ->
            tab.text = getTitle(position)
        }.attach()

        return binding.root
    }

    private fun getTitle(position: Int): CharSequence {
        return when (position) {
            0 -> resources.getText(R.string.mission_destroy_meteorites)
            else -> resources.getText(R.string.mission_destroy_spaceships)
        }
    }

    private inner class SectionsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
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

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            viewModel.changeMission(position)
            binding.viewpagerMission.currentItem = position
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

