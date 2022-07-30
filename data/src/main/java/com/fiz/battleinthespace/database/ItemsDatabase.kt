package com.fiz.battleinthespace.database

import com.fiz.battleinthespace.domain.models.Item
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.TypeItems

class ItemsDatabase {
    companion object {
        fun getStartItems(): List<TypeItems> {
            return listOf(
                TypeItems(
                    "0", R.string.weapon, R.drawable.station_weapon,
                    mutableListOf(
                        Item(
                            "Пуля",
                            R.string.bullet,
                            R.drawable.weapon_2,
                            100,
                            StateProduct.INSTALL
                        ),
                        Item(
                            "Двойная пуля",
                            R.string.double_bullet,
                            R.drawable.weapon_3,
                            300,
                            StateProduct.NONE
                        ),
                        Item(
                            "Ракета",
                            R.string.missile,
                            R.drawable.weapon_4,
                            500,
                            StateProduct.NONE
                        ),
                        Item(
                            "Шар",
                            R.string.ball,
                            R.drawable.weapon_5,
                            1000,
                            StateProduct.NONE
                        ),
                    )
                ),
                TypeItems(
                    "Скорость полета", R.string.flight_speed, R.drawable.station_speed_fly,
                    mutableListOf(
                        Item(
                            "0",
                            R.string.basic_engine,
                            R.drawable.back,
                            100,
                            StateProduct.INSTALL
                        )
                    )
                ),
                TypeItems(
                    "Скорость поворота", R.string.turning_speed, R.drawable.station_speed_rotate,
                    mutableListOf(
                        Item(
                            "0",
                            R.string.basic_steering_wheel,
                            R.drawable.back,
                            100,
                            StateProduct.INSTALL
                        )
                    )
                ),
                TypeItems(
                    "Скорость стрельбы", R.string.rate_of_fire, R.drawable.station_speed_shoot,
                    mutableListOf(
                        Item(
                            "0",
                            R.string.basic_charger,
                            R.drawable.back,
                            100,
                            StateProduct.INSTALL
                        )
                    )
                ),
                TypeItems(
                    "Корпус", R.string.body, R.drawable.station_speed_shoot,
                    mutableListOf(
                        Item(
                            "0",
                            R.string.base_case,
                            R.drawable.back,
                            100,
                            StateProduct.INSTALL
                        )
                    )
                ),
            )
        }
    }
}