package com.example.communitypolls.ui.sugg

<<<<<<< HEAD
=======
import androidx.compose.material3.*
>>>>>>> 71da6fb (Updated App Icon)
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.ui.ServiceLocator

@Composable
fun AdminSuggRoute(onClose: () -> Unit) {
    val repo = ServiceLocator.suggestionRepository
    val vm: AdminSuggViewModel = viewModel(
<<<<<<< HEAD
        factory = object: ViewModelProvider.Factory {
=======
        factory = object : ViewModelProvider.Factory {
>>>>>>> 71da6fb (Updated App Icon)
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdminSuggViewModel(repo) as T
            }
        }
    )
<<<<<<< HEAD
    val state by vm.state.collectAsState()

    if (state.error != null) {
        // Show your app’s dialog/snackbar; or keep it minimal:
        vm.clearError()
    }

=======

    val state by vm.state.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    // Handle error if any
    if (state.error != null) {
        vm.clearError()
    }

    // Main screen with admin suggestion actions
>>>>>>> 71da6fb (Updated App Icon)
    AdminSuggScreen(
        state = state,
        onAccept = { vm.setStatus(it, "accepted") },
        onDecline = { vm.setStatus(it, "declined") },
<<<<<<< HEAD
        onBack = onClose
    )
=======
        onDelete = { pendingDeleteId = it }, // Show confirm dialog instead of immediate delete
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
>>>>>>> 71da6fb (Updated App Icon)
}
