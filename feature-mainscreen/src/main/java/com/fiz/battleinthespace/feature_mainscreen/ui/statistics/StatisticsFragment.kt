package com.fiz.battleinthespace.feature_mainscreen.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fiz.battleinthespace.feature_mainscreen.data.repositories.PlayerRepositoryImpl
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentStatisticsBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.ApplicationFeatureMainScreen
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModelFactory

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        val app = requireActivity().application as ApplicationFeatureMainScreen
        val playerRepository = PlayerRepositoryImpl(
            app.getPlayersLocalDataSourceFeatureMainScreen(),
            app.getSharedPrefPlayerStorageFeatureMainScreen()
        )
        MainViewModelFactory(playerRepository)
    }

    private val adapter: StatisticsAdapter by lazy { StatisticsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        binding.statisticsRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.players.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
    }
}