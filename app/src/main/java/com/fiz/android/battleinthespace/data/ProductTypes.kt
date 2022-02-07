package com.fiz.android.battleinthespace.data

import com.fiz.android.battleinthespace.R

data class ProductTypes(val name: Int, val imageId: Int) {
    companion object {
        fun createTypes(): List<ProductTypes> {
            return listOf(
                ProductTypes(R.string.weapon, R.drawable.station_weapon),
                ProductTypes(R.string.flight_speed, R.drawable.station_speed_fly),
                ProductTypes(R.string.turning_speed, R.drawable.station_speed_rotate),
                ProductTypes(R.string.rate_of_fire, R.drawable.station_speed_shoot),
                ProductTypes(R.string.body, R.drawable.station_speed_shoot),
            )
        }
    }
}