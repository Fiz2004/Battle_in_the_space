package com.fiz.android.battleinthespace.database

import androidx.room.TypeConverter
import com.fiz.android.battleinthespace.options.StateProduct

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
    fun toItems(items: String?): HashMap<Int, StateProduct> {
        val result = hashMapOf<Int, StateProduct>()
        val itemsSplit = items?.split(";")
        if (itemsSplit != null) {
            for (itemsNoSplit in itemsSplit) {
                val item = itemsNoSplit.split("=")
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
                result[first] = productState
            }
        }
        return result
    }

    @TypeConverter
    fun fromItems(items: HashMap<Int, StateProduct>?): String? {
        val keys = items?.keys
        val values = items?.values?.map {
            when (it) {
                StateProduct.NONE -> "0"
                StateProduct.BUY -> "1"
                StateProduct.INSTALL -> "2"
            }
        }
        var result = ""
        if (keys != null && values != null) {
            for ((index, key) in keys.withIndex()) {
                result += "$key=${values[index]}"
                result += ";"
            }
        }
        return result.substring(0, result.length - 1)
    }

}