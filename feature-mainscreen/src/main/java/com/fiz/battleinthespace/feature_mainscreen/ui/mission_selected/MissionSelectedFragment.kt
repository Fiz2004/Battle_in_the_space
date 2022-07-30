package com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentMissionSelectedBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.NestedSectionsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MissionSelectedFragment : Fragment() {
    private var _binding: FragmentMissionSelectedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionSelectedBinding.inflate(inflater, container, false)

        val pagerAdapter = NestedSectionsPagerAdapter(this)
        binding.viewpagerMission.adapter = pagerAdapter

        TabLayoutMediator(binding.tabsMission, binding.viewpagerMission) { tab, position ->
            tab.text = resources.getText(NestedSectionsPagerAdapter.getTitle(position))
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewpagerMission.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                viewModel.clickOnMission(position)
                binding.viewpagerMission.currentItem = position
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

