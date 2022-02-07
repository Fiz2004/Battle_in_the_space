package com.fiz.android.battleinthespace.presentation.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.databinding.ActivityMainBinding
import com.fiz.android.battleinthespace.presentation.game.GameActivity
import com.fiz.android.battleinthespace.presentation.main.options.OptionsFragment
import com.fiz.android.battleinthespace.presentation.main.space_station.SpaceStationFragment
import com.fiz.android.battleinthespace.presentation.main.statistics.StatisticsFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val activityLauncher = registerForActivityResult(GameActivityContract()) { result ->
        if (result != null)
            viewModel.money.value = viewModel.money.value?.plus(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = getTitle(position)
        }.attach()

        binding.flyFab.setOnClickListener {
            activityLauncher.launch("123")
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.savePlayers()
    }

    private fun getTitle(position: Int): CharSequence {
        return when (position) {
            0 -> resources.getText(R.string.title_mission_selection)
            1 -> resources.getText(R.string.title_space_station)
            2 -> resources.getText(R.string.title_statistics)
            else -> resources.getText(R.string.title_options)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    class SectionsPagerAdapter(fm: FragmentManager, lc: Lifecycle) :
        androidx.viewpager2.adapter.FragmentStateAdapter(fm, lc) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MissionSelectedFragment()
                1 -> SpaceStationFragment()
                2 -> StatisticsFragment()
                else -> OptionsFragment()
            }
        }
    }

    inner class GameActivityContract : ActivityResultContract<String, Int?>() {

        override fun createIntent(context: Context, input: String?): Intent {
            val intent = Intent(context, GameActivity::class.java)
            return viewModel.onClickDone(intent)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int? = when {
            resultCode != Activity.RESULT_OK -> null
            else -> intent?.getIntExtra("score", 0)
        }

        override fun getSynchronousResult(context: Context, input: String?): SynchronousResult<Int?>? {
            return if (input.isNullOrEmpty()) SynchronousResult(0) else null
        }
    }
}

