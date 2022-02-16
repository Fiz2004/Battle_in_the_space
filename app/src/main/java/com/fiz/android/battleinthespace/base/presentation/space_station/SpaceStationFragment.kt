package com.fiz.android.battleinthespace.base.presentation.space_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.Item
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.helpers.CallBackTypeItemClick
import com.fiz.android.battleinthespace.base.presentation.helpers.ItemsAdapter
import com.fiz.android.battleinthespace.base.presentation.helpers.TypeItemsAdapter
import com.fiz.android.battleinthespace.databinding.FragmentSpaceStationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpaceStationFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel>({ requireActivity() })

    private lateinit var binding: FragmentSpaceStationBinding

    private lateinit var typeItemsAdapter: TypeItemsAdapter
    private lateinit var itemsAdapter: ItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpaceStationBinding.inflate(inflater, container, false)

        viewModel.type.observe(viewLifecycleOwner) { type ->
            binding.stationRecycler.adapter =
                if (type == 0) {
                    typeItemsAdapter = TypeItemsAdapter(viewModel.player.items,
                        CallBackTypeItemClick { position: Int ->
                            viewModel.setType(position + 1)
                        })
                    typeItemsAdapter
                } else {
                    val indexType = viewModel.type.value?.minus(1) ?: throw Error("Не доступна Livedata type")
                    val items = viewModel.getItems()
                    var listProduct = items[indexType].items
                    listProduct = Item.addZeroFirstItem(listProduct) as MutableList<Item>
                    itemsAdapter = ItemsAdapter(listProduct)
                    itemsAdapter.setListener { position: Int ->
                        if (position == 0) {
                            viewModel.setType(0)
                        } else {
                            if (listProduct[position].state == StateProduct.BUY) {
                                listProduct.forEachIndexed { index, it ->
                                    if (it.state == StateProduct.INSTALL)
                                        viewModel.changeItems(index - 1, indexType, StateProduct.BUY)
                                }
                                viewModel.changeItems(position - 1, indexType, StateProduct.INSTALL)
                            } else {
                                viewModel.buyItem(position - 1, indexType)
                                binding.money.text = getString(R.string.balance, viewModel.getMoney())
                            }
                            viewModel.setType(viewModel.type.value!!)
                        }
                    }
                    itemsAdapter
                }
        }

        binding.money.text = getString(R.string.balance, viewModel.getMoney())

        val layoutManager = GridLayoutManager(activity, 2)
        binding.stationRecycler.layoutManager = layoutManager

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.money.text = getString(R.string.balance, viewModel.getMoney())
    }

}