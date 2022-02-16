package com.fiz.android.battleinthespace.base.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.presentation.options.OptionsFragment
import com.fiz.android.battleinthespace.base.presentation.space_station.SpaceStationFragment
import com.fiz.android.battleinthespace.base.presentation.statistics.StatisticsFragment
import com.fiz.android.battleinthespace.databinding.ActivityMainBinding
import com.fiz.android.battleinthespace.game.presentation.GameActivity
import com.fiz.android.battleinthespace.presentation.main.MissionSelectedFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(PlayerRepository.get())
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val gameActivityLauncher = registerForActivityResult(GameActivityContract()) { result ->
        if (result != null) {
            viewModel.addMoney(value = result)
            viewModel.savePlayers()
        }
    }

    val googleSignInActivityLauncher = registerForActivityResult(GoogleSignInActivityContract()) { result ->
        if (result != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    viewModel.signInFirebaseWithGoogle(account.idToken!!)
                } else {
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.playerListLiveData.observe(this) {
            if (it == null || it.isEmpty())
                viewModel.fillInitValue()
            else
                viewModel.initPlayer(it[0])
        }

        viewModel.errorTextToToast.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = getTitle(position)
        }.attach()

        binding.flyFab.setOnClickListener {
            gameActivityLauncher.launch("123")
        }
    }

    private fun getTitle(position: Int): CharSequence {
        return when (position) {
            0 -> resources.getText(R.string.title_mission_selection)
            1 -> resources.getText(R.string.title_space_station)
            2 -> resources.getText(R.string.title_statistics)
            else -> resources.getText(R.string.title_options)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.savePlayers()
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

        override fun createIntent(context: Context, input: String): Intent {
            val intent = Intent(context, GameActivity::class.java)
            return viewModel.getDataForIntent(intent)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int? = when {
            resultCode != Activity.RESULT_OK -> null
            else -> intent?.getIntExtra("score", 0)
        }

        override fun getSynchronousResult(context: Context, input: String): SynchronousResult<Int?>? {
            return if (input.isNullOrEmpty()) SynchronousResult(0) else null
        }
    }
}

class GoogleSignInActivityContract : ActivityResultContract<Intent, Intent?>() {

    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent
    }
}

