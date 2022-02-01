package com.fiz.android.battleinthespace.options

import android.content.Context
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.interfaces.stateProduct
import java.io.Serializable

data class Product(val name: Int, val imageId: Int, val cost: Int, var state: stateProduct)
data class Type(val names: Int, val imageIds: Int, val products: List<Product>)

class Station(context: Context) : Serializable {

    var money = 1000

    companion object {
        val types = listOf<Type>(
            Type(
                R.string.weapon, R.drawable.station_weapon, listOf(
                    Product(R.string.bullet, R.drawable.weapon_2, 100, stateProduct.INSTALL),
                    Product(R.string.double_bullet, R.drawable.weapon_3, 300, stateProduct.NONE),
                    Product(R.string.missile, R.drawable.weapon_4, 500, stateProduct.NONE),
                    Product(R.string.ball, R.drawable.weapon_5, 1000, stateProduct.NONE),
                )
            ),

            Type(
                R.string.flight_speed, R.drawable.station_speed_fly, listOf(
                    Product(R.string.basic_engine, R.drawable.weapon_1, 100, stateProduct.INSTALL)
                )
            ),

            Type(
                R.string.turning_speed, R.drawable.station_speed_rotate, listOf(
                    Product(R.string.basic_steering_wheel, R.drawable.weapon_1, 100, stateProduct.INSTALL)
                )
            ),

            Type(
                R.string.rate_of_fire, R.drawable.station_speed_shoot, listOf(
                    Product(R.string.basic_charger, R.drawable.weapon_1, 100, stateProduct.INSTALL)
                )
            ),

            Type(
                R.string.body, R.drawable.station_speed_shoot, listOf(
                    Product(R.string.base_case, R.drawable.weapon_1, 100, stateProduct.INSTALL)
                )
            )
        )
    }
}