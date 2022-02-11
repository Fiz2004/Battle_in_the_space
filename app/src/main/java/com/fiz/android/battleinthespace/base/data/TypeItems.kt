package com.fiz.android.battleinthespace.base.data

import com.fiz.android.battleinthespace.R

data class TypeItems(val name: Int, val imageId: Int) {
    companion object {
        fun createTypes(): List<TypeItems> {
            return listOf(
                TypeItems(R.string.weapon, R.drawable.station_weapon),
                TypeItems(R.string.flight_speed, R.drawable.station_speed_fly),
                TypeItems(R.string.turning_speed, R.drawable.station_speed_rotate),
                TypeItems(R.string.rate_of_fire, R.drawable.station_speed_shoot),
                TypeItems(R.string.body, R.drawable.station_speed_shoot),
            )
        }
    }
}