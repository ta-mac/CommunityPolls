package com.example.communitypolls.ui.polls

import androidx.compose.runtime.*
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
    val saved by vm.saved.collectAsState()

    LaunchedEffect(saved) {
        if (saved) onSaved()
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
        onSelectClosePreset = vm::setCloseAfterHours,
        onSave = { vm.save() },
        onDismissError = {
            if (state.error != null) vm.clearError() else onCancel()
        },
        screenTitle = "Edit Poll",
        primaryButtonText = "Update"
    )
}
