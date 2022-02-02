package com.fiz.android.battleinthespace.interfaces.main.space_station

import android.content.Context
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
    private lateinit var stationRecycler: RecyclerView
    private lateinit var captionImageAdapter: CaptionImageAdapter
    private lateinit var imagesCaptionCaptionAdapter: ImagesCaptionCaptionAdapter
    private var type: Int = 0

    private lateinit var parentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            station = savedInstanceState.getSerializable(Station::class.java.simpleName) as Station
            type = savedInstanceState.getInt("Type")
        } else {
            station = Station()
        }

        return inflater.inflate(
            R.layout.fragment_space_station, container, false
        )
    }

    override fun onStart() {
        super.onStart()
        val localView: View = view ?: return

        stationRecycler = localView.findViewById(R.id.station_recycler)
        val layoutManager = GridLayoutManager(activity, 2)
        stationRecycler.layoutManager = layoutManager

        moneyTextView = localView.findViewById(R.id.money)

        updateUI()
    }

    private fun updateUI() {
        moneyTextView.text = resources.getString(R.string.balance) + station.money.toString() + "$"
        stationRecycler.adapter = if (type == 0) {
            configureCaptionImageAdapter()
            captionImageAdapter
        } else {
            configureImagesCaptionCaptionAdapter()
            imagesCaptionCaptionAdapter
        }
    }

    private fun configureCaptionImageAdapter() {
        val names = mutableListOf<String>()
        val images = mutableListOf<Int>()
        for (type in Station.types) {
            names += parentContext.resources.getString(type.names)
            images += type.imageIds
        }
        captionImageAdapter = CaptionImageAdapter(names, images)

        captionImageAdapter.setListener { position: Int ->
            type = position + 1
            updateUI()
        }
    }

    private fun configureImagesCaptionCaptionAdapter() {
        val names = mutableListOf("Назад")
        val images = mutableListOf(R.drawable.weapon_1)
        val costs = mutableListOf(0)
        val states = mutableListOf(stateProduct.NONE)
        for (type in Station.types[type - 1].products) {
            names += parentContext.resources.getString(type.name)
            images += type.imageId
            costs += type.cost
            states += type.state
        }
        imagesCaptionCaptionAdapter = ImagesCaptionCaptionAdapter(names, costs, images, states)

        imagesCaptionCaptionAdapter.setListener { position: Int ->
            if (position == 0) {
                type = 0
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
                        moneyTextView.text = resources.getString(R.string.balance) + station.money.toString() + "$"
                        Station.types[localtype].products[position - 1].state = stateProduct.BUY
                    }
            }
            updateUI()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Station::class.java.simpleName, station)
        outState.putInt("Type", type)
        super.onSaveInstanceState(outState)
    }
}