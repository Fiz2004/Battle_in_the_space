package com.fiz.android.battleinthespace.options

import android.content.Context
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.interfaces.stateProduct
import java.io.Serializable

data class Product(val name: String, val imageId: Int, val cost: Int, var state: stateProduct)
data class Type(val names: String, val imageIds: Int, val products: List<Product>)

class Station(context: Context) : Serializable {

    var money = 1000

    companion object {
        val types = listOf<Type>(
            Type(
                "Оружие", R.drawable.station_weapon, listOf(
                    Product("Пуля", R.drawable.weapon_2, 100, stateProduct.INSTALL),
                    Product("Двойная пуля", R.drawable.weapon_3, 300, stateProduct.NONE),
                    Product("Ракета", R.drawable.weapon_4, 500, stateProduct.NONE),
                    Product("Шар", R.drawable.weapon_5, 1000, stateProduct.NONE),
                )),

            Type(
                "Скорость полета", R.drawable.station_speed_fly, listOf(
                    Product("Пуля", R.drawable.weapon_1, 100, stateProduct.INSTALL))),

            Type(
                "Скорость поворота", R.drawable.station_speed_rotate, listOf(
                    Product("Пуля", R.drawable.weapon_1, 100, stateProduct.INSTALL))),

            Type(
                "Скорость стрельбы", R.drawable.station_speed_shoot, listOf(
                    Product("Пуля", R.drawable.weapon_1, 100, stateProduct.INSTALL))),

            Type(
                "Вес", R.drawable.station_speed_shoot, listOf(
                    Product("Пуля", R.drawable.weapon_1, 100, stateProduct.INSTALL))))
    }

    val weapon: MutableList<Boolean> = mutableListOf(true, false, false, false)
    val speed: MutableList<Boolean> = mutableListOf(true)
    val speedRotate: MutableList<Boolean> = mutableListOf(true)
    val speedWeapon: MutableList<Boolean> = mutableListOf(true)
    val weight: MutableList<Boolean> = mutableListOf(true)
}