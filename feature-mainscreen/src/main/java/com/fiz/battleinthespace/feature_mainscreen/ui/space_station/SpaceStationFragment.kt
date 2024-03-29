package com.fiz.battleinthespace.feature_mainscreen.ui.space_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fiz.battleinthespace.common.launchAndRepeatWithViewLifecycle
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentSpaceStationBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.ItemsAdapter
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.TypeItemsAdapter

class SpaceStationFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentSpaceStationBinding? = null
    private val binding get() = _binding!!

    private lateinit var typeItemsAdapter: TypeItemsAdapter
    private lateinit var itemsAdapter: ItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpaceStationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    private fun setupObservers() {

        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { viewState ->
                if (viewState.players.isEmpty()) return@collect

                binding.money.text = getString(R.string.balance, viewState.players[0].money)

                binding.stationRecycler.adapter =
                    if (viewState.type == 0)
                        getTypeItemsAdapter()
                    else
                        getItemsAdapter()

                binding.stationRecycler.adapter =
                    if (viewState.type == 0)
                        getTypeItemsAdapter()
                    else
                        getItemsAdapter()
            }
        }
    }

    private fun getItemsAdapter(): ItemsAdapter {
        val items = viewModel.getItemsWithZero()
        itemsAdapter = ItemsAdapter(items, viewModel::clickItems)
        return itemsAdapter
    }

    private fun getTypeItemsAdapter(): TypeItemsAdapter {
        val items = viewModel.getItems()
        typeItemsAdapter = TypeItemsAdapter(items, viewModel::clickTypeItem)
        return typeItemsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}