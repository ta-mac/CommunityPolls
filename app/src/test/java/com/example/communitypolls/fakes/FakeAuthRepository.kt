package com.example.communitypolls.fakes

import com.example.communitypolls.data.auth.AuthRepository
import com.example.communitypolls.data.auth.AuthResult
import com.example.communitypolls.model.AppUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class FakeAuthRepository : AuthRepository {
    private val users = ConcurrentHashMap<String, Pair<String, AppUser>>()
    private val id = AtomicInteger(1)
    private val _current = MutableStateFlow<AppUser?>(null)

    override val currentUser = _current.asStateFlow()

    override suspend fun signUp(email: String, password: String, displayName: String): AuthResult {
        if (users.containsKey(email)) return AuthResult.Error("Email already registered")
        val uid = "u${id.getAndIncrement()}"
        val user = AppUser(uid = uid, email = email, displayName = displayName, role = "user")
        users[email] = password to user
        _current.value = user
        return AuthResult.Success(user)
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        val entry = users[email] ?: return AuthResult.Error("No such user")
        if (entry.first != password) return AuthResult.Error("Wrong password")
        _current.value = entry.second
        return AuthResult.Success(entry.second)
    }

    override suspend fun signInAnonymously(): AuthResult {
        val uid = "guest_${id.getAndIncrement()}"
        val user = AppUser(uid = uid, displayName = "Guest", role = "guest", email = "")
        _current.value = user
        return AuthResult.Success(user)
    }

    override suspend fun signOut() { _current.value = null }

    override suspend fun refreshRole(): AppUser? = _current.value
}
