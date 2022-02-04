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
    fun toItems(items: String?): HashMap<Int, StateProduct>? {
        val itemSplit = items?.split("=")
        val first = (itemSplit?.get(0))?.toInt() ?: 0
        val second = itemSplit?.get(1) ?: 0
        val productState = when (second) {
            "0" -> StateProduct.NONE
            "1" -> StateProduct.BUY
            "2" -> StateProduct.INSTALL
            else -> {
                StateProduct.NONE
            }
        }
        return hashMapOf(Pair<Int, StateProduct>(first, productState))
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
        return "$keys=$values"
    }

}