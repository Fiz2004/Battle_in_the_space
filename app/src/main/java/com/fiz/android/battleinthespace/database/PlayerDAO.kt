package com.fiz.android.battleinthespace.database

import androidx.room.*
import com.fiz.android.battleinthespace.options.Player

@Dao
interface PlayerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlayer(player: Player)

    @Update
    fun update(player: Player)

    @Query("SELECT * from information_player_table WHERE id = (:id)")
    fun get(id: Int): Player?

    @Query("DELETE FROM information_player_table")
    fun clear()

    @Query("SELECT * FROM information_player_table")
    fun getAll(): List<Player>

    @Query("SELECT count(*) FROM information_player_table")
    fun getCount(): Int?
}


