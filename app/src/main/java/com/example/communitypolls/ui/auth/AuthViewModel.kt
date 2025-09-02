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

    init {
        // Keep UI in sync with Firebase auth state
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
            is AuthResult.Success -> _state.value = AuthUiState(user = res.user)
            is AuthResult.Error -> _state.value = _state.value.copy(loading = false, error = res.message)
        }
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val res = repo.signIn(email, password)) {
            is AuthResult.Success -> _state.value = AuthUiState(user = res.user)
            is AuthResult.Error -> _state.value = _state.value.copy(loading = false, error = res.message)
        }
    }

    fun signInGuest() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val res = repo.signInAnonymously()) {
            is AuthResult.Success -> _state.value = AuthUiState(user = res.user)
            is AuthResult.Error -> _state.value = _state.value.copy(loading = false, error = res.message)
        }
    }

    fun signOut() = viewModelScope.launch {
        repo.signOut()
        // repo.currentUser flow will emit null and update UI automatically
    }
}
