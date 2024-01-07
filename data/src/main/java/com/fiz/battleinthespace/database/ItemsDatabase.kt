package com.fiz.battleinthespace.database

import com.fiz.battleinthespace.domain.models.CategoryItem
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.SubItem

class ItemsDatabase {
    companion object {
        fun getStartItems(): List<CategoryItem> {
            return listOf(
                CategoryItem(
                    "0", R.string.weapon, R.drawable.station_weapon,
                    mutableListOf(
                        SubItem.Item(
                            "Пуля",
                            R.string.bullet,
                            R.drawable.weapon_2,
                            100,
                            StateProduct.INSTALL
                        ),
                        SubItem.Item(
                            "Двойная пуля",
                            R.string.double_bullet,
                            R.drawable.weapon_3,
                            300,
                            StateProduct.NONE
                        ),
                        SubItem.Item(
                            "Ракета",
                            R.string.missile,
                            R.drawable.weapon_4,
                            500,
                            StateProduct.NONE
                        ),
                        SubItem.Item(
                            "Шар",
                            R.string.ball,
                            R.drawable.weapon_5,
                            1000,
                            StateProduct.NONE
                        ),
                    )
                ),
                CategoryItem(
                    "Скорость полета", R.string.flight_speed, R.drawable.station_speed_fly,
                    mutableListOf(
                        SubItem.Item(
                            "0",
                            R.string.basic_engine,
                            R.drawable.back,
                            100,
                            StateProduct.INSTALL
                        )
                    )
                ),
                CategoryItem(
                    "Скорость поворота", R.string.turning_speed, R.drawable.station_speed_rotate,
                    mutableListOf(
                        SubItem.Item(
                            "0",
                            R.string.basic_steering_wheel,
                            R.drawable.back,
                            100,
                            StateProduct.INSTALL
                        )
                    )
                ),
                CategoryItem(
                    "Скорость стрельбы", R.string.rate_of_fire, R.drawable.station_speed_shoot,
                    mutableListOf(
                        SubItem.Item(
                            "0",
                            R.string.basic_charger,
                            R.drawable.back,
                            100,
                            StateProduct.INSTALL
                        )
                    )
                ),
                CategoryItem(
                    "Корпус", R.string.body, R.drawable.station_speed_shoot,
                    mutableListOf(
                        SubItem.Item(
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