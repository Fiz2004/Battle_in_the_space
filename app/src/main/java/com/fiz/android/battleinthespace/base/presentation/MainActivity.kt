package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.presentation.helpers.ActivityContract
import com.fiz.android.battleinthespace.base.presentation.helpers.SectionsPagerAdapter
import com.fiz.android.battleinthespace.databinding.ActivityMainBinding
import com.fiz.android.battleinthespace.game.presentation.GameActivity

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(applicationContext)
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val accountViewModel: AccountViewModel by lazy {
        val viewModelFactory = AccountViewModelFactory()
        ViewModelProvider(this, viewModelFactory)[AccountViewModel::class.java]
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val gameActivityLauncher = registerForActivityResult(ActivityContract()) { result ->
        result?.let { viewModel.gameActivityFinish(result) }
    }

    val googleSignInActivityLauncher = registerForActivityResult(ActivityContract()) { result ->
        result?.let { accountViewModel.signInFirebaseWithGoogle(result) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        accountViewModel.errorTextToToast.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        initUI()

        viewModel.initPlayerIfFirstStart()
    }

    private fun initUI() {
        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = pagerAdapter

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 ->
                        binding.bottomNavigation.menu.findItem(R.id.page_1).isChecked = true
                    1 ->
                        binding.bottomNavigation.menu.findItem(R.id.page_2).isChecked = true
                    2 ->
                        binding.bottomNavigation.menu.findItem(R.id.page_3).isChecked = true
                    3 ->
                        binding.bottomNavigation.menu.findItem(R.id.page_4).isChecked = true
                }
            }
        })

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    binding.viewpager.currentItem = 0
                    true
                }
                R.id.page_2 -> {
                    binding.viewpager.currentItem = 1
                    true
                }
                R.id.page_3 -> {
                    binding.viewpager.currentItem = 2
                    true
                }
                else -> {
                    binding.viewpager.currentItem = 3
                    true
                }
            }
        }

        binding.flyFab.setOnClickListener {
            viewModel.savePlayers()
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




