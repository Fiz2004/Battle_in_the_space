package com.fiz.android.battleinthespace

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity


class OptionsActivity : AppCompatActivity() {

    private var playersEditTexts: MutableList<EditText> = mutableListOf()
    private var playersRadioButtons: MutableList<RadioButton> = mutableListOf()
    private var playersToggleButtons: MutableList<ToggleButton> = mutableListOf()

    private lateinit var exitButton: Button

    private lateinit var options: Options

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
            options = extras.getSerializable(Options::class.java.simpleName) as Options
            playersRadioButtons[options.countPlayers - 1].isChecked = true

            for (n in 0..3)
                playersEditTexts[n].setText(options.name[n])

            for (n in 0..3)
                playersToggleButtons[n].isChecked = options.playerControllerPlayer[n]
        } else {
            options = Options(applicationContext)
        }

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
                    options.name[n] = s.toString()
                }
            })

        exitButton.setOnClickListener {
            val data = Intent()
            data.putExtra(Options::class.java.simpleName, options)
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Options::class.java.simpleName, options)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        options = savedInstanceState.getSerializable(Options::class.java.simpleName) as Options
        playersRadioButtons[options.countPlayers - 1].isChecked = true

        for (n in 0..3)
            playersEditTexts[n].setText(options.name[n])

        for (n in 0..3)
            playersToggleButtons[n].isChecked = options.playerControllerPlayer[n]
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
                (it == playersToggleButtons[n]) -> options.playerControllerPlayer[n] = playersToggleButtons[n].isChecked
            }
    }
}