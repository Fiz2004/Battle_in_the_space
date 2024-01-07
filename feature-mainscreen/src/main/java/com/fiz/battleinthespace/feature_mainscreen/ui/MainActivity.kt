package com.fiz.battleinthespace.feature_mainscreen.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.fiz.battleinthespace.common.collectUiEffect
import com.fiz.battleinthespace.feature_mainscreen.databinding.ActivityMainBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.SectionsPagerAdapter
import com.fiz.battleinthespace.feature_mainscreen.ui.adapters.SectionsPagerAdapter.Companion.Sections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), ::activityCallback,
    )

    private fun activityCallback(result: ActivityResult) {
        if (result.resultCode == RESULT_OK || result.data == null) {
            val data = result.data ?: return
            viewModel.signInFirebaseWithGoogle(data)
        } else {
            Toast.makeText(this@MainActivity, "Отмена входа", Toast.LENGTH_LONG).show()
        }
    }

    fun signInGoogle(intent: Intent) {
        signInLauncher.launch(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.init()
        binding.setupListener()

        collectUiEffect(viewModel.textToToast, { binding.collectUiEffect(it) })
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
            val intent =
                Intent(this@MainActivity, Class.forName("com.fiz.battleinthespace.feature_gamescreen.ui.GameActivity"))
            startActivity(intent)
        }
    }

    private fun ActivityMainBinding.collectUiEffect(uiEffect: String?) {
        uiEffect?.let {
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
        }
    }
}






