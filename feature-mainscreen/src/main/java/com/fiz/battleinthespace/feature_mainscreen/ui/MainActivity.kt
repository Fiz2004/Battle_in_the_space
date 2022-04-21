package com.fiz.battleinthespace.feature_mainscreen.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorage
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.data.repositories.PlayerRepositoryImpl
import com.fiz.battleinthespace.feature_mainscreen.databinding.ActivityMainBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.SectionsPagerAdapter
import com.fiz.battleinthespace.feature_mainscreen.ui.utils.ActivityContract

interface ApplicationFeatureMainScreen {
    fun getPlayersLocalDataSourceFeatureMainScreen(): PlayersLocalDataSource
    fun getSharedPrefPlayerStorageFeatureMainScreen(): SharedPrefPlayerStorage
    fun getIntentForNextScreen(): Intent
}

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        val app = (application as ApplicationFeatureMainScreen)
        val playerRepository = PlayerRepositoryImpl(
            app.getPlayersLocalDataSourceFeatureMainScreen(),
            app.getSharedPrefPlayerStorageFeatureMainScreen()
        )
        MainViewModelFactory(playerRepository)
    }

    private val accountViewModel: AccountViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val googleSignInActivityLauncher =
        registerForActivityResult(ActivityContract()) { result ->
            result?.let { accountViewModel.signInFirebaseWithGoogle(result) }
        }

    private val itemMenu = listOf(R.id.page_1, R.id.page_2, R.id.page_3, R.id.page_4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        accountViewModel.set(this, googleSignInActivityLauncher)

        init()
        setupListener()
        setupObserve()
    }

    private fun init() {
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
            val intent = (application as ApplicationFeatureMainScreen).getIntentForNextScreen()
            startActivity(intent)
        }
    }

    private fun setupObserve() {
        accountViewModel.errorTextToToast.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}




