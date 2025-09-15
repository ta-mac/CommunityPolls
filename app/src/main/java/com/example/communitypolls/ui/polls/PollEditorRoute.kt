package com.example.communitypolls.ui.polls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PollEditorRoute(
    createdByUid: String,
    onSaved: (String) -> Unit,
    onCancel: () -> Unit = {}
) {
    val vm: PollEditorViewModel = viewModel(factory = PollEditorVmFactory())
    val state by vm.state.collectAsState()

    LaunchedEffect(state.savedId) {
        state.savedId?.let { onSaved(it) }
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
        onSave = { vm.save(createdByUid) },
        onDismissError = {
            if (state.error != null) vm.clearError() else onCancel()
        }
    )
}
