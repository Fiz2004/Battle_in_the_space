package com.fiz.battleinthespace.database.firebase

import com.fiz.battleinthespace.database.Player
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DBManager {
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun add(player: Player) {
        db.child(player.id.toString()).child(auth.uid.toString()).child("info").setValue(player)
    }
}