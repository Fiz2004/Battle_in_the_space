package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.base.presentation.helpers.ActivityContract
import com.fiz.android.battleinthespace.base.presentation.helpers.SectionsPagerAdapter
import com.fiz.android.battleinthespace.databinding.ActivityMainBinding
import com.fiz.android.battleinthespace.game.presentation.GameActivity
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory()
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val gameActivityLauncher = registerForActivityResult(ActivityContract()) { result ->
        result?.let { viewModel.gameActivityFinish(result) }
    }

    val googleSignInActivityLauncher = registerForActivityResult(ActivityContract()) { result ->
        result?.let { viewModel.signInFirebaseWithGoogle(result) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.playerListLiveData.observe(this) {
            viewModel.refreshPlayerListLiveData(it)
        }

        viewModel.errorTextToToast.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = SectionsPagerAdapter.getTitle(this, position)
        }.attach()

        binding.flyFab.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            gameActivityLauncher.launch(viewModel.getDataForIntent(intent))
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.savePlayers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.addSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}




