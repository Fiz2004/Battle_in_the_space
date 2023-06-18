package com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.fiz.battleinthespace.common.collectUiState
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentMissionSelectedBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.ViewState
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.NestedSectionsPagerAdapter
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.NestedSectionsPagerAdapter.Companion.Missions
import com.google.android.material.tabs.TabLayoutMediator

class MissionSelectedFragment : Fragment() {
    private var _binding: FragmentMissionSelectedBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private val viewModel: MainViewModel by activityViewModels()

    private val pagerAdapter by lazy { NestedSectionsPagerAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionSelectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.init()
        binding.setupListeners()
        collectUiState(viewModel.viewState, { binding.collectUiState(it) })
    }

    private fun FragmentMissionSelectedBinding.init() {
        viewpagerMission.adapter = pagerAdapter

        TabLayoutMediator(tabsMission, viewpagerMission) { tab, position ->
            tab.text = getString(Missions[position].title)
        }.attach()
    }

    private fun FragmentMissionSelectedBinding.setupListeners() {
        viewpagerMission.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.clickOnMission(position)
            }
        })
    }

    private fun FragmentMissionSelectedBinding.collectUiState(viewState: ViewState) {
        if (viewState.players.isEmpty()) return

        viewpagerMission.currentItem = viewState.players.getOrNull(0)?.mission ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewpagerMission.adapter = null
        _binding = null
    }


}


