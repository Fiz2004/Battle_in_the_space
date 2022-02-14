package com.fiz.android.battleinthespace.base.data

import com.fiz.android.battleinthespace.R

class ItemsDatabase {
    companion object {
        fun getStartItems(): List<TypeItems> {
            return listOf(
                TypeItems(
                    0, R.string.weapon, R.drawable.station_weapon,
                    mutableListOf(
                        Item(0, R.string.bullet, R.drawable.weapon_2, 100, StateProduct.INSTALL),
                        Item(1, R.string.double_bullet, R.drawable.weapon_3, 300, StateProduct.NONE),
                        Item(2, R.string.missile, R.drawable.weapon_4, 500, StateProduct.NONE),
                        Item(3, R.string.ball, R.drawable.weapon_5, 1000, StateProduct.NONE),
                    )),
                TypeItems(
                    1, R.string.flight_speed, R.drawable.station_speed_fly,
                    mutableListOf(
                        Item(0, R.string.basic_engine, R.drawable.back, 100, StateProduct.INSTALL)
                    )),
                TypeItems(
                    2, R.string.turning_speed, R.drawable.station_speed_rotate,
                    mutableListOf(
                        Item(0, R.string.basic_steering_wheel, R.drawable.back, 100, StateProduct.INSTALL)
                    )),
                TypeItems(
                    3, R.string.rate_of_fire, R.drawable.station_speed_shoot,
                    mutableListOf(
                        Item(0, R.string.basic_charger, R.drawable.back, 100, StateProduct.INSTALL)
                    )),
                TypeItems(
                    4, R.string.body, R.drawable.station_speed_shoot,
                    mutableListOf(
                        Item(0, R.string.base_case, R.drawable.back, 100, StateProduct.INSTALL)
                    )),
            )
        }
    }
}