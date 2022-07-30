package com.fiz.battleinthespace.database.data_source.network

import android.util.Log
import com.fiz.battleinthespace.common.Resource
import com.fiz.battleinthespace.database.data_source.network.dto.PlayersDto
import com.fiz.battleinthespace.domain.models.Player
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PlayersRemoteDataSource @Inject constructor() {
    private val db = Firebase.firestore

    fun save(uuid: String, players: List<Player>) {
        val playersDTO = PlayersDto.from(players)
        db.collection(PLAYERS).document(uuid)
            .set(playersDTO)
            .addOnSuccessListener { task ->
                Log.d("AAA", "Save")
            }
            .addOnFailureListener { e ->
                Log.w("AAA", "Error", e)
            }
    }

    fun getFlowPlayers(uuid: String): Flow<Resource<List<Player>>> {
        return callbackFlow {
            val query = db.collection(PLAYERS).document(uuid)
            val registration = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Resource.Error(message = "Error Load"))
                    return@addSnapshotListener
                }
                val result = snapshot?.toObject(PlayersDto::class.java)

                if (snapshot != null && snapshot.exists() && result != null) {
                    trySend(Resource.Success(result.toPlayers()))
                } else {
                    trySend(Resource.Error(message = "Error Load"))
                }
            }
            awaitClose {
                registration.remove()
            }
        }
    }

    suspend fun getPlayers(uuid: String): List<Player> = suspendCoroutine { continuation ->
        db.collection(PLAYERS).document(uuid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject(PlayersDto::class.java)
                    if (result != null)
                        continuation.resumeWith(Result.success(result.toPlayers()))
                    else
                        continuation.resumeWithException(Exception("No Players"))
                } else {
                    continuation.resumeWithException(Exception("База данных пуста. Нет игрока с таким id"))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    companion object {
        const val PLAYERS = "players"
    }
}

