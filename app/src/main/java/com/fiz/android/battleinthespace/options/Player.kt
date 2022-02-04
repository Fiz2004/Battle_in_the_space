package com.fiz.android.battleinthespace.options

import com.fiz.android.battleinthespace.R
import java.util.*

data class Player(
    val id: UUID = UUID.randomUUID(),
    var name: String = "Player",
    var controllerPlayer: Boolean = true,

    var mission: Int = 0,

    var money: Int = 1000,

    var items: HashMap<Int, StateProduct> = hashMapOf()
) {
    init {
        for (item in Products.createListProducts()) {
            items[item.name] = StateProduct.NONE
        }
        items[R.string.bullet] = StateProduct.INSTALL
        items[R.string.basic_engine] = StateProduct.INSTALL
        items[R.string.basic_steering_wheel] = StateProduct.INSTALL
        items[R.string.basic_charger] = StateProduct.INSTALL
        items[R.string.base_case] = StateProduct.INSTALL
    }
}

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

data class Product(val name: Int, val imageId: Int, val cost: Int, var state: StateProduct) {
    companion object {
        fun getListProduct(type: Int, items: MutableMap<Int, StateProduct>): List<Product> {
            val result = mutableListOf<Product>()
            result.add(Product(0, 0, 0, StateProduct.NONE))
            val allItems = Products.createListProducts()
            val itemsType = allItems.filter { it.type == type }
            for (item in itemsType) {
                val state = items[item.name] ?: StateProduct.NONE
                result.add(Product(item.name, item.imageId, item.cost, state))
            }
            return result
        }
    }
}

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
    }
}

enum class StateProduct {
    NONE, BUY, INSTALL,
}