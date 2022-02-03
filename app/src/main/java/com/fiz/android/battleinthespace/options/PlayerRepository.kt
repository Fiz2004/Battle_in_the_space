package com.fiz.android.battleinthespace.options

import android.content.Context

class PlayerRepository private constructor(context: Context) {
    private val databasePlayers = mutableListOf(
        Player(name = "Player 1"),
        Player(name = "Player 2", controllerPlayer = false),
        Player(name = "Player 3", controllerPlayer = false),
        Player(name = "Player 4", controllerPlayer = false))

    private val playersDAO = databasePlayers

    private val countPlayer = databasePlayers.size

    fun getCountPlayer() = countPlayer

    fun getPlayers() = playersDAO

    fun getPlayer(number: Int) = playersDAO[number]

    companion object {
        private var INSTANCE: PlayerRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PlayerRepository(context)
            }
        }

        fun get(): PlayerRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}