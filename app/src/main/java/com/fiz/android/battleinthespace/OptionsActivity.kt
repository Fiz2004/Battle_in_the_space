package com.fiz.android.battleinthespace

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class OptionsActivity : AppCompatActivity() {
    private var name1: String = ""
    private var name2: String = ""
    private var name3: String = ""
    private var name4: String = ""

    private lateinit var onePlayersEditText: EditText
    private lateinit var twoPlayersEditText: EditText
    private lateinit var threePlayersEditText: EditText
    private lateinit var fourPlayersEditText: EditText

    private lateinit var onePlayersRadioButton: RadioButton
    private lateinit var twoPlayersRadioButton: RadioButton
    private lateinit var threePlayersRadioButton: RadioButton
    private lateinit var fourPlayersRadioButton: RadioButton

    private lateinit var exitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        onePlayersEditText = findViewById(R.id.one_players_options_edittext)
        twoPlayersEditText = findViewById(R.id.two_players_options_edittext)
        threePlayersEditText = findViewById(R.id.three_players_options_edittext)
        fourPlayersEditText = findViewById(R.id.four_players_options_edittext)

        onePlayersRadioButton= findViewById(R.id.one_players_options_radiobutton)
        twoPlayersRadioButton= findViewById(R.id.two_players_options_radiobutton)
        threePlayersRadioButton= findViewById(R.id.three_players_options_radiobutton)
        fourPlayersRadioButton= findViewById(R.id.four_players_options_radiobutton)

        exitButton = findViewById(R.id.exit_options_button)

        onePlayersEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                name1 = s.toString()
            }
        })

        twoPlayersEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                name2 = s.toString()
            }
        })

        threePlayersEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                name3 = s.toString()
            }
        })

        fourPlayersEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                name4 = s.toString()
            }
        })

        exitButton.setOnClickListener {
            finish()
        }
    }
}