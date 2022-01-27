package com.fiz.android.battleinthespace.interfaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.R

class SpaceStationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val station = inflater.inflate(
            R.layout.fragment_space_station, container, false
        )

        val stationRecycler = station.findViewById<RecyclerView>(R.id.station_recycler)

        val names = listOf<String>("Оружие", "Скорость полета", "Скорость поворота", "Скорость стрельбы", "Вес")
        val images = listOf<Int>(
            R.drawable.meteorite11,
            R.drawable.meteorite21,
            R.drawable.meteorite31,
            R.drawable.meteorite41,
            R.drawable.meteorite12
        )
        val cost = listOf<Int>(0, 0, 0, 0, 0)

        val adapter = ImagesCaptionCaptionAdapter(names, cost, images)
        stationRecycler.adapter = adapter
        val layoutManager = GridLayoutManager(activity, 2)
        stationRecycler.layoutManager = layoutManager
        return station
    }

}