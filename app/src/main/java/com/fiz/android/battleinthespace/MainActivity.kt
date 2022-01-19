package com.fiz.android.battleinthespace

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var newGameButton: Button
    private lateinit var optionsButton: Button
    private lateinit var exitButton: Button

    private class Options {
        var countPlayers = 4
        var name: Array<String> = Array(4) {i-> "Player ${i+1}" }
        var playerControllerPlayer: Array<Boolean> = arrayOf(true,false,false,false)
    }

    private val options = Options()

    var mStartForResult = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            val countPlayer: Int = intent?.getIntExtra("countPlayers", 1) ?: 1
            options.countPlayers = countPlayer

            for (n in 0..3)
                if (countPlayer >= n + 1)
                    options.name[n] = intent?.getStringExtra("namePlayer${n + 1}").toString()

            for (n in 0..3)
                if (countPlayer >= n + 1)
                    options.playerControllerPlayer[n] = intent?.getBooleanExtra("controller${n + 1}", false) ?: false

        } else {
            options.countPlayers = 1
            for (n in 0..3)
                options.playerControllerPlayer[n] = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newGameButton = findViewById(R.id.new_game_main_button)
        optionsButton = findViewById(R.id.options_main_button)
        exitButton = findViewById(R.id.exit_main_button)

        newGameButton.setOnClickListener { view: View ->
            startActivity(getIntent(this, GameActivity::class.java))
        }

        optionsButton.setOnClickListener { view: View ->
            mStartForResult.launch(getIntent(this, OptionsActivity::class.java))
        }

        exitButton.setOnClickListener {
            finish()
        }
    }


    private fun getIntent(context: Context,classes:Class<*>):Intent {
        val result = Intent(context, classes)
        result.putExtra("countPlayers", options.countPlayers)
        for (n in 0..3)
            result.putExtra("namePlayer${n + 1}", options.name[n])
        for (n in 0..3)
            result.putExtra("controller${n + 1}", options.playerControllerPlayer[n])
        return result
    }
}