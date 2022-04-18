package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.storage.SharedPrefPlayerStorage
import com.fiz.android.battleinthespace.base.presentation.utils.ActivityContract
import com.fiz.android.battleinthespace.base.presentation.utils.SectionsPagerAdapter
import com.fiz.android.battleinthespace.databinding.ActivityMainBinding
import com.fiz.android.battleinthespace.game.presentation.GameActivity

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(SharedPrefPlayerStorage(applicationContext))
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val accountViewModel: AccountViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val gameActivityLauncher = registerForActivityResult(ActivityContract()) { result ->
        result?.let { viewModel.gameActivityFinish(result) }
    }

    val googleSignInActivityLauncher = registerForActivityResult(ActivityContract()) { result ->
        result?.let { accountViewModel.signInFirebaseWithGoogle(result) }
    }

    val itemMenu = listOf(R.id.page_1, R.id.page_2, R.id.page_3, R.id.page_4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
        setupListener()
        setupObserve()
    }

    private fun init() {
        viewModel.initPlayerIfFirstStart()

        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = pagerAdapter
    }

    private fun setupListener() {
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.bottomNavigation.menu.findItem(itemMenu[position]).isChecked = true
            }
        })

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            binding.viewpager.currentItem = itemMenu.indexOf(item.itemId)
            true
        }

        binding.flyFab.setOnClickListener {
            viewModel.savePlayers()
            val intent = Intent(this, GameActivity::class.java)
            gameActivityLauncher.launch(viewModel.getDataForIntent(intent))
        }
    }

    private fun setupObserve() {
        accountViewModel.errorTextToToast.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
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




