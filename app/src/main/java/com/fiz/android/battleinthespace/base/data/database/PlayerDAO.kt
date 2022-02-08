package com.fiz.android.battleinthespace.base.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fiz.android.battleinthespace.base.data.Player

@Dao
interface PlayerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlayer(player: Player)

    @Update
    fun update(player: Player)

    @Query("SELECT * from information_player_table WHERE id = :id")
    fun get(id: Int): LiveData<Player?>

    @Query("DELETE FROM information_player_table")
    fun clear()

    @Query("SELECT * FROM information_player_table")
    fun getAll(): LiveData<List<Player>>

    @Query("SELECT count(*) FROM information_player_table")
    fun getCount(): Int?
}


