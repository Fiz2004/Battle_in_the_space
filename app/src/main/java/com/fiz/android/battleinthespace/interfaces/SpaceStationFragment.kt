package com.fiz.android.battleinthespace.interfaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.options.Station

class SpaceStationFragment : Fragment() {

    private lateinit var station: Station
    private lateinit var moneyTextView: TextView
    private var type: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            station = savedInstanceState.getSerializable(Station::class.java.simpleName) as Station
            type = savedInstanceState.getInt("Type")
        } else {
            station = Station(requireContext())
        }

        val stationView = inflater.inflate(
            R.layout.fragment_space_station, container, false
        )

        val stationRecycler = stationView.findViewById<RecyclerView>(R.id.station_recycler)
        if (type == 0)
            configureCaptionImageAdapter(stationRecycler)
        else
            configureImagesCaptionCaptionAdapter(stationRecycler)

        moneyTextView = stationView.findViewById(R.id.money)
        moneyTextView.text = station.money.toString()

        return stationView
    }

    private fun configureCaptionImageAdapter(stationRecycler: RecyclerView) {
        val names = mutableListOf<String>()
        val images = mutableListOf<Int>()
        for (type in Station.types) {
            names += type.names
            images += type.imageIds
        }
        val adapter = CaptionImageAdapter(names, images)
        stationRecycler.adapter = adapter
        val layoutManager = GridLayoutManager(activity, 2)
        stationRecycler.layoutManager = layoutManager

        adapter.setListener { position: Int ->
            type = position + 1
            configureImagesCaptionCaptionAdapter(stationRecycler)
        }
    }

    private fun configureImagesCaptionCaptionAdapter(stationRecycler: RecyclerView) {
        val adapter = createImagesCaptionCaptionAdapter()
        stationRecycler.adapter = adapter
        val layoutManager = GridLayoutManager(activity, 2)
        stationRecycler.layoutManager = layoutManager

        setListenerAdapter(adapter, stationRecycler)
    }

    fun createImagesCaptionCaptionAdapter(): ImagesCaptionCaptionAdapter {
        val names = mutableListOf<String>("Назад")
        val images = mutableListOf<Int>(R.drawable.weapon_1)
        val costs = mutableListOf<Int>(0)
        val states = mutableListOf<stateProduct>(stateProduct.NONE)
        for (type in Station.types[type - 1].products) {
            names += type.name
            images += type.imageId
            costs += type.cost
            states += type.state
        }
        return ImagesCaptionCaptionAdapter(names, costs, images, states)
    }

    private fun setListenerAdapter(
        adapter: ImagesCaptionCaptionAdapter,
        stationRecycler: RecyclerView,
    ) {
        var adapter1 = adapter
        adapter1.setListener { position: Int ->
            if (position == 0) {
                configureCaptionImageAdapter(stationRecycler)
            } else {
                val localtype = type - 1
                if (Station.types[localtype].products[position - 1].state == stateProduct.BUY) {
                    Station.types[localtype].products.forEach {
                        if (it.state == stateProduct.INSTALL) it.state = stateProduct.BUY
                    }
                    Station.types[localtype].products[position - 1].state = stateProduct.INSTALL
                } else
                    if (station.money - Station.types[localtype].products[position - 1].cost >= 0) {
                        station.money -= Station.types[localtype].products[position - 1].cost
                        moneyTextView.text = station.money.toString()
                        Station.types[localtype].products[position - 1].state = stateProduct.BUY
                    }
                adapter1 = createImagesCaptionCaptionAdapter()
                setListenerAdapter(adapter1, stationRecycler)
                stationRecycler.adapter = adapter1
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Station::class.java.simpleName, station)
        outState.putInt("Type", type)
        super.onSaveInstanceState(outState)
    }
}