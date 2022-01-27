package com.fiz.android.battleinthespace.`interface`

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.game.Options
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), OptionsFragment.Companion.Listener {
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

        options = Options(applicationContext)

        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        val viewPager = findViewById<ViewPager2>(R.id.viewpager)
        viewPager.adapter = pagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
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
        outState.putSerializable(Options::class.java.simpleName, options)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        options = savedInstanceState.getSerializable(Options::class.java.simpleName) as Options
    }


    fun onClickDone(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(Options::class.java.simpleName, options)
        startActivity(intent)
    }

    override fun playersRadioButtonsClicked(id: Int) {
        options.countPlayers = id + 1
    }

    override fun playersEditTexts(id: Int, text: String) {
        options.name[id] = text
    }

    override fun playersToggleButtonsClicked(id: Int) {
        options.playerControllerPlayer[id] = !options.playerControllerPlayer[id]
    }

    private inner class SectionsPagerAdapter(fm: FragmentManager, lc: Lifecycle) :
        androidx.viewpager2.adapter.FragmentStateAdapter(fm, lc) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> MissionSelectedFragment()
                1 -> SpaceStationFragment()
                2 -> StatisticsFragment()
                else -> OptionsFragment()
            }

            val bundle = Bundle()
            bundle.putSerializable(Options::class.java.simpleName, options)
            fragment.arguments = bundle

            return fragment
        }

    }

}

