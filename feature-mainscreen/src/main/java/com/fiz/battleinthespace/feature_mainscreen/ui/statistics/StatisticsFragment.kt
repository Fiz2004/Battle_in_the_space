package com.fiz.battleinthespace.feature_mainscreen.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.battleinthespace.common.collectUiState
import com.fiz.battleinthespace.common.unsafeLazy
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private val viewModel: StatisticViewModel by viewModels()

    private val adapter: StatisticsAdapter by unsafeLazy { StatisticsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.init()

        collectUiState(viewModel.viewState, { binding.collectUiState(it) })
    }

    private fun FragmentStatisticsBinding.init() {
        listStatistics.adapter = adapter
    }

    private fun FragmentStatisticsBinding.collectUiState(viewState: StatisticViewState) {
        listStatistics.isVisible = !viewState.isLoading
        progress.isVisible = viewState.isLoading
        adapter.submitList(viewState.items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listStatistics.adapter = null
        _binding = null
    }
}
