package com.fiz.battleinthespace.domain.repositories

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun createAccount(email: String, password: String): String

    suspend fun signInWithEmail(email: String, password: String): String

    suspend fun signInFirebaseWithGoogle(result: String): String

    suspend fun resetPassword(email: String): String

    fun signOut()

    fun getFlowEmail(): Flow<String?>

    fun getAuthUuid(): String?
}