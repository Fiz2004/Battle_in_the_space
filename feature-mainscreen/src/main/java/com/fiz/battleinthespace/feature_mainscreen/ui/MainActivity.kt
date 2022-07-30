package com.fiz.battleinthespace.feature_mainscreen.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.fiz.battleinthespace.common.launchAndRepeatWithViewLifecycle
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.ActivityMainBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.SectionsPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val accountViewModel: AccountViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), ::activityCallback,
    )

    private fun activityCallback(result: ActivityResult) {
        if (result.resultCode == RESULT_OK || result.data == null) {
            result.data?.let { data ->
                accountViewModel.signInFirebaseWithGoogle(data)
            }
        } else {
            Toast.makeText(this@MainActivity, "Отмена входа", Toast.LENGTH_LONG).show()
        }
    }

    private val itemMenu = listOf(R.id.page_1, R.id.page_2, R.id.page_3, R.id.page_4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
            val intent = Intent("com.fiz.battleinthespace.GameActivity")
            startActivity(intent)
        }
    }

    private fun setupObserve() {
        launchAndRepeatWithViewLifecycle {
            accountViewModel.textToToast.collect {
                it?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}




