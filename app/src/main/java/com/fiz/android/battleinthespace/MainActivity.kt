package com.fiz.android.battleinthespace

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var newGameButton: Button
    private lateinit var optionsButton: Button
    private lateinit var exitButton: Button

    private class Options {
        var countPlayers = 4
        var namePlayer1 = "Player 1"
        var namePlayer2 = "Player 2"
        var namePlayer3 = "Player 3"
        var namePlayer4 = "Player 4"
    }

    private val options = Options()

    var mStartForResult = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            val countPlayer: Int = intent?.getIntExtra("countPlayers", 1) ?: 1
            options.countPlayers = countPlayer
            var result=""
            if (countPlayer >= 1) {
                result=intent?.getStringExtra("namePlayer1").toString()
                options.namePlayer1 = result
            }
            if (countPlayer >= 2) {
                result=intent?.getStringExtra("namePlayer2").toString()
                options.namePlayer2 = result
            }
            if (countPlayer >= 3) {
                result=intent?.getStringExtra("namePlayer3").toString()
                options.namePlayer3 = result
            }
            if (countPlayer >= 4) {
                result=intent?.getStringExtra("namePlayer4").toString()
                options.namePlayer4 = result
            }
        } else {
            options.countPlayers = 1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newGameButton = findViewById(R.id.new_game_main_button)
        optionsButton = findViewById(R.id.options_main_button)
        exitButton = findViewById(R.id.exit_main_button)

        newGameButton.setOnClickListener { view: View ->
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("countPlayers", options.countPlayers)
            intent.putExtra("namePlayer1", options.namePlayer1)
            intent.putExtra("namePlayer2", options.namePlayer2)
            intent.putExtra("namePlayer3", options.namePlayer3)
            intent.putExtra("namePlayer4", options.namePlayer4)
            startActivity(intent)
        }

        optionsButton.setOnClickListener { view: View ->
            val intent = Intent(this, OptionsActivity::class.java)
            intent.putExtra("countPlayers", options.countPlayers)
            intent.putExtra("namePlayer1", options.namePlayer1)
            intent.putExtra("namePlayer2", options.namePlayer2)
            intent.putExtra("namePlayer3", options.namePlayer3)
            intent.putExtra("namePlayer4", options.namePlayer4)
            mStartForResult.launch(intent)
        }

        exitButton.setOnClickListener {
            finish()
        }
    }
}