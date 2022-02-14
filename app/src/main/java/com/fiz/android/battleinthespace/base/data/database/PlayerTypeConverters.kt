package com.fiz.android.battleinthespace.base.data.database

import androidx.room.TypeConverter
import com.fiz.android.battleinthespace.base.data.ItemsDatabase
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.base.data.TypeItems

class PlayerTypeConverters {
    //    @TypeConverter
    //    fun toUUID(uuid: String?): UUID? {
    //        return UUID.fromString(uuid)
    //    }
    //
    //    @TypeConverter
    //    fun fromUUID(uuid: UUID?): String? {
    //        return uuid?.toString()
    //    }

    @TypeConverter
    fun toItems(items: String?): List<TypeItems> {
        val result = ItemsDatabase.getStartItems()
        val itemsSplit = items?.split(";")
        if (itemsSplit != null) {
            for ((indexType, itemsNoSplit) in itemsSplit.withIndex()) {
                val itemsWithState = itemsNoSplit.split(".")
                for (itemWithState in itemsWithState) {
                    val item = itemWithState.split("=")
                    val first = (item[0]).toInt()
                    val second = item[1]
                    val productState = when (second) {
                        "0" -> StateProduct.NONE
                        "1" -> StateProduct.BUY
                        "2" -> StateProduct.INSTALL
                        else -> {
                            StateProduct.NONE
                        }
                    }
                    result[indexType].items[first].state = productState
                }
            }
        }
        return result
    }

    @TypeConverter
    fun fromItems(items: List<TypeItems>): String? {
        var result = ""
        for (typeItems in items) {
            for (item in typeItems.items) {
                val state = when (item.state) {
                    StateProduct.NONE -> "0"
                    StateProduct.BUY -> "1"
                    StateProduct.INSTALL -> "2"
                }
                result += "${item.id}=${state}."
            }
            result = result.substring(0, result.length - 1)
            result += ";"
        }
        return result.substring(0, result.length - 1)
    }

}