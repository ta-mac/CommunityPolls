package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.poll.CreatePollResult
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.model.PollOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PollEditorOption(
    val id: String = "",
    val text: String = ""
)

data class PollEditorUiState(
    val title: String = "",
    val description: String = "",
    val options: List<PollEditorOption> = listOf(PollEditorOption(), PollEditorOption()),
    val isActive: Boolean = true,
    val closesAt: Long? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class PollEditorViewModel(
    private val repo: PollRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PollEditorUiState())
    val state: StateFlow<PollEditorUiState> = _state.asStateFlow()

    fun setTitle(value: String) {
        _state.value = _state.value.copy(title = value, error = null)
    }

    fun setDescription(value: String) {
        _state.value = _state.value.copy(description = value, error = null)
    }

    fun setOptionId(index: Int, value: String) = updateOption(index) { it.copy(id = value) }

    fun setOptionText(index: Int, value: String) = updateOption(index) { it.copy(text = value) }

    fun addOption() {
        _state.value = _state.value.copy(options = _state.value.options + PollEditorOption(), error = null)
    }

    fun removeOption(index: Int) {
        val list = _state.value.options.toMutableList()
        if (index in list.indices && list.size > 2) {
            list.removeAt(index)
            _state.value = _state.value.copy(options = list, error = null)
        } else {
            _state.value = _state.value.copy(error = "A poll must have at least two options.")
        }
    }

    fun setActive(active: Boolean) {
        _state.value = _state.value.copy(isActive = active, error = null)
    }

    /** hours = null means no closing time */
    fun setCloseAfterHours(hours: Long?) {
        val closes = if (hours == null) null else System.currentTimeMillis() + hours * 60L * 60L * 1000L
        _state.value = _state.value.copy(closesAt = closes, error = null)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun save(createdByUid: String) {
        val s = _state.value
        val trimmedTitle = s.title.trim()
        if (trimmedTitle.isEmpty()) {
            _state.value = s.copy(error = "Title is required.")
            return
        }
        val cleaned = s.options.map { PollEditorOption(it.id.trim(), it.text.trim()) }
        if (cleaned.size < 2 || cleaned.any { it.id.isEmpty() || it.text.isEmpty() }) {
            _state.value = s.copy(error = "Please provide at least 2 options with id and text.")
            return
        }

        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            val result = repo.createPoll(
                title = trimmedTitle,
                description = s.description.trim(),
                options = cleaned.map { PollOption(id = it.id, text = it.text) },
                createdByUid = createdByUid,
                closesAtMillis = s.closesAt,
                isActive = s.isActive
            )
            when (result) {
                is CreatePollResult.Success ->
                    _state.value = _state.value.copy(loading = false, error = null)
                is CreatePollResult.Error ->
                    _state.value = _state.value.copy(loading = false, error = result.message)
            }
        }
    }

    // --- helper to update a single option in-place ---
    private fun updateOption(index: Int, transform: (PollEditorOption) -> PollEditorOption) {
        val list = _state.value.options.toMutableList()
        if (index in list.indices) {
            list[index] = transform(list[index])
            _state.value = _state.value.copy(options = list, error = null)
        }
    }
}
