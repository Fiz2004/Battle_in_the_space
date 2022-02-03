package com.fiz.android.battleinthespace.interfaces.main.options

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.databinding.FragmentOptionsBinding
import com.fiz.android.battleinthespace.interfaces.main.MainViewModel

class OptionsFragment : Fragment() {
    private var _binding: FragmentOptionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    companion object {
        interface Listener {
            fun playersEditTexts(id: Int, text: String)
        }
    }

    private lateinit var parentContext: Listener

    private lateinit var playersEditTexts: MutableList<EditText>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentContext = context as Listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        _binding = FragmentOptionsBinding.inflate(inflater, container, false)

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        playersEditTexts = mutableListOf(
            binding.onePlayersEditText,
            binding.twoPlayersEditText,
            binding.threePlayersEditText,
            binding.fourPlayersEditText)

        for (n in 0..3)
            playersEditTexts[n].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) { /* for lint */
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) { /* for lint */
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    parentContext.playersEditTexts(n, s.toString())
                }
            })

        renderInterface()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        renderInterface()
    }

    private fun renderInterface() {
        for (n in 0..3)
            playersEditTexts[n].setText(viewModel.name[n])
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}