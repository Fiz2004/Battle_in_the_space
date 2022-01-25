package com.fiz.android.battleinthespace

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var newGameButton: Button
    private lateinit var optionsButton: Button
    private lateinit var exitButton: Button

    private lateinit var options: Options

    private var mStartForResult = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        options = if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            intent?.getSerializableExtra(Options::class.java.simpleName) as Options
        } else {
            Options(applicationContext)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newGameButton = findViewById(R.id.new_game_main_button)
        optionsButton = findViewById(R.id.options_main_button)
        exitButton = findViewById(R.id.exit_main_button)

        options = Options(applicationContext)

        newGameButton.setOnClickListener {
            startActivity(getIntent(this, GameActivity::class.java))
        }

        optionsButton.setOnClickListener {
            mStartForResult.launch(getIntent(this, OptionsActivity::class.java))
        }

        exitButton.setOnClickListener {
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
    }


    private fun getIntent(context: Context, classes: Class<*>): Intent {
        val result = Intent(context, classes)
        result.putExtra(Options::class.java.simpleName, options)
        return result
    }
}