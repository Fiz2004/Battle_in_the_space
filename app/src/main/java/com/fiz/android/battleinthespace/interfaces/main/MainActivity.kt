package com.fiz.android.battleinthespace.interfaces.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.databinding.ActivityMainBinding
import com.fiz.android.battleinthespace.interfaces.game.GameActivity
import com.fiz.android.battleinthespace.interfaces.main.options.OptionsFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), OptionsFragment.Companion.Listener,
    MissionSelectedFragment.Companion.Listener {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = getTitle(position)
        }.attach()
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

    fun onClickDone(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(viewModel.onClickDone(intent))
    }

    override fun playersEditTexts(id: Int, text: String) {
        viewModel.playersEditTexts(id, text)
    }

    override fun playersToggleButtonsClicked(id: Int) {
        viewModel.playersToggleButtonsClicked(id)
    }

    private inner class SectionsPagerAdapter(fm: FragmentManager, lc: Lifecycle) :
        androidx.viewpager2.adapter.FragmentStateAdapter(fm, lc) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            return viewModel.createFragment(position)
        }
    }

    override fun changeFragment(id: Int) {
        viewModel.changeFragment(id)
    }
}

