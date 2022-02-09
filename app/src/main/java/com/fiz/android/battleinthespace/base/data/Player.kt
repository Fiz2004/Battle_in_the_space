package com.fiz.android.battleinthespace.base.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "information_player_table")
data class Player(
    @PrimaryKey
    val id: Int = 0,
    @NonNull @ColumnInfo(name = "name")
    var name: String = "Player",
    @NonNull @ColumnInfo(name = "controllerPlayer")
    var controllerPlayer: Boolean = true,
    @NonNull @ColumnInfo(name = "mission")
    var mission: Int = 0,
    @NonNull @ColumnInfo(name = "money")
    var money: Int = 1000,
    @NonNull @ColumnInfo(name = "items")
    var items: HashMap<Int, StateProduct> = ItemDefault.itemsDefault()
)
