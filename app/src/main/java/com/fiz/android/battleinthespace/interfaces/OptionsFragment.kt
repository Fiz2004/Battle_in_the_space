package com.fiz.android.battleinthespace.interfaces

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.options.Options


class OptionsFragment : Fragment() {

    companion object {
        interface Listener {
            fun playersRadioButtonsClicked(id: Int)
            fun playersEditTexts(id: Int, text: String)
            fun playersToggleButtonsClicked(id: Int)
        }
    }

    private lateinit var parentContext: Listener
    private lateinit var options: Options

    private var playersEditTexts: MutableList<EditText> = mutableListOf()
    private var playersRadioButtons: MutableList<RadioButton> = mutableListOf()
    private var playersToggleButtons: MutableList<ToggleButton> = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentContext = context as Listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val extras = arguments

        options = if (extras != null) {
            extras.getSerializable(Options::class.java.simpleName) as Options
        } else {
            Options(requireContext())
        }
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onStart() {
        super.onStart()
        val localView = view ?: return

        playersEditTexts.add(localView.findViewById(R.id.one_players_options_edittext))
        playersEditTexts.add(localView.findViewById(R.id.two_players_options_edittext))
        playersEditTexts.add(localView.findViewById(R.id.three_players_options_edittext))
        playersEditTexts.add(localView.findViewById(R.id.four_players_options_edittext))

        playersRadioButtons.add(localView.findViewById(R.id.one_players_options_radiobutton))
        playersRadioButtons.add(localView.findViewById(R.id.two_players_options_radiobutton))
        playersRadioButtons.add(localView.findViewById(R.id.three_players_options_radiobutton))
        playersRadioButtons.add(localView.findViewById(R.id.four_players_options_radiobutton))

        playersToggleButtons.add(localView.findViewById(R.id.one_players_options_togglebutton))
        playersToggleButtons.add(localView.findViewById(R.id.two_players_options_togglebutton))
        playersToggleButtons.add(localView.findViewById(R.id.three_players_options_togglebutton))
        playersToggleButtons.add(localView.findViewById(R.id.four_players_options_togglebutton))

        for (playersRadioButton in playersRadioButtons)
            playersRadioButton.setOnClickListener(onRadioClick())

        for (playersToggleButton in playersToggleButtons)
            playersToggleButton.setOnClickListener(onToggleClick())

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
    }

    private fun renderInterface() {
        playersRadioButtons[options.countPlayers - 1].isChecked = true

        for (n in 0..3)
            playersEditTexts[n].setText(options.name[n])

        for (n in 0..3)
            playersToggleButtons[n].isChecked = options.playerControllerPlayer[n]
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Options::class.java.simpleName, options)
        super.onSaveInstanceState(outState)
    }

    private fun onRadioClick(): (View) -> Unit = {
        for (n in 0..3)
            if (it == playersRadioButtons[n])
                parentContext.playersRadioButtonsClicked(n)
        for (n in 0..3)
            when {
                (it == playersRadioButtons[n]) ->
                    playersRadioButtons[n].isChecked = true

                else ->
                    playersRadioButtons[n].isChecked = false
            }
    }

    private fun onToggleClick(): (View) -> Unit = {
        for (n in 0..3)
            if (it == playersToggleButtons[n])
                parentContext.playersToggleButtonsClicked(n)
    }
}