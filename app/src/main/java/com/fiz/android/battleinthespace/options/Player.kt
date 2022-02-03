package com.fiz.android.battleinthespace.options

import com.fiz.android.battleinthespace.R
import java.util.*

data class Player(
    val id: UUID = UUID.randomUUID(),
    var name: String = "Player",
    var controllerPlayer: Boolean = true,

    var mission: Int = 0,

    var money: Int = 1000,

    var items: List<Item> = Item.createItems()
)

data class Types(val names: Int, val imageIds: Int) {
    companion object {
        fun createTypes(): List<Types> {
            return listOf(
                Types(R.string.weapon, R.drawable.station_weapon),
                Types(R.string.flight_speed, R.drawable.station_speed_fly),
                Types(R.string.turning_speed, R.drawable.station_speed_rotate),
                Types(R.string.rate_of_fire, R.drawable.station_speed_shoot),
                Types(R.string.body, R.drawable.station_speed_shoot),
            )
        }
    }
}

data class Product(val name: Int, val imageId: Int, val cost: Int, var state: StateProduct) :
    data

class Type(val names: Int, val imageIds: Int, val products: List<Product>) :

    data

class Item(
    val type: Int = 0,
    val name: Int = 0,
    val imageId: Int = 0,
    val cost: Int = 0,
    var state: StateProduct = StateProduct.NONE) {
    companion object {
        fun createItems(): List<Item> {
            return listOf(
                Item(R.string.weapon, R.string.bullet, R.drawable.weapon_2, 100, StateProduct.INSTALL),
                Item(R.string.weapon, R.string.double_bullet, R.drawable.weapon_3, 300, StateProduct.NONE),
                Item(R.string.weapon, R.string.missile, R.drawable.weapon_4, 500, StateProduct.NONE),
                Item(R.string.weapon, R.string.ball, R.drawable.weapon_5, 1000, StateProduct.NONE),
                Item(R.string.flight_speed, R.string.basic_engine, R.drawable.back, 100, StateProduct.INSTALL),
                Item(R.string.turning_speed, R.string.basic_steering_wheel, R.drawable.back, 100, StateProduct.INSTALL),
                Item(R.string.rate_of_fire, R.string.basic_charger, R.drawable.back, 100, StateProduct.INSTALL),
                Item(R.string.body, R.string.base_case, R.drawable.back, 100, StateProduct.INSTALL)
            )
        }

        fun createTypes(): List<Type> {
            return listOf(
                Type(
                    R.string.weapon, R.drawable.station_weapon, listOf(
                        Product(R.string.bullet, R.drawable.weapon_2, 100, StateProduct.INSTALL),
                        Product(R.string.double_bullet, R.drawable.weapon_3, 300, StateProduct.NONE),
                        Product(R.string.missile, R.drawable.weapon_4, 500, StateProduct.NONE),
                        Product(R.string.ball, R.drawable.weapon_5, 1000, StateProduct.NONE),
                    )
                ),

                Type(
                    R.string.flight_speed, R.drawable.station_speed_fly, listOf(
                        Product(R.string.basic_engine, R.drawable.back, 100, StateProduct.INSTALL)
                    )
                ),

                Type(
                    R.string.turning_speed, R.drawable.station_speed_rotate, listOf(
                        Product(R.string.basic_steering_wheel, R.drawable.back, 100, StateProduct.INSTALL)
                    )
                ),

                Type(
                    R.string.rate_of_fire, R.drawable.station_speed_shoot, listOf(
                        Product(R.string.basic_charger, R.drawable.back, 100, StateProduct.INSTALL)
                    )
                ),

                Type(
                    R.string.body, R.drawable.station_speed_shoot, listOf(
                        Product(R.string.base_case, R.drawable.back, 100, StateProduct.INSTALL)
                    )
                )
            )
        }
    }
}

enum class StateProduct {
    NONE, BUY, INSTALL,
}