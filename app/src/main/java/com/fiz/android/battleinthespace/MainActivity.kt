package com.fiz.android.battleinthespace

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var newGameButton: Button
    private lateinit var optionsButton:Button
    private lateinit var exitButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newGameButton=findViewById(R.id.new_game_main_button)
        optionsButton=findViewById(R.id.options_main_button)
        exitButton=findViewById(R.id.exit_main_button)

        newGameButton.setOnClickListener { view: View ->
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        optionsButton.setOnClickListener {  view: View ->
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
        }

        exitButton.setOnClickListener {
            finish()
        }
    }
}