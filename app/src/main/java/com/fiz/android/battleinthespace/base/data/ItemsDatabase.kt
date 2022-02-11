package com.fiz.android.battleinthespace.base.data

import com.fiz.android.battleinthespace.R

class ItemsDatabase(
    val type: Int = 0,
    val name: Int = 0,
    val imageId: Int = 0,
    val cost: Int = 0,
) {
    companion object {
        fun createListItems(): List<ItemsDatabase> {
            return listOf(
                ItemsDatabase(R.string.weapon, R.string.bullet, R.drawable.weapon_2, 100),
                ItemsDatabase(R.string.weapon, R.string.double_bullet, R.drawable.weapon_3, 300),
                ItemsDatabase(R.string.weapon, R.string.missile, R.drawable.weapon_4, 500),
                ItemsDatabase(R.string.weapon, R.string.ball, R.drawable.weapon_5, 1000),
                ItemsDatabase(R.string.flight_speed, R.string.basic_engine, R.drawable.back, 100),
                ItemsDatabase(R.string.turning_speed, R.string.basic_steering_wheel, R.drawable.back, 100),
                ItemsDatabase(R.string.rate_of_fire, R.string.basic_charger, R.drawable.back, 100),
                ItemsDatabase(R.string.body, R.string.base_case, R.drawable.back, 100)
            )
        }

        fun itemsDefault(): HashMap<Int, StateProduct> {
            val result = hashMapOf<Int, StateProduct>()
            for (item in createListItems()) {
                result[item.name] = StateProduct.NONE
            }
            result[R.string.bullet] = StateProduct.INSTALL
            result[R.string.basic_engine] = StateProduct.INSTALL
            result[R.string.basic_steering_wheel] = StateProduct.INSTALL
            result[R.string.basic_charger] = StateProduct.INSTALL
            result[R.string.base_case] = StateProduct.INSTALL
            return result

        }
    }
}