package com.example.communitypolls.ui.polls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.ui.ServiceLocator

@Composable
fun PollEditRoute(
    pollId: String,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val repo = ServiceLocator.pollRepository
    val vm: PollEditViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PollEditViewModel(repo, pollId) as T
            }
        }
    )
    val state by vm.state.collectAsState()

    // When update succeeds we set savedId, then pop back
    LaunchedEffect(state.savedId) {
        if (state.savedId != null) onSaved()
    }

    PollEditorScreen(
        state = state,
        onTitleChange = vm::setTitle,
        onDescriptionChange = vm::setDescription,
        onOptionIdChange = vm::setOptionId,
        onOptionTextChange = vm::setOptionText,
        onAddOption = vm::addOption,
        onRemoveOption = vm::removeOption,
        onToggleActive = vm::setActive,
        onSelectClosePreset = vm::setCloseAfterHours, // (Int?) -> Unit
        onSave = { vm.save() },
        onDismissError = {
            if (state.error != null) vm.clearError() else onCancel()
        }
    )
}
