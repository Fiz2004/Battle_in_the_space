package com.fiz.android.battleinthespace.base.presentation.space_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.storage.SharedPrefPlayerStorage
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.MainViewModelFactory
import com.fiz.android.battleinthespace.base.presentation.utils.CallBackItemClick
import com.fiz.android.battleinthespace.base.presentation.utils.ItemsAdapter
import com.fiz.android.battleinthespace.databinding.FragmentSpaceStationBinding


class SpaceStationFragment : Fragment() {
    private val viewModel: MainViewModel by lazy {
        val viewModelFactory =
            MainViewModelFactory(SharedPrefPlayerStorage(requireActivity().applicationContext))
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun initUI() {
        val layoutManager = GridLayoutManager(activity, 2)
        binding.stationRecycler.layoutManager = layoutManager
    }

    private fun updateUI() {
        setAdapter(viewModel.type)
        val money = viewModel.getMoney()
        binding.money.text = getString(R.string.balance, money)
    }

    private fun setAdapter(type: Int) {
        fun callBackItemClick() = CallBackItemClick { position: Int ->
            viewModel.clickItems(position, viewModel.type)
            updateUI()
        }

        fun callBackTypeItemClick() = CallBackTypeItemClick { position: Int ->
            viewModel.clickTypeItem(position + 1)
            updateUI()
        }

        binding.stationRecycler.adapter =
            if (type == 0) {
                getTypeItemsAdapter(::callBackTypeItemClick)
            } else {
                getItemsAdapter(::callBackItemClick)
            }
    }

    private fun getItemsAdapter(callBackItemClick: () -> CallBackItemClick): ItemsAdapter {
        val items = viewModel.getItemsWithZero(viewModel.type)
        itemsAdapter = ItemsAdapter(
            items,
            callBackItemClick()
        )
        return itemsAdapter
    }

    private fun getTypeItemsAdapter(callBackTypeItemClick: () -> CallBackTypeItemClick): TypeItemsAdapter {
        val items = viewModel.getItems()
        typeItemsAdapter = TypeItemsAdapter(
            items,
            callBackTypeItemClick()
        )
        return typeItemsAdapter
    }
}