package com.example.communitypolls.ui.sugg

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.ui.ServiceLocator

@Composable
fun AdminSuggRoute(onClose: () -> Unit) {
    val repo = ServiceLocator.suggestionRepository
    val vm: AdminSuggViewModel = viewModel(
        factory = object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdminSuggViewModel(repo) as T
            }
        }
    )
    val state by vm.state.collectAsState()

    if (state.error != null) {
        // Show your app’s dialog/snackbar; or keep it minimal:
        vm.clearError()
    }

    AdminSuggScreen(
        state = state,
        onAccept = { vm.setStatus(it, "accepted") },
        onDecline = { vm.setStatus(it, "declined") },
        onBack = onClose
    )
}
