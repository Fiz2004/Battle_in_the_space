package com.fiz.battleinthespace.database.data_source.local

internal interface SharedPrefPlayerStorage {

    suspend fun getCountPlayers(): Int

    suspend fun saveCountPlayers(count: Int)

    suspend fun getUuid(): String?

    suspend fun saveGUuid(uuid: String)

    suspend fun deleteGUuid()
}