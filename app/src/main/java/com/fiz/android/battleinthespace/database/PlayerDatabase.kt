package com.fiz.android.battleinthespace.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fiz.android.battleinthespace.data.Player

@Database(entities = [Player::class], version = 1, exportSchema = false)
@TypeConverters(PlayerTypeConverters::class)
abstract class PlayerDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDAO
}