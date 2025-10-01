package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.poll.OpResult
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.model.PollOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PollEditViewModel(
    private val repo: PollRepository,
    private val pollId: String
) : ViewModel() {

    private val _state = MutableStateFlow(PollEditorState())
    val state: StateFlow<PollEditorState> = _state.asStateFlow()

    init {
        // Load existing poll into the editor state
        viewModelScope.launch {
            repo.observePoll(pollId).collectLatest { poll ->
                poll ?: return@collectLatest
                _state.value = _state.value.copy(
                    title = poll.title,
                    description = poll.description,
                    options = poll.options.map { PollEditorOption(it.id, it.text) },
                    isActive = poll.isActive,
                    // leave closeAfterHours null unless user chooses a new preset
                    closeAfterHours = null,
                    error = null
                )
            }
        }
    }

    fun setTitle(value: String) { _state.value = _state.value.copy(title = value, error = null) }
    fun setDescription(value: String) { _state.value = _state.value.copy(description = value, error = null) }
    fun setOptionId(index: Int, value: String) = updateOption(index) { it.copy(id = value) }
    fun setOptionText(index: Int, value: String) = updateOption(index) { it.copy(text = value) }
    fun addOption() { _state.value = _state.value.copy(options = _state.value.options + PollEditorOption()) }
    fun removeOption(index: Int) {
        val list = _state.value.options.toMutableList()
        if (index in list.indices && list.size > 2) {
            list.removeAt(index)
            _state.value = _state.value.copy(options = list)
        }
    }
    fun setActive(value: Boolean) { _state.value = _state.value.copy(isActive = value) }
    fun setCloseAfterHours(value: Int?) { _state.value = _state.value.copy(closeAfterHours = value) }
    fun clearError() { _state.value = _state.value.copy(error = null) }

    fun save() {
        val s = _state.value
        if (s.loading) return

        val normalized = s.options.map { it.copy(id = it.id.trim(), text = it.text.trim()) }
        if (s.title.isBlank()) { _state.value = s.copy(error = "Title is required"); return }
        if (normalized.size < 2) { _state.value = s.copy(error = "Add at least two options"); return }
        if (normalized.any { it.id.isBlank() || it.text.isBlank() }) {
            _state.value = s.copy(error = "Each option needs both an ID and Text"); return
        }

        val closesAtMillis = s.closeAfterHours?.let { hours ->
            System.currentTimeMillis() + hours * 60L * 60L * 1000L
        }
        val cleaned = normalized.map { PollOption(id = it.id, text = it.text) }

        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val res = repo.updatePoll(
                pollId = pollId,
                title = s.title.trim(),
                description = s.description.trim(),
                options = cleaned,
                closesAtMillis = closesAtMillis,
                isActive = s.isActive
            )) {
                is OpResult.Success -> _state.value = _state.value.copy(loading = false, savedId = pollId)
                is OpResult.Error   -> _state.value = _state.value.copy(loading = false, error = res.message)
            }
        }
    }

    private fun updateOption(index: Int, transform: (PollEditorOption) -> PollEditorOption) {
        val list = _state.value.options.toMutableList()
        if (index in list.indices) {
            list[index] = transform(list[index])
            _state.value = _state.value.copy(options = list, error = null)
        }
    }
}
