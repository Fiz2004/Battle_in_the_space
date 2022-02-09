package com.fiz.android.battleinthespace.base.data

import com.fiz.android.battleinthespace.R

data class ItemTypesDefault(val name: Int, val imageId: Int) {
    companion object {
        fun createTypes(): List<ItemTypesDefault> {
            return listOf(
                ItemTypesDefault(R.string.weapon, R.drawable.station_weapon),
                ItemTypesDefault(R.string.flight_speed, R.drawable.station_speed_fly),
                ItemTypesDefault(R.string.turning_speed, R.drawable.station_speed_rotate),
                ItemTypesDefault(R.string.rate_of_fire, R.drawable.station_speed_shoot),
                ItemTypesDefault(R.string.body, R.drawable.station_speed_shoot),
            )
        }
    }
}