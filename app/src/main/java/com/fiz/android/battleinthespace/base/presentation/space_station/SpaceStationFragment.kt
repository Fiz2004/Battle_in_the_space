package com.fiz.android.battleinthespace.base.presentation.space_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.*
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.MainViewModelFactory
import com.fiz.android.battleinthespace.base.presentation.helpers.CaptionImageAdapter
import com.fiz.android.battleinthespace.base.presentation.helpers.ImagesCaptionCaptionAdapter
import com.fiz.android.battleinthespace.databinding.FragmentSpaceStationBinding

class SpaceStationFragment : Fragment() {
    private var _binding: FragmentSpaceStationBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(PlayerRepository.get())
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    private lateinit var captionImageAdapter: CaptionImageAdapter
    private lateinit var imagesCaptionCaptionAdapter: ImagesCaptionCaptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpaceStationBinding.inflate(inflater, container, false)

        viewModel.type.observe(viewLifecycleOwner) { type ->
            binding.stationRecycler.adapter = if (type == 0) {
                captionImageAdapter = CaptionImageAdapter(ItemTypesDefault.createTypes())
                captionImageAdapter.setListener { position: Int ->
                    viewModel.setType(position + 1)
                }
                captionImageAdapter
            } else {
                val currentType = viewModel.type.value?.minus(1) ?: throw Error("Не доступна Livedata type")
                val nameType = ItemTypesDefault.createTypes()[currentType].name
                val items = viewModel.items.value ?: throw Error("Не доступна Livedata items")
                val listProduct = Item.getListProduct(nameType, items)
                imagesCaptionCaptionAdapter = ImagesCaptionCaptionAdapter(listProduct)
                imagesCaptionCaptionAdapter.setListener { position: Int ->
                    if (position == 0) {
                        viewModel.setType(0)
                    } else {
                        if (listProduct[position].state == StateProduct.BUY) {
                            val allProductsType = ItemDefault.createListItems().filter { it.type == nameType }
                            allProductsType.forEach {
                                if (items[it.name] == StateProduct.INSTALL)
                                    viewModel.changeItems(it.name, StateProduct.BUY)
                            }
                            val key = listProduct[position].name
                            viewModel.changeItems(key, StateProduct.INSTALL)
                        } else {
                            val money = viewModel.money.value ?: throw Error("Не доступна Livedata money")
                            if (money - listProduct[position].cost >= 0) {
                                viewModel.moneyMinus(listProduct[position].cost)
                                val key = listProduct[position].name
                                viewModel.changeItems(key, StateProduct.BUY)
                            }
                        }
                    }
                }
                imagesCaptionCaptionAdapter
            }
        }

        viewModel.money.observe(viewLifecycleOwner) { money ->
            binding.money.text = getString(R.string.balance, money)
        }

        val layoutManager = GridLayoutManager(activity, 2)
        binding.stationRecycler.layoutManager = layoutManager

        return binding.root
    }

}