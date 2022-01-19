package com.fiz.android.battleinthespace

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class OptionsActivity : AppCompatActivity() {
    private var name: Array<String> = Array(4) { "" }
    private var playerControllerPlayer: Array<Boolean> = arrayOf(true,false,false,false)

    private var playersEditTexts: MutableList<EditText> = mutableListOf()
    private var playersRadioButtons: MutableList<RadioButton> = mutableListOf()
    private var playersToggleButtons: MutableList<ToggleButton> = mutableListOf()

    private lateinit var exitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        playersEditTexts.add(findViewById(R.id.one_players_options_edittext))
        playersEditTexts.add(findViewById(R.id.two_players_options_edittext))
        playersEditTexts.add(findViewById(R.id.three_players_options_edittext))
        playersEditTexts.add(findViewById(R.id.four_players_options_edittext))

        playersRadioButtons.add(findViewById(R.id.one_players_options_radiobutton))
        playersRadioButtons.add(findViewById(R.id.two_players_options_radiobutton))
        playersRadioButtons.add(findViewById(R.id.three_players_options_radiobutton))
        playersRadioButtons.add(findViewById(R.id.four_players_options_radiobutton))

        playersToggleButtons.add(findViewById(R.id.one_players_options_togglebutton))
        playersToggleButtons.add(findViewById(R.id.two_players_options_togglebutton))
        playersToggleButtons.add(findViewById(R.id.three_players_options_togglebutton))
        playersToggleButtons.add(findViewById(R.id.four_players_options_togglebutton))

        for (playersRadioButton in playersRadioButtons)
            playersRadioButton.setOnClickListener(onRadioClick())

        for (playersToggleButton in playersToggleButtons)
            playersToggleButton.setOnClickListener(onToggleClick())

        exitButton = findViewById(R.id.exit_options_button)

        val extras = intent.extras

        if (extras != null) {
            playersRadioButtons[extras.getInt("countPlayers") - 1].isChecked = true

            for (n in 0..3)
                name[n] = extras.getString("namePlayer${n + 1}").toString()

            for (n in 0..3)
                playersEditTexts[n].setText(name[n])

            for (n in 0..3)
                playerControllerPlayer[n] = extras.getBoolean("controller${n + 1}")

            for (n in 0..3)
                playersToggleButtons[n].isChecked = playerControllerPlayer[n]
        }

        for (n in 0..3)
            playersEditTexts[n].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    name[n] = s.toString()
                }
            })

        exitButton.setOnClickListener {
            val data = Intent()
            for (n in 0..3)
                data.putExtra("namePlayer${n + 1}", name[n])

            for (n in 0..3)
                if (playersRadioButtons[n].isChecked)
                    data.putExtra("countPlayers", n + 1)

            for (n in 0..3)
                data.putExtra("controller${n + 1}", playerControllerPlayer[n])

            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun onRadioClick(): (View) -> Unit = {
        for (n in 0..3)
            when {
                (it == playersRadioButtons[n]) -> playersRadioButtons[n].isChecked = true
                else -> playersRadioButtons[n].isChecked = false
            }
    }

    private fun onToggleClick(): (View) -> Unit = {
        for (n in 0..3)
            when {
                (it == playersToggleButtons[n]) -> playerControllerPlayer[n] = playersToggleButtons[n].isChecked
            }
    }
}