package com.fiz.battleinthespace.feature_mainscreen.ui.mission_selected

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fiz.battleinthespace.feature_mainscreen.R

internal class MissionDestroyMeteoriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mission_destroy_meteorite, container, false)
    }
}