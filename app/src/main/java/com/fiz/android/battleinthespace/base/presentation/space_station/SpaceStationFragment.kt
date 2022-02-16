package com.fiz.android.battleinthespace.base.presentation.space_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.MainViewModelFactory
import com.fiz.android.battleinthespace.base.presentation.helpers.CallBackItemClick
import com.fiz.android.battleinthespace.base.presentation.helpers.CallBackTypeItemClick
import com.fiz.android.battleinthespace.base.presentation.helpers.ItemsAdapter
import com.fiz.android.battleinthespace.base.presentation.helpers.TypeItemsAdapter
import com.fiz.android.battleinthespace.databinding.FragmentSpaceStationBinding


class SpaceStationFragment : Fragment() {
    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory()
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    private lateinit var binding: FragmentSpaceStationBinding

    private lateinit var typeItemsAdapter: TypeItemsAdapter
    private lateinit var itemsAdapter: ItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpaceStationBinding.inflate(inflater, container, false)

        val layoutManager = GridLayoutManager(activity, 2)
        binding.stationRecycler.layoutManager = layoutManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.type.observe(viewLifecycleOwner) { type ->
            setAdapter(type)
        }

        viewModel.money.observe(viewLifecycleOwner) {
            binding.money.text = getString(R.string.balance, it)
        }


    }

    override fun onResume() {
        super.onResume()
        setAdapter(viewModel.type.value ?: 0)
    }

    private fun setAdapter(type: Int) {
        binding.stationRecycler.adapter =
            if (type == 0) {
                val items = viewModel.getItems()
                typeItemsAdapter = TypeItemsAdapter(items,
                    CallBackTypeItemClick { position: Int ->
                        viewModel.setType(position + 1)
                    })
                typeItemsAdapter
            } else {
                itemsAdapter = ItemsAdapter(viewModel.getItemsWithZero(),
                    CallBackItemClick { position: Int ->
                        viewModel.clickItems(position)
                    })
                itemsAdapter
            }
    }
}