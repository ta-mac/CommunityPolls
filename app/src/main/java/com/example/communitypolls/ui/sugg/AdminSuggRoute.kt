package com.example.communitypolls.ui.sugg

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.ui.ServiceLocator

@Composable
fun AdminSuggRoute(onClose: () -> Unit) {
    val repo = ServiceLocator.suggestionRepository
    val vm: AdminSuggViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdminSuggViewModel(repo) as T
            }
        }
    )

    val state by vm.state.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    // Clear error if any
    if (state.error != null) {
        vm.clearError()
    }

    AdminSuggScreen(
        state = state,
        onAccept = { vm.setStatus(it, "accepted") },
        onDecline = { vm.setStatus(it, "declined") },
        onDelete = { pendingDeleteId = it },
        onBack = onClose
    )

    // Confirmation dialog for deletion
    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.delete(pendingDeleteId!!)
                        pendingDeleteId = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Suggestion") },
            text = { Text("Are you sure you want to delete this suggestion? This action cannot be undone.") }
        )
    }
}
