package com.fiz.battleinthespace.feature_mainscreen.ui.space_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.battleinthespace.common.collectUiState
import com.fiz.battleinthespace.common.unsafeLazy
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentSpaceStationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class SpaceStationFragment : Fragment() {

    private val viewModel: SpaceStationViewModel by viewModels()

    private var _binding: FragmentSpaceStationBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private val adapter: ItemUiAdapter by unsafeLazy {
        ItemUiAdapter(viewModel::clickItem, viewModel::undoClickItem)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpaceStationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.init()

        collectUiState(viewModel.viewState, { binding.collectUiState(it) })
    }

    private fun FragmentSpaceStationBinding.init() {
        stationRecycler.adapter = adapter
    }

    private fun FragmentSpaceStationBinding.collectUiState(viewState: SpaceStationViewState) {
        progress.isVisible = viewState.isLoading
        mainContent.isVisible = !viewState.isLoading

        txtMoney.text = getString(R.string.balance, viewState.money)

        adapter.submitList(viewState.items)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.stationRecycler.adapter = null
        _binding = null
    }
}
