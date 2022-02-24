package com.fiz.android.battleinthespace.base.presentation.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.MainViewModelFactory
import com.fiz.android.battleinthespace.base.presentation.helpers.StatisticsAdapter
import com.fiz.android.battleinthespace.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(requireActivity().applicationContext)
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    private lateinit var adapter: StatisticsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        adapter = StatisticsAdapter()
        binding.statisticsRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.players?.let { adapter.setData(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}