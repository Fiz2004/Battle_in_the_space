package com.fiz.android.battleinthespace.presentation.main.space_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fiz.android.battleinthespace.data.Product
import com.fiz.android.battleinthespace.data.ProductTypes
import com.fiz.android.battleinthespace.data.Products
import com.fiz.android.battleinthespace.data.StateProduct
import com.fiz.android.battleinthespace.databinding.FragmentSpaceStationBinding
import com.fiz.android.battleinthespace.presentation.main.MainViewModel

class SpaceStationFragment : Fragment() {
    private var _binding: FragmentSpaceStationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    private lateinit var captionImageAdapter: CaptionImageAdapter
    private lateinit var imagesCaptionCaptionAdapter: ImagesCaptionCaptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpaceStationBinding.inflate(inflater, container, false)

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val layoutManager = GridLayoutManager(activity, 2)
        binding.stationRecycler.layoutManager = layoutManager
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        binding.stationRecycler.adapter = if (viewModel.type.value == 0) {
            configureCaptionImageAdapter()
            captionImageAdapter
        } else {
            configureImagesCaptionCaptionAdapter()
            imagesCaptionCaptionAdapter
        }
    }

    private fun configureCaptionImageAdapter() {
        captionImageAdapter = CaptionImageAdapter(ProductTypes.createTypes())

        captionImageAdapter.setListener { position: Int ->
            viewModel.setType(position + 1)
            updateUI()
        }
    }

    private fun configureImagesCaptionCaptionAdapter() {
        val currentType = viewModel.type.value?.minus(1) ?: 0
        val nameType = ProductTypes.createTypes()[currentType].name
        val items = viewModel.playerLiveData.value?.items ?: return
        val listProduct = Product.getListProduct(nameType, items)

        imagesCaptionCaptionAdapter = ImagesCaptionCaptionAdapter(listProduct)

        imagesCaptionCaptionAdapter.setListener { position: Int ->
            if (position == 0) {
                viewModel.setType(0)
            } else {
                if (listProduct[position].state == StateProduct.BUY) {
                    val allProductsType = Products.createListProducts().filter { it.type == nameType }
                    allProductsType.forEach {
                        if (items[it.name] == StateProduct.INSTALL)
                            viewModel.playerLiveData.value?.items!![it.name] = StateProduct.BUY
                    }
                    val key = listProduct[position].name
                    viewModel.playerLiveData.value?.items!![key] = StateProduct.INSTALL
                } else
                    if (viewModel.playerLiveData.value?.money?.minus(listProduct[position].cost)!! >= 0) {
                        viewModel.configureMoney(listProduct[position].cost)
                        val key = listProduct[position].name
                        viewModel.playerLiveData.value?.items!![key] = StateProduct.BUY
                    }
            }
            updateUI()
        }
    }
}