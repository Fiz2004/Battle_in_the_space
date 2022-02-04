package com.fiz.android.battleinthespace.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fiz.android.battleinthespace.options.Player

@Dao
interface PlayerDAO {
    @Insert
    fun addPlayer(player: Player)

    @Update
    fun update(player: Player)

    @Query("SELECT * from information_player_table WHERE id = (:id)")
    fun get(id: Int): LiveData<Player>?

    @Query("DELETE FROM information_player_table")
    fun clear()

    @Query("SELECT * FROM information_player_table ORDER BY id DESC")
    fun getAll(): LiveData<List<Player>>

    @Query("SELECT count(*) FROM information_player_table")
    fun getCount(): LiveData<Int>
}


