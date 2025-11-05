package com.example.communitypolls.fakes

import com.example.communitypolls.data.auth.AuthRepository
import com.example.communitypolls.data.auth.AuthResult
import com.example.communitypolls.model.AppUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthRepository : AuthRepository {

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    override val currentUser: Flow<AppUser?> = _currentUser

    private var shouldFail = false

    fun setShouldFail(value: Boolean) {
        shouldFail = value
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        return if (shouldFail) {
            AuthResult.Error("Sign-in failed")
        } else {
            val fakeUser = AppUser(uid = "123", email = email, displayName = "Fake User", role = "user")
            _currentUser.value = fakeUser
            AuthResult.Success(fakeUser)
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): AuthResult {
        return if (shouldFail) {
            AuthResult.Error("Sign-up failed")
        } else {
            val fakeUser = AppUser(uid = "456", email = email, displayName = displayName, role = "user")
            _currentUser.value = fakeUser
            AuthResult.Success(fakeUser)
        }
    }

    override suspend fun signInAnonymously(): AuthResult {
        return if (shouldFail) {
            AuthResult.Error("Anonymous sign-in failed")
        } else {
            val fakeUser = AppUser(uid = "anon123", email = "", displayName = "Guest", role = "guest")
            _currentUser.value = fakeUser
            AuthResult.Success(fakeUser)
        }
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override suspend fun refreshRole(): AppUser? {
        return _currentUser.value
    }

    override suspend fun resetPassword(email: String): AuthResult {
        return if (shouldFail) {
            AuthResult.Error("Reset password failed")
        } else {
            AuthResult.Success(AppUser(uid = "reset", email = email, displayName = "Reset User", role = "user"))
        }
    }

    override suspend fun updateDisplayName(name: String) {
        val user = _currentUser.value
        if (user != null) {
            _currentUser.value = user.copy(displayName = name)
        }
    }

    override suspend fun changePassword(newPassword: String) {
        if (shouldFail) throw Exception("Change password failed")
        // Simulate success silently
    }
}
