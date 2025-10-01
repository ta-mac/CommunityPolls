package com.example.communitypolls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communitypolls.ui.ServiceLocator
import com.example.communitypolls.ui.auth.AuthViewModel

class AuthVmFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repo = ServiceLocator.authRepository
            return AuthViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
