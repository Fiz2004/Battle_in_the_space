package com.fiz.android.battleinthespace.presentation.main.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.database.PlayerRepository
import com.fiz.android.battleinthespace.databinding.FragmentOptionsBinding
import com.fiz.android.battleinthespace.presentation.main.MainViewModel
import com.fiz.android.battleinthespace.presentation.main.MainViewModelFactory

class OptionsFragment : Fragment() {
    private var _binding: FragmentOptionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(PlayerRepository.get())
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOptionsBinding.inflate(inflater, container, false)

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}