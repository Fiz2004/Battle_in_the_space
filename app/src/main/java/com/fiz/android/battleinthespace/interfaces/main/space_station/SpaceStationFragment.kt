package com.fiz.android.battleinthespace.interfaces.main.space_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fiz.android.battleinthespace.databinding.FragmentSpaceStationBinding
import com.fiz.android.battleinthespace.interfaces.main.MainViewModel
import com.fiz.android.battleinthespace.options.Item
import com.fiz.android.battleinthespace.options.StateProduct
import com.fiz.android.battleinthespace.options.Types

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
        captionImageAdapter = CaptionImageAdapter(Types.createTypes())

        captionImageAdapter.setListener { position: Int ->
            viewModel.type.value = position + 1
            updateUI()
        }
    }

    private fun configureImagesCaptionCaptionAdapter() {
        val itemsGroup: MutableMap<Int, List<Item>> = mutableMapOf()
        viewModel.playerListLiveData.value?.get(0)?.items!!.groupBy { it.type }.values.forEachIndexed { index, list ->
            itemsGroup[index] = list
        }

        val listItems = mutableListOf<Item>(Item())
        listItems += itemsGroup[viewModel.type.value?.minus(1)]!!

        imagesCaptionCaptionAdapter = ImagesCaptionCaptionAdapter(listItems)

        imagesCaptionCaptionAdapter.setListener { position: Int ->
            if (position == 0) {
                viewModel.type.value = 0
            } else {
                val localtype = viewModel.type.value?.minus(1) ?: 0
                if (listItems[position - 1].state == StateProduct.BUY) {
                    listItems.forEach {
                        if (it.state == StateProduct.INSTALL) it.state = StateProduct.BUY
                    }
                    listItems[position - 1].state = StateProduct.INSTALL
                } else
                    if (viewModel.playerListLiveData.value?.get(0)?.money?.minus(listItems[position - 1].cost)!! >= 0) {
                        viewModel.configureMoney(listItems[position - 1].cost)
                        listItems[position - 1].state = StateProduct.BUY
                    }
            }
            updateUI()
        }
    }
}