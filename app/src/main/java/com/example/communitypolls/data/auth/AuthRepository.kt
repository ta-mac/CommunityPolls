package com.example.communitypolls.data.auth

import com.example.communitypolls.model.AppUser
import kotlinx.coroutines.flow.Flow

sealed interface AuthResult {
    data class Success(val user: AppUser) : AuthResult
    data class Error(val message: String) : AuthResult
}

interface AuthRepository {
    val currentUser: Flow<AppUser?>

    suspend fun signUp(email: String, password: String, displayName: String): AuthResult
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signInAnonymously(): AuthResult
    suspend fun signOut()
    suspend fun refreshRole(): AppUser?
    suspend fun resetPassword(email: String): AuthResult //New
    suspend fun updateDisplayName(name: String)//New
    suspend fun changePassword(newPassword: String)//New

}
