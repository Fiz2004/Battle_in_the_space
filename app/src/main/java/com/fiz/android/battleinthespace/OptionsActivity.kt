package com.fiz.android.battleinthespace

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView


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

        onePlayersRadioButton = findViewById(R.id.one_players_options_radiobutton)
        twoPlayersRadioButton = findViewById(R.id.two_players_options_radiobutton)
        threePlayersRadioButton = findViewById(R.id.three_players_options_radiobutton)
        fourPlayersRadioButton = findViewById(R.id.four_players_options_radiobutton)

        onePlayersRadioButton.setOnClickListener(onRadioClick())
        twoPlayersRadioButton.setOnClickListener(onRadioClick())
        threePlayersRadioButton.setOnClickListener(onRadioClick())
        fourPlayersRadioButton.setOnClickListener(onRadioClick())

        exitButton = findViewById(R.id.exit_options_button)

        val extras = intent.extras

        if (extras != null) {
            if (extras.getInt("countPlayers") == 1) {
                onePlayersRadioButton.isChecked = true
            }
            if (extras.getInt("countPlayers") == 2) {
                twoPlayersRadioButton.isChecked = true
            }
            if (extras.getInt("countPlayers") == 3) {
                threePlayersRadioButton.isChecked = true
            }
            if (extras.getInt("countPlayers") == 4) {
                fourPlayersRadioButton.isChecked = true
            }
            name1= extras.getString("namePlayer1").toString()
            name2=extras.getString("namePlayer2").toString()
            name3=extras.getString("namePlayer3").toString()
            name4=extras.getString("namePlayer4").toString()
            onePlayersEditText.setText(name1)
            twoPlayersEditText.setText(name2)
            threePlayersEditText.setText(name3)
            fourPlayersEditText.setText(name4)
        }

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
            val data = Intent()
            data.putExtra("namePlayer1",name1)
            data.putExtra("namePlayer2",name2)
            data.putExtra("namePlayer3",name3)
            data.putExtra("namePlayer4",name4)
            if (onePlayersRadioButton.isChecked){
                data.putExtra("countPlayers", 1)
            }
            if (twoPlayersRadioButton.isChecked) {
            }
            if (threePlayersRadioButton.isChecked) {
                data.putExtra("countPlayers", 3)
            }
            if (fourPlayersRadioButton.isChecked) {
                data.putExtra("countPlayers", 4)
            }
            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun onRadioClick(): (View) -> Unit = {
        when (it) {
            onePlayersRadioButton -> {
                onePlayersRadioButton.isChecked = true
                twoPlayersRadioButton.isChecked = false
                threePlayersRadioButton.isChecked = false
                fourPlayersRadioButton.isChecked = false
            }
            twoPlayersRadioButton -> {
                onePlayersRadioButton.isChecked = false
                twoPlayersRadioButton.isChecked = true
                threePlayersRadioButton.isChecked = false
                fourPlayersRadioButton.isChecked = false
            }
            threePlayersRadioButton -> {
                onePlayersRadioButton.isChecked = false
                twoPlayersRadioButton.isChecked = false
                threePlayersRadioButton.isChecked = true
                fourPlayersRadioButton.isChecked = false
            }
            fourPlayersRadioButton -> {
                onePlayersRadioButton.isChecked = false
                twoPlayersRadioButton.isChecked = false
                threePlayersRadioButton.isChecked = false
                fourPlayersRadioButton.isChecked = true
            }
        }
    }

}