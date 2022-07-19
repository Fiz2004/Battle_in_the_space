package com.fiz.battleinthespace.database.firebase

import android.util.Log
import com.fiz.battleinthespace.domain.models.Player
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DBManager {
    val db = Firebase.firestore
    val auth = Firebase.auth

    fun add(player: Player) {
        db.collection("players")
            .add(player)
            .addOnSuccessListener { documentReference ->
                Log.d("AAA", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("AAA", "Error adding document", e)
            }
    }
}