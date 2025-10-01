package com.example.communitypolls.ui.sugg

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.ui.ServiceLocator
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SuggestPollRoute(
    onSubmitted: (newId: String) -> Unit,
    onCancel: () -> Unit
) {
    val repo = ServiceLocator.suggestionRepository
    val user = FirebaseAuth.getInstance().currentUser
    val uid  = user?.uid.orEmpty()
    val initialName = user?.email ?: user?.displayName ?: uid.takeLast(6)

    val vm: SuggestPollViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SuggestPollViewModel(repo, uid, initialName) as T
            }
        }
    )
    val state by vm.state.collectAsState()

    LaunchedEffect(state.submittedId) {
        state.submittedId?.let { id ->
            onSubmitted(id)
            vm.consumeSubmitted()
        }
    }

    SuggestPollScreen(
        state = state,
        onTitleChange = vm::setTitle,
        onDescriptionChange = vm::setDescription,
        onNameOrEmailChange = vm::setNameOrEmail,
        onSubmit = vm::submit,
        onDismissError = {
            if (state.error != null) vm.clearError() else onCancel()
        }
    )
}
