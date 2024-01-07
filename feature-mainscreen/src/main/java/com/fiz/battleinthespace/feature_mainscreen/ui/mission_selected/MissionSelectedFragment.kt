package com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.fiz.battleinthespace.common.collectUiState
import com.fiz.battleinthespace.common.unsafeLazy
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentMissionSelectedBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.NestedSectionsPagerAdapter
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.NestedSectionsPagerAdapter.Companion.Missions
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MissionSelectedFragment : Fragment() {

    private var _binding: FragmentMissionSelectedBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private val viewModel: MissionSelectedViewModel by viewModels()

    private val pagerAdapter by unsafeLazy { NestedSectionsPagerAdapter(this) }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        private var isInit: Boolean = false

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (!isInit) {
                isInit = true
                return
            }
            viewModel.clickOnMission(position)
        }
    }

    private var tabLayoutMediator: TabLayoutMediator? = null

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
        tabLayoutMediator = TabLayoutMediator(tabsMission, viewpagerMission) { tab, position ->
            tab.text = getString(Missions[position].title)
        }
        tabLayoutMediator?.attach()
    }

    private fun FragmentMissionSelectedBinding.setupListeners() {
        viewpagerMission.registerOnPageChangeCallback(onPageChangeCallback)
    }

    private fun FragmentMissionSelectedBinding.collectUiState(viewState: MissionSelectedViewState) {
        mainContainer.isVisible = !viewState.isLoading
        progress.isVisible = viewState.isLoading
        viewpagerMission.currentItem = viewState.mission
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewpagerMission.adapter = null
        binding.viewpagerMission.unregisterOnPageChangeCallback(onPageChangeCallback)
        tabLayoutMediator?.detach()
        _binding = null
    }

}


