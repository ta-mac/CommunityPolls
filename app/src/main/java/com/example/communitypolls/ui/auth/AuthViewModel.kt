package com.example.communitypolls.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.auth.AuthRepository
import com.example.communitypolls.data.auth.AuthResult
import com.example.communitypolls.model.AppUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

data class AuthUiState(
    val loading: Boolean = false,
    val user: AppUser? = null,
    val error: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _passwordStrength = MutableStateFlow("")
    val passwordStrength: StateFlow<String> = _passwordStrength

    fun validatePasswordInput(password: String) {
        val issues = mutableListOf<String>()
        if (password.length < 6) issues.add("At least 6 characters")
        if (!password.any { it.isUpperCase() }) issues.add("1 uppercase letter")
        if (!password.any { it.isDigit() }) issues.add("1 number")

        _passwordStrength.value = if (issues.isEmpty()) {
            "✅ Strong password"
        } else {
            "⚠ Missing: ${issues.joinToString(", ")}"
        }
    }

    init {
        viewModelScope.launch {
            repo.currentUser.collect { user ->
                _state.value = _state.value.copy(
                    user = user,
                    loading = false,
                    error = null
                )
            }
        }
    }

    fun signUp(email: String, password: String, name: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val res = repo.signUp(email, password, name)) {
            is AuthResult.Success -> {
                try {
                    repo.updateDisplayName(name)
                    res.user?.let {
                        _state.value = AuthUiState(user = it.copy(displayName = name))
                    } ?: run {
                        _state.value = AuthUiState(user = res.user)
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Failed to update name", loading = false)
                }
            }
            is AuthResult.Error -> _state.value =
                _state.value.copy(loading = false, error = res.message)
        }
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val res = repo.signIn(email, password)) {
            is AuthResult.Success -> _state.value = AuthUiState(user = res.user)
            is AuthResult.Error -> _state.value =
                _state.value.copy(loading = false, error = res.message)
        }
    }

    fun signInGuest() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val res = repo.signInAnonymously()) {
            is AuthResult.Success -> _state.value = AuthUiState(user = res.user)
            is AuthResult.Error -> _state.value =
                _state.value.copy(loading = false, error = res.message)
        }
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val res = repo.resetPassword(email)) {
            is AuthResult.Success -> _state.value = _state.value.copy(
                loading = false,
                error = "Reset email sent to $email"
            )
            is AuthResult.Error -> _state.value = _state.value.copy(
                loading = false,
                error = res.message
            )
        }
    }

    fun updateDisplayName(name: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        try {
            repo.updateDisplayName(name)
            _state.value = _state.value.copy(error = "Name updated successfully", loading = false)
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = e.message, loading = false)
        }
    }

    fun changePassword(newPassword: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        try {
            repo.changePassword(newPassword)
            _state.value = _state.value.copy(error = "Password updated successfully", loading = false)
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = e.message, loading = false)
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()

        // ✅ Immediately clear local user session so recomposition triggers
        _state.value = _state.value.copy(
            user = null,
            error = null,
            loading = false
        )
    }
}
