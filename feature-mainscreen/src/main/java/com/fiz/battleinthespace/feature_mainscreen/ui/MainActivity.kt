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
import com.fiz.battleinthespace.feature_mainscreen.databinding.ActivityMainBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.SectionsPagerAdapter
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.SectionsPagerAdapter.Companion.Sections
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
            val data = result.data ?: return
            accountViewModel.signInFirebaseWithGoogle(data)
        } else {
            Toast.makeText(this@MainActivity, "Отмена входа", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.init()
        binding.setupListener()
        binding.setupObserve()
    }

    private fun ActivityMainBinding.init() {
        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        viewpager.adapter = pagerAdapter
    }

    private fun ActivityMainBinding.setupListener() {
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                bottomNavigation.menu.findItem(Sections[position].id).isChecked = true
            }
        })

        bottomNavigation.setOnItemSelectedListener { item ->
            viewpager.currentItem = Sections.indexOfFirst { it.id == item.itemId }
            true
        }

        flyFab.setOnClickListener {
            val intent = Intent("com.fiz.battleinthespace.GameActivity")
            startActivity(intent)
        }
    }

    private fun ActivityMainBinding.setupObserve() {
        launchAndRepeatWithViewLifecycle {
            accountViewModel.textToToast.collect {
                it?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}




