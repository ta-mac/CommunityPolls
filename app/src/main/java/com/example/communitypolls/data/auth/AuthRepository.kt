package com.example.communitypolls.data.auth

import com.example.communitypolls.model.AppUser
import kotlinx.coroutines.flow.Flow

// Result wrapper for auth operations
sealed interface AuthResult {
    data class Success(val user: AppUser) : AuthResult
    data class Error(val message: String) : AuthResult
}

// Contract the UI will use (no Firebase types here)
interface AuthRepository {
    /** Emits the current user, or null when signed out. */
    val currentUser: Flow<AppUser?>

    suspend fun signUp(email: String, password: String, displayName: String): AuthResult
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signInAnonymously(): AuthResult
    suspend fun signOut()
    suspend fun refreshRole(): AppUser? // re-fetch role/profile if needed
}
