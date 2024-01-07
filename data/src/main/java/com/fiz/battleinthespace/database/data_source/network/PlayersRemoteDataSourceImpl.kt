package com.fiz.battleinthespace.database.data_source.network

import android.util.Log
import com.fiz.battleinthespace.database.R
import com.fiz.battleinthespace.database.data_source.network.dto.PlayersDto
import com.fiz.battleinthespace.domain.models.CategoryItem
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.SubItem
import com.fiz.battleinthespace.exceptions.RemoteException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class PlayersRemoteDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : PlayersRemoteDataSource {

    override suspend fun save(uuid: String, players: List<Player>) {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val playersDTO = PlayersDto.from(players)
                db.collection(PLAYERS).document(uuid)
                    .set(playersDTO)
                    .addOnSuccessListener { _ ->
                        Log.d("AAA", "Save")
                        continuation.resumeWith(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        Log.w("AAA", "Error", e)
                        continuation.resumeWithException(e)
                    }

                continuation.invokeOnCancellation { continuation.cancel() }
            }
        }
    }

    override suspend fun setItemState(uuid: String, item: SubItem.Item, value: StateProduct) {
        return withContext(Dispatchers.IO) {
            val players = getPlayers(uuid).toMutableList()
            suspendCoroutine { continuation ->
                val categoryIndex =
                    players[0].categoryItems.indexOfFirst { it.subItems.contains(item) }
                val itemIndex =
                    players[0].categoryItems[categoryIndex].subItems.indexOfFirst { it.id == item.id }
                players[0].categoryItems[categoryIndex].subItems[itemIndex].state = value
                val playersDTO = PlayersDto.from(players)
                db.collection(PLAYERS).document(uuid)
                    .set(playersDTO)
                    .addOnSuccessListener { _ ->
                        Log.d("AAA", "Save")
                        continuation.resumeWith(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        Log.w("AAA", "Error", e)
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

    override suspend fun getItems(uuid: String, categoryId: String): List<SubItem> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                db.collection(PLAYERS).document(uuid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val result = document.toObject(PlayersDto::class.java)
                            if (result != null)
                                continuation.resumeWith(
                                    Result.success(
                                        result.toPlayers()[0].categoryItems.find { it.id == categoryId }?.subItems
                                            ?: emptyList()
                                    )
                                )
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
        }
    }

    override suspend fun getCategoryItems(uuid: String): List<CategoryItem> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                db.collection(PLAYERS).document(uuid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val result = document.toObject(PlayersDto::class.java)
                            if (result != null)
                                continuation.resumeWith(
                                    Result.success(
                                        result.toPlayers()[0].categoryItems
                                    )
                                )
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
        }
    }

    override suspend fun buyItemState(uuid: String, item: SubItem.Item) {
        return withContext(Dispatchers.IO) {
            val players = getPlayers(uuid).toMutableList()
            if (players[0].money < item.cost) return@withContext
            suspendCoroutine { continuation ->
                val categoryIndex =
                    players[0].categoryItems.indexOfFirst { it.subItems.contains(item) }
                val itemIndex =
                    players[0].categoryItems[categoryIndex].subItems.indexOfFirst { it.id == item.id }
                players[0].categoryItems[categoryIndex].subItems[itemIndex].state = StateProduct.BUY
                players[0].money -= item.cost
                val playersDTO = PlayersDto.from(players)
                db.collection(PLAYERS).document(uuid)
                    .set(playersDTO)
                    .addOnSuccessListener { _ ->
                        Log.d("AAA", "Save")
                        continuation.resumeWith(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        Log.w("AAA", "Error", e)
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

    override suspend fun setName(uuid: String, index: Int, newName: String) {
        return withContext(Dispatchers.IO) {
            val players = getPlayers(uuid).toMutableList()
            if (players[index].name == newName) return@withContext
            suspendCoroutine { continuation ->
                players[index].name = newName
                val playersDTO = PlayersDto.from(players)
                db.collection(PLAYERS).document(uuid)
                    .set(playersDTO)
                    .addOnSuccessListener { _ ->
                        Log.d("AAA", "Save")
                        continuation.resumeWith(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        Log.w("AAA", "Error", e)
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

    override suspend fun setController(uuid: String, index: Int, checked: Boolean) {
        return withContext(Dispatchers.IO) {
            val players = getPlayers(uuid).toMutableList()
            if (players[index].controllerPlayer == checked) return@withContext
            suspendCoroutine { continuation ->
                players[index].controllerPlayer = checked
                val playersDTO = PlayersDto.from(players)
                db.collection(PLAYERS).document(uuid)
                    .set(playersDTO)
                    .addOnSuccessListener { _ ->
                        Log.d("AAA", "Save")
                        continuation.resumeWith(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        Log.w("AAA", "Error", e)
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

    override suspend fun saveMission(uuid: String, value: Int) {
        return withContext(Dispatchers.IO) {
            val players = getPlayers(uuid).toMutableList()
            if (players[0].mission == value) return@withContext
            suspendCoroutine { continuation ->
                players[0].mission = value
                val playersDTO = PlayersDto.from(players)
                db.collection(PLAYERS).document(uuid)
                    .set(playersDTO)
                    .addOnSuccessListener { _ ->
                        Log.d("AAA", "Save")
                        continuation.resumeWith(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        Log.w("AAA", "Error", e)
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

    override fun getFlowPlayers(uuid: String): Flow<Result<List<Player>>> {
        return callbackFlow {
            val query = db.collection(PLAYERS).document(uuid)
            val registration = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Result.failure(e))
                    return@addSnapshotListener
                }
                val result = snapshot?.toObject(PlayersDto::class.java)

                if (snapshot != null && snapshot.exists() && result != null) {
                    trySend(Result.success(result.toPlayers()))
                } else {
                    trySend(Result.failure(Exception("Error Load")))
                }
            }
            awaitClose {
                registration.remove()
            }
        }
    }

    override suspend fun getPlayers(uuid: String): List<Player> = suspendCoroutine { continuation ->
        db.collection(PLAYERS).document(uuid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject(PlayersDto::class.java)
                    if (result != null)
                        continuation.resumeWith(Result.success(result.toPlayers()))
                    else
                        continuation.resumeWithException(RemoteException(R.string.errorLoadPlayersNoSavedPlayers))
                } else {
                    continuation.resumeWithException(RemoteException(R.string.errorLoadPlayersNoDocument))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    private companion object {
        const val PLAYERS = "players"
    }
}

