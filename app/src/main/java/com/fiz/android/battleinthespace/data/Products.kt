package com.fiz.android.battleinthespace.data

import com.fiz.android.battleinthespace.R

class Products(
    val type: Int = 0,
    val name: Int = 0,
    val imageId: Int = 0,
    val cost: Int = 0,
) {
    companion object {
        fun createListProducts(): List<Products> {
            return listOf(
                Products(R.string.weapon, R.string.bullet, R.drawable.weapon_2, 100),
                Products(R.string.weapon, R.string.double_bullet, R.drawable.weapon_3, 300),
                Products(R.string.weapon, R.string.missile, R.drawable.weapon_4, 500),
                Products(R.string.weapon, R.string.ball, R.drawable.weapon_5, 1000),
                Products(R.string.flight_speed, R.string.basic_engine, R.drawable.back, 100),
                Products(R.string.turning_speed, R.string.basic_steering_wheel, R.drawable.back, 100),
                Products(R.string.rate_of_fire, R.string.basic_charger, R.drawable.back, 100),
                Products(R.string.body, R.string.base_case, R.drawable.back, 100)
            )
        }

        fun itemsDefault(): HashMap<Int, StateProduct> {
            val result = hashMapOf<Int, StateProduct>()
            for (item in Products.createListProducts()) {
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