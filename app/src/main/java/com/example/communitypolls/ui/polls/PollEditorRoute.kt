package com.example.communitypolls.ui.polls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PollEditorRoute(
    createdByUid: String,
    onSaved: (String) -> Unit,
    onCancel: () -> Unit = {}
) {
    val vm: PollEditorViewModel = viewModel(factory = PollEditorVmFactory())
    val state by vm.state.collectAsState()

    // Navigate out when a poll is successfully created
    LaunchedEffect(state.createdPollId) {
        state.createdPollId?.let { onSaved(it) }
    }

    PollEditorScreen(
        state = state,
        onTitleChange = vm::setTitle,
        onDescriptionChange = vm::setDescription,
        onOptionIdChange = vm::setOptionId,
        onOptionTextChange = vm::setOptionText,
        onAddOption = vm::addOption,
        onRemoveOption = vm::removeOption,
        onSave = { vm.save(createdByUid) },
        // Cancel vs error-dismiss share the same callback in the screen.
        // If there's an error showing, just clear it; otherwise treat as "Cancel".
        onDismissError = {
            if (state.error != null) vm.clearError() else onCancel()
        }
    )
}
